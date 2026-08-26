package com.fintech.eod.processor;


import com.fintech.core.accounts.domain.AccountEntity;
import com.fintech.eod.service.ControlService;
import com.fintech.eod.service.InterestCalculationService;
import com.fintech.eod.service.InterestRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InterestProcessor implements ItemProcessor<AccountEntity, AccountEntity>, org.springframework.batch.core.StepExecutionListener{

    private final InterestRateService interestRateService;
    private final InterestCalculationService interestCalculationService;
    private final ControlService controlService; // Inyectamos el servicio de control
    private static final BigDecimal DAYS_IN_YEAR = new BigDecimal("360");
    // Variables para guardar el estado del control una sola vez por Job
    private boolean isMonthEnd;
    private LocalDate businessDate;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // Leemos el control UNA SOLA VEZ antes de que empiecen las 500k cuentas
        var control = controlService.getControlEntity();
        this.isMonthEnd = control.isMonthEnd();
        this.businessDate = control.getBusinessDate();
        System.out.println(">>> [Processor] Iniciando Step. ¿Es fin de mes?: " + isMonthEnd);
    }

    @Override
    public AccountEntity process(AccountEntity account) {

        return interestCalculationService.calculate(account, isMonthEnd);
    }

}
