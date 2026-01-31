package com.finance.tracker.dto;

import java.math.BigDecimal;

public class WalletRequest {
    private String walletName;
    private String walletType;
    private BigDecimal balance;

    public String getWalletName() { return walletName; }
    public void setWalletName(String walletName) { this.walletName = walletName; }

    public String getWalletType() { return walletType; }
    public void setWalletType(String walletType) { this.walletType = walletType; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}