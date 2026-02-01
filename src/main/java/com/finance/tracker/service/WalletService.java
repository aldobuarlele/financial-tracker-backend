package com.finance.tracker.service;

import com.finance.tracker.dto.WalletRequest;
import com.finance.tracker.model.User;
import com.finance.tracker.model.Wallet;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Wallet> getAllMyWallets() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return walletRepository.findByUserId(user.getId());
    }

    public Wallet createWallet(WalletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = new Wallet();
        wallet.setUserId(user.getId());
        wallet.setWalletName(request.getWalletName());
        wallet.setWalletType(request.getWalletType());
        wallet.setBalance(request.getBalance());

        return walletRepository.save(wallet);
    }
}
