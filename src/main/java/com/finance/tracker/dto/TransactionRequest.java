package com.finance.tracker.dto;

import java.math.BigDecimal;

public class TransactionRequest {

    private Long walletId;
    private BigDecimal amount;
    private String description;
    private String type; // "INCOME" atau "EXPENSE"
    private Long categoryId;

    // ==========================================
    // MANUAL GETTERS & SETTERS (Tanpa Lombok)
    // ==========================================

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
