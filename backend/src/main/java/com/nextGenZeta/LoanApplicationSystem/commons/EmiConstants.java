package com.nextGenZeta.LoanApplicationSystem.commons;

public class EmiConstants {
    public static final String EMI_BASE = "/api/v1/emi";
    public static final String EMI_PREVIEW = "/preview";
    public static final String EMI_CREATE_SCHEDULE = "/loans/{loanId}/schedule";
    public static final String EMI_GET_SCHEDULE = "/loans/{loanId}/schedule";
    public static final String EMI_UPDATE_PAYMENT = "/loans/{loanId}/payments";

    public static final String ERR_LOAN_AMOUNT_POSITIVE = "Loan amount must be positive";
    public static final String ERR_INTEREST_RATE_POSITIVE = "Interest rate must be positive";
    public static final String ERR_TENURE_MIN = "Tenure must be at least 1 month";
    public static final String ERR_NO_SCHEDULE_FOUND = "No repayment schedule found for loanId: ";
    public static final String ERR_REPAYMENT_NOT_FOUND = "Repayment schedule not found";

    public static final double HUNDRED = 100.0;
    public static final double INTEREST_RATE = 7.5;
}