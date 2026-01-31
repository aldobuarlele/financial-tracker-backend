package com.finance.tracker.controller;

import com.finance.tracker.model.Wallet;
import com.finance.tracker.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping
    public List<Wallet> getAllWallets() {
        return walletService.getAllMyWallets();
    }

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        Wallet newWallet = walletService.createWallet(wallet);
        return ResponseEntity.ok(newWallet);
    }
}
