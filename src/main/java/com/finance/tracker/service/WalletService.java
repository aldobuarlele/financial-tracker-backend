package com.finance.tracker.service;

import com.finance.tracker.model.Wallet;
import com.finance.tracker.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public List<Wallet> getAllMyWallets() {
        Long dummyUserId = 1L;
        return walletRepository.findByUserId(dummyUserId);
    }

    public Wallet createWallet(Wallet wallet) {
        wallet.setUserId(1L);
        return walletRepository.save(wallet);
    }
}
