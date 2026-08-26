package com.fintech.eod.service;


import com.fintech.eod.dto.InterestRangeDTO;
import com.fintech.eod.listener.InterestJobListener;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class InterestRateService {

    public BigDecimal getRateForBalance(Integer groupId, BigDecimal balance) {
        List<InterestRangeDTO> ranges = InterestJobListener.rangeCache.get(groupId);

        if (ranges == null || ranges.isEmpty()) return BigDecimal.ZERO;

        // Buscamos el rango donde encaja el saldo (balance >= min Y balance < max)
        return ranges.stream()
                .filter(r -> balance.compareTo(r.minAmount()) >= 0
                        && balance.compareTo(r.maxAmount()) < 0)
                .map(InterestRangeDTO::rateValue)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }
}
