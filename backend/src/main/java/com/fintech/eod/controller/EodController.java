package com.fintech.eod.controller;

import com.fintech.eod.service.ControlService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/eod")
public class EodController {

    private final JobLauncher jobLauncher;
    //private final Job preparationJob;
    private final Job masterEodJob;
    private final ControlService controlService;

    public EodController(JobLauncher jobLauncher, Job masterEodJob, ControlService controlService) {
        this.jobLauncher = jobLauncher;
        //this.preparationJob = preparationJob;
        this.masterEodJob = masterEodJob;
        this.controlService = controlService;
    }

    @PostMapping("/run-process")
    public ResponseEntity<String> runEodProcess() throws Exception {

        // 1. Fail-Fast: Validamos que el sistema esté apto para cerrar (evita doble ejecución)
        controlService.validateSystemReadyForClosing();

        // 2. Armamos parámetros con timestamp único para permitir reintentos en metadatos batch
        JobParameters params = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();

        // 3. Lanzamos el flujo completo secuencial: Bloqueo -> Intereses -> Apertura
        jobLauncher.run(masterEodJob, params);

        return ResponseEntity.ok("Proceso de Fin de Día (EoD) ejecutado exitosamente.");
    }
}
