package com.fintech.eod.service;

import com.fintech.core.accounts.domain.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterestCalculationService {

    private final InterestRateService interestRateService;

    public AccountEntity calculate(AccountEntity account, boolean isMonthEnd) {

        BigDecimal baseBalance = account.getBalanceToday();
        BigDecimal rate = Optional.ofNullable(
                interestRateService.getRateForBalance(account.getSubproduct().getInterestGroupIdValue(), baseBalance)
        ).orElse(BigDecimal.ZERO);

        if (baseBalance.compareTo(BigDecimal.ZERO) <= 0 || rate.compareTo(BigDecimal.ZERO) <= 0) {
            account.setTempRate(rate);
            account.setTempDailyInterest(BigDecimal.ZERO);
            account.setTempRemainderBefore(account.getInterestRemainder() != null ? account.getInterestRemainder() : BigDecimal.ZERO);
            account.setTempAccrued(BigDecimal.ZERO);
            return account;
        }

        BigDecimal dailyInterest = baseBalance.multiply(rate)
                .divide(BigDecimal.valueOf(360), 10, RoundingMode.HALF_UP);

        BigDecimal totalInterestToHandle = dailyInterest.add(account.getInterestRemainder());
        BigDecimal toAccrue = totalInterestToHandle.setScale(2, RoundingMode.DOWN);
        BigDecimal newRemainder = totalInterestToHandle.subtract(toAccrue);

        account.setInterestRemainder(newRemainder);
        account.setTempRate(rate);
        account.setTempDailyInterest(dailyInterest);
        account.setTempRemainderBefore(account.getInterestRemainder());
        account.setTempAccrued(toAccrue);
        account.setTempGrossInterest(totalInterestToHandle);
        account.setAccruedInterestMonth(account.getAccruedInterestMonth().add(toAccrue));

        if (isMonthEnd && account.shouldPayInterest()) {
            int currentMonths = account.getMonthsAccumulated() + 1;
            account.setMonthsAccumulated(currentMonths);
            account.setAmountToPay(account.getAccruedInterestMonth());
            account.setAccruedInterestMonth(BigDecimal.ZERO);
        }

        return account;
    }
}
