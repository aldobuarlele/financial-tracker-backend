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
import java.util.stream.Collectors;

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

        if ("PARENT".equals(user.getRole())) {
            List<User> familyMembers = userRepository.findByFamilyId(user.getFamilyId());
            List<Long> familyUserIds = familyMembers.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            return walletRepository.findByUserIdIn(familyUserIds);
        } else {
            return walletRepository.findByUserId(user.getId());
        }
    }

    public Wallet createWallet(WalletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = new Wallet();
        wallet.setWalletName(request.getWalletName());
        wallet.setWalletType(request.getWalletType());
        wallet.setBalance(request.getBalance());
        wallet.setUserId(user.getId());

        return walletRepository.save(wallet);
    }
}