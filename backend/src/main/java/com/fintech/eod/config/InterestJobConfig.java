package com.fintech.eod.config;


import com.fintech.core.accounts.domain.AccountEntity;
import com.fintech.eod.listener.InterestJobListener;
import com.fintech.eod.processor.InterestProcessor;
import com.fintech.eod.service.ControlService;
import com.fintech.management.subproducts.domain.SubproductEntity;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class InterestJobConfig {

    private final DataSource dataSource;
    private final ControlService controlService;

    public InterestJobConfig(DataSource dataSource, ControlService controlService) {
        this.dataSource = dataSource;
        this.controlService = controlService;
    }

    @Bean
    @StepScope // <--- CRÍTICO: El bean se reconstruye en cada ejecución
    public JdbcPagingItemReader<AccountEntity> interestReader() {
        // Obtenemos la fecha directamente de la tabla control_system
        LocalDate beforeDate = controlService.getBeforeBusinessDate();

        JdbcPagingItemReader<AccountEntity> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(1000); // Tamaño de la página para los hilos

        // Mapeamos los resultados a nuestra entidad
        reader.setRowMapper((rs, rowNum) -> {
            AccountEntity account = new AccountEntity();
            account.setId(rs.getLong("id"));
            account.setAccountNumber(rs.getString("account_number"));
            account.setBalanceToday(rs.getBigDecimal("balance_today"));
            account.setInterestRemainder(rs.getBigDecimal("interest_remainder"));
            account.setAccruedInterestMonth(rs.getBigDecimal("accrued_interest_month"));
            // ESTAS LÍNEAS SON LAS QUE FALTAN:
            account.setAvailableBalance(rs.getBigDecimal("available_balance"));
            account.setAmountHold(rs.getBigDecimal("amount_hold"));
            account.setAmountNcToday(rs.getBigDecimal("amount_nc_today"));
            account.setAmountNdToday(rs.getBigDecimal("amount_nd_today"));
            // Inyectamos el interest_group_id dentro del objeto subproducto de la cuenta
            // para que el Processor sepa qué tasa aplicar sin ir a la DB.
            SubproductEntity sub = new SubproductEntity();
            sub.setInterestGroupId(rs.getInt("interest_group_id"));
            account.setPeriodicity(rs.getInt("periodicity"));
            account.setMonthsAccumulated(rs.getInt("months_accumulated"));
            account.setSubproduct(sub);
            return account;
        });

        // CAMBIO: Usamos el QueryProvider específico para evitar que Spring Batch lo genere mal
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, account_number, balance_today, interest_remainder, accrued_interest_month, available_balance, amount_hold, interest_group_id, periodicity, months_accumulated, amount_nc_today, amount_nd_today");
        // Simplificamos el FROM para que no haya dudas con los alias
        queryProvider.setFromClause("FROM (SELECT a.*, s.interest_group_id FROM accounts a INNER JOIN subproduct s ON a.subproduct_id = s.id) AS data");
        // Quitamos los alias "a." del WHERE ya que están dentro del subquery
        queryProvider.setWhereClause("WHERE status_id = 58 AND generates_interest = TRUE AND last_processed_date = :processDate");
        // 4. SORT KEY (EL CAMBIO CRÍTICO):
        // Usamos el alias 'a.id' para que la base de datos sepa exactamente qué ID usar para paginar
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        try {
            reader.setQueryProvider(queryProvider);
        } catch (Exception e) {
            throw new RuntimeException("Error al configurar el Reader de Intereses", e);
        }

        // PASO FUNDAMENTAL: Pasar el valor del parametro a processDate
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("processDate", java.sql.Date.valueOf(beforeDate));
        reader.setParameterValues(parameters);

        return reader;
    }

    @Bean
    @StepScope // <--- OBLIGATORIO para usar #{jobExecutionContext}
    public JdbcBatchItemWriter<AccountEntity> accountUpdateWriter(
            DataSource dataSource,
            @Value("#{jobExecutionContext['CONST_BUSINESS_DATE']}") String businessDateStr) {

        return new JdbcBatchItemWriterBuilder<AccountEntity>()
                .dataSource(dataSource)
                .sql("UPDATE accounts SET " +
                        "balance_yesterday = balance_today, " +
                        "available_balance = available_balance + COALESCE(:amountToPay, 0), " +
                        "balance_today = balance_today + COALESCE(:amountToPay, 0), " +
                        "accrued_interest_month = :accruedInterestMonth, " +
                        "interest_remainder = :interestRemainder, " +
                        "amount_nd_yesterday = :amountNdToday, " +
                        "amount_nc_yesterday = :amountNcToday, " +
                        "amount_nd_today = 0, " +
                        "amount_nc_today = 0, " +
                        "months_accumulated = :monthsAccumulated, " +
                        "last_processed_date = CAST(:businessDate AS DATE), " + // Usamos la fecha del contexto
                        "last_date_nc = CASE WHEN COALESCE(:amountToPay, 0) > 0 THEN CURRENT_TIMESTAMP ELSE last_date_nc END, " +
                        "version = version + 1, " +
                        "updated_at = CURRENT_TIMESTAMP " +
                        "WHERE id = :id")
                .itemSqlParameterSourceProvider(item -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    // Parámetros del objeto AccountEntity
                    params.addValue("id", item.getId());
                    params.addValue("amountToPay", item.getAmountToPay());
                    params.addValue("accruedInterestMonth", item.getAccruedInterestMonth());
                    params.addValue("interestRemainder", item.getInterestRemainder());
                    params.addValue("amountNdToday", item.getAmountNdToday());
                    params.addValue("amountNcToday", item.getAmountNcToday());
                    params.addValue("monthsAccumulated", item.getMonthsAccumulated());
                    // Parámetro inyectado del Contexto
                    params.addValue("businessDate", businessDateStr);
                    return params;
                })
                .assertUpdates(false)
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<AccountEntity> interestSnapshotWriter(
            DataSource dataSource,
            @Value("#{jobExecutionContext['CONST_BUSINESS_DATE']}") String businessDate) {

        return new JdbcBatchItemWriterBuilder<AccountEntity>()
                .dataSource(dataSource)
                .sql("INSERT INTO account_balance_snapshot (" +
                        "account_id, snapshot_date, available_balance, balance_today, amount_hold, " +
                        "applied_rate, interest_day, remainder_before, remainder_after, gross_interest, " +
                        "accrued_month_to_date, total_nd_day, total_nc_day, created_at) " +
                        "VALUES (:id, CAST(:businessDate AS DATE), :availableBalance, :balanceToday, :amountHold, " +
                        "COALESCE(:tempRate, 0), " +
                        "COALESCE(:tempDailyInterest, 0), " +
                        "COALESCE(:tempRemainderBefore, 0), " +
                        "COALESCE(:interestRemainder, 0), " +
                        "COALESCE(:tempGrossInterest, 0), " +
                        "COALESCE(:accruedInterestMonth, 0), " +
                        ":amountNdToday, :amountNcToday, CURRENT_TIMESTAMP)")
                .itemSqlParameterSourceProvider(item -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    // Datos del Processor
                    params.addValue("id", item.getId());
                    params.addValue("availableBalance", item.getAvailableBalance());
                    params.addValue("balanceToday", item.getBalanceToday());
                    params.addValue("amountHold", item.getAmountHold());
                    params.addValue("tempRate", item.getTempRate());
                    params.addValue("tempDailyInterest", item.getTempDailyInterest());
                    params.addValue("tempRemainderBefore", item.getTempRemainderBefore());
                    params.addValue("interestRemainder", item.getInterestRemainder());
                    params.addValue("tempGrossInterest", item.getTempGrossInterest());
                    params.addValue("accruedInterestMonth", item.getAccruedInterestMonth());
                    params.addValue("amountNdToday", item.getAmountNdToday());
                    params.addValue("amountNcToday", item.getAmountNcToday());

                    // Dato del Contexto
                    params.addValue("businessDate", businessDate);
                    return params;
                })
                .assertUpdates(false)
                .build();
    }

    @Bean
    @StepScope
    public CompositeItemWriter<AccountEntity> compositeInterestWriter(
            JdbcBatchItemWriter<AccountEntity> accountUpdateWriter,
            JdbcBatchItemWriter<AccountEntity> interestSnapshotWriter,
            JdbcBatchItemWriter<AccountEntity> capitalizationWriter) {

        CompositeItemWriter<AccountEntity> writer = new CompositeItemWriter<>();
        // El orden es importante: primero registramos el evento y luego actualizamos la cuenta
        writer.setDelegates(Arrays.asList(
                interestSnapshotWriter,
                accountUpdateWriter,
                capitalizationWriter
        ));
        return writer;
    }

    @Bean
    public TaskExecutor interestTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);        // 5 hilos iniciales
        executor.setMaxPoolSize(10);       // Máximo 10 hilos si hay mucha carga
        executor.setQueueCapacity(1000);   // Capacidad de la cola de tareas
        executor.setThreadNamePrefix("IntTh-"); // Prefijo para identificar los hilos en el log
        executor.initialize();
        return executor;
    }

    @Bean
    public Step calculateInterestStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcPagingItemReader<AccountEntity> interestReader,
            InterestProcessor interestProcessor,
            CompositeItemWriter<AccountEntity> compositeInterestWriter,
            TaskExecutor interestTaskExecutor) {

        return new StepBuilder("calculateInterestStep", jobRepository)
                .<AccountEntity, AccountEntity>chunk(1000, transactionManager) // Procesa de 1000 en 1000
                .reader(interestReader)
                .processor(interestProcessor)
                .writer(compositeInterestWriter)
                .taskExecutor(interestTaskExecutor) // Aquí activamos el multihilo
                .build();
    }

    @Bean
    public Job interestCalculationJob(
            JobRepository jobRepository,
            Step calculateInterestStep,
            InterestJobListener interestJobListener) {

        return new JobBuilder("interestCalculationJob", jobRepository)
                .listener(interestJobListener) // <--- CARGA LA CACHÉ ANTES DE EMPEZAR
                .start(calculateInterestStep)  // <--- EJECUTA EL PROCESO MULTIHILO
                .build();
    }

    // 3. WRITER 2: CAPITALIZACIÓN (¡EL NUEVO!)
    // Este escribe en la tabla log_mov solo si hay pago
    @Bean
    @StepScope
    public JdbcBatchItemWriter<AccountEntity> capitalizationWriter(
            DataSource dataSource,
            @Value("#{jobExecutionContext['CONST_TR_TYPE_ID']}") Long trTypeId,
            @Value("#{jobExecutionContext['CONST_ORIGIN_ID']}") Long originId,
            @Value("#{jobExecutionContext['CONST_CONFIG_ID']}") Long configId,
            @Value("#{jobExecutionContext['CONST_BUSINESS_DATE']}") String businessDate) {

        return new JdbcBatchItemWriterBuilder<AccountEntity>()
                .dataSource(dataSource)
                .sql("INSERT INTO log_mov (" +
                        "account_id, transaction_type_id, business_date, amount, " +
                        "previous_balance, new_balance, operation_date, description, " +
                        "transaction_reference, origin_id, status, created_by, terminal_ip, config_id" +
                        ") " +
                        "SELECT :id, :trTypeId, CAST(:businessDate AS DATE), :amountToPay, :availableBalance, " +
                        "(:availableBalance + :amountToPay), CURRENT_TIMESTAMP, " +
                        "'INTEREST CAPITALIZATION FREQ ' || :periodicity, " +
                        "'CAP-' || :id || '-' || REPLACE(:businessDate, '-', ''), " +
                        ":originId, 'REGISTERED', 'BATCH_EOD', '127.0.0.1', :configId " +
                        "WHERE :amountToPay > 0") // <--- EL FILTRO DE SEGURIDAD
                .itemSqlParameterSourceProvider(item -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    params.addValue("id", item.getId());
                    params.addValue("amountToPay", item.getAmountToPay() != null ? item.getAmountToPay() : BigDecimal.ZERO);
                    params.addValue("availableBalance", item.getAvailableBalance());
                    params.addValue("periodicity", item.getPeriodicity());

                    params.addValue("trTypeId", trTypeId);
                    params.addValue("originId", originId);
                    params.addValue("configId", configId);
                    params.addValue("businessDate", businessDate);
                    return params;
                })
                .assertUpdates(false)
                .build();
    }

}
