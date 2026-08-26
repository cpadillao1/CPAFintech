package com.fintech.eod.config;

import com.fintech.eod.listener.InterestJobListener;
import com.fintech.eod.tasklet.SystemLockTasklet;
import com.fintech.eod.tasklet.SystemUnlockTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MasterEodJobConfig {

    // 1. EL JOB MAESTRO DE FIN DE DÍA (EOD)
    @Bean
    public Job masterEodJob(JobRepository jobRepository,
                            Step lockSystemStep,
                            Step calculateInterestStep, // Inyectamos el step de tu clase InterestJobConfig
                            Step unlockSystemStep,
                            InterestJobListener interestJobListener) {

        return new JobBuilder("MASTER_EOD_JOB", jobRepository)
                .listener(interestJobListener)
                .start(lockSystemStep)           // Paso 1: Bloquea el core (IN_CLOSING)
                .next(calculateInterestStep)     // Paso 2: Ejecuta tu procesamiento masivo de intereses
                .next(unlockSystemStep)          // Paso 3: Avanza las fechas y abre el sistema (ACTIVE)
                .build();
    }

    // 2. STEP DE BLOQUEO (Tasklet inicial)
    @Bean
    public Step lockSystemStep(JobRepository jobRepository,
                               SystemLockTasklet lockTasklet,
                               PlatformTransactionManager transactionManager) {
        return new StepBuilder("lockSystemStep", jobRepository)
                .tasklet(lockTasklet, transactionManager)
                .build();
    }

    // 3. STEP DE APESURA / AVANCE DE FECHAS (Tasklet final)
    @Bean
    public Step unlockSystemStep(JobRepository jobRepository,
                                 SystemUnlockTasklet unlockTasklet,
                                 PlatformTransactionManager transactionManager) {
        return new StepBuilder("unlockSystemStep", jobRepository)
                .tasklet(unlockTasklet, transactionManager)
                .build();
    }
}
