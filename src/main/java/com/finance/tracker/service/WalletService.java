package com.finance.tracker.service;

import com.finance.tracker.model.Wallet;
import com.finance.tracker.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    @Autowired // Dependency Injection: Minta Spring isikan Repository ini
    private WalletRepository walletRepository;

    // Mengambil semua dompet milik user (User ID 1)
    public List<Wallet> getAllMyWallets() {
        Long dummyUserId = 1L;
        return walletRepository.findByUserId(dummyUserId);
    }

    // Membuat dompet baru
    public Wallet createWallet(Wallet wallet) {
        // Set default user ID ke 1
        wallet.setUserId(1L);
        return walletRepository.save(wallet);
    }
}
