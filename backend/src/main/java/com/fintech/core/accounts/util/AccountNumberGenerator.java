package com.fintech.core.accounts.util;

import com.fintech.core.accounts.domain.AccountSequence;
import com.fintech.core.accounts.repository.AccountSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final AccountSequenceRepository sequenceRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getNextNumber(String sequenceId) {
        // 1. Incrementamos el valor en la DB
        sequenceRepository.incrementSequence(sequenceId);

        // 2. Obtenemos el nuevo valor actualizado
        AccountSequence seq = sequenceRepository.findById(sequenceId)
                .orElseThrow(() -> new RuntimeException("Sequence not found: " + sequenceId));

        // 3. Formateamos con ceros a la izquierda según el 'length' de la tabla
        return String.format("%s%0" + seq.getLength() + "d",
                seq.getPrefix(),
                seq.getCurrentValue());
    }
}

