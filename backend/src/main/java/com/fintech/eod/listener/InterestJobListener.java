package com.fintech.eod.listener;

import com.fintech.core.transactions.domain.TrConfigEntity;
import com.fintech.core.transactions.repository.TrConfigRepository;
import com.fintech.eod.dto.InterestRangeDTO;
import com.fintech.management.catalog.service.CatalogService;
import com.fintech.management.control_system.service.SystemControlService;
import com.fintech.management.subproducts.domain.InterestRangeEntity;
import com.fintech.management.subproducts.repository.InterestRangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor // Genera el constructor con los campos final
public class InterestJobListener implements JobExecutionListener {

    private final InterestRangeRepository rangeRepository;
    private final CatalogService catalogService; // NUEVO
    private final SystemControlService controlService; // NUEVO
    private final TrConfigRepository trConfigRepository;

    // Mantenemos tu caché de rangos tal cual
    public static Map<Integer, List<InterestRangeDTO>> rangeCache;

    @Override
    public void beforeJob(JobExecution jobExecution) {

        // 1. TU LÓGICA ORIGINAL: Cargar rangos en caché
        List<InterestRangeEntity> allRanges = rangeRepository.findAll();
        rangeCache = allRanges.stream()
                .filter(r -> "ACTIVE".equals(r.getStatus()))
                .collect(Collectors.groupingBy(
                        r -> r.getGroup().getId(),
                        Collectors.mapping(
                                r -> new InterestRangeDTO(r.getMinAmount(), r.getMaxAmount(), r.getRateValue()),
                                Collectors.toList()
                        )
                ));

        // 2. NUEVA LÓGICA: Cargar constantes técnicas para los Writers
        // Buscamos los IDs una sola vez para no pedirlos 500,000 veces en los hilos
        //Long trTypeId = catalogService.getDetailIdByCode("TRANSACTION_TYPE", "CR");
        // 2. NUEVA LÓGICA: Cargar configuración desde TrConfig (INT_PAY)
        TrConfigEntity config = trConfigRepository.findByMnemonic("INT_PAY")
                .orElseThrow(() -> new RuntimeException("Critical Error: That setting does not exist 'INT_PAY' in tr_configs"));

        // Extraemos los valores de la configuración
        Long trTypeId = config.getConfigId();
        //Long trTypeId = config.getTransactionTypeId();

        Long originId = catalogService.getDetailIdByCode("CHANNEL", "BATCH");
        LocalDate businessDate = controlService.getControlEntity().getBusinessDate();

        // 3. GUARDAR EN EL CONTEXTO (Forma segura de pasar datos a los Steps)
        jobExecution.getExecutionContext().putLong("CONST_TR_TYPE_ID", trTypeId);
        jobExecution.getExecutionContext().putLong("CONST_CONFIG_ID", trTypeId);
        jobExecution.getExecutionContext().putLong("CONST_ORIGIN_ID", originId);
        jobExecution.getExecutionContext().putString("CONST_BUSINESS_DATE", businessDate.toString());

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Opcional: Limpiar la caché al terminar para liberar memoria
        if (rangeCache != null) {
            rangeCache.clear();
        }
    }
}