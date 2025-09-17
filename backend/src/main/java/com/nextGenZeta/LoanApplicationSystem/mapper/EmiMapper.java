package com.nextGenZeta.LoanApplicationSystem.mapper;

import com.nextGenZeta.LoanApplicationSystem.controllers.response.RepaymentScheduleDto;
import com.nextGenZeta.LoanApplicationSystem.model.entities.RepaymentSchedule;
import org.springframework.stereotype.Component;

@Component
public class EmiMapper {

    public RepaymentScheduleDto toRepaymentScheduleDto(RepaymentSchedule repaymentSchedule) {
        return RepaymentScheduleDto.builder()
                .id(repaymentSchedule.getId())
                .loanId(repaymentSchedule.getLoanId())
                .monthNumber(repaymentSchedule.getMonthNumber())
                .dueDate(repaymentSchedule.getDueDate())
                .principalAmount(repaymentSchedule.getPrincipalAmount())
                .interestAmount(repaymentSchedule.getInterestAmount())
                .emiAmount(repaymentSchedule.getEmiAmount())
                .balanceRemaining(repaymentSchedule.getBalanceRemaining())
                .paymentStatus(repaymentSchedule.getPaymentStatus())
                .paymentDate(repaymentSchedule.getPaymentDate())
                .build();
    }
}