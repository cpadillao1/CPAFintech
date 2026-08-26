package com.fintech.eod.tasklet;

import com.fintech.eod.service.ControlService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SystemUnlockTasklet implements Tasklet {

    private final ControlService controlService;

    @Override
    @Transactional
    public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) {
        // Llamamos a la nueva lógica de apertura
        controlService.unlockSystemAndAdvanceDate();
        return RepeatStatus.FINISHED;
    }
}
