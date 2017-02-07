package com.deliveredtechnologies.rulebook;

import java.math.BigDecimal;

/**
 * Created by clong on 2/7/17.
 */
public class ApplicantBean {
    private int creditScore;
    private BigDecimal cashOnHand;

    public ApplicantBean(int creditScore, BigDecimal cashOnHand) {
        this.creditScore = creditScore;
        this.cashOnHand = cashOnHand;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public BigDecimal getCashOnHand() {
        return cashOnHand;
    }

    public void setCashOnHand(BigDecimal cashOnHand) {
        this.cashOnHand = cashOnHand;
    }
}
