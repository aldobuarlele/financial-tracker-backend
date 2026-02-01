package com.finance.tracker.service;

import com.finance.tracker.dto.TransactionRequest;
import com.finance.tracker.model.Category;
import com.finance.tracker.model.Transaction;
import com.finance.tracker.model.User;
import com.finance.tracker.model.Wallet;
import com.finance.tracker.repository.CategoryRepository;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Transaction> getAllTransactions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUserIdOrderByTransactionDateDesc(user.getId());
    }

    @Transactional
    public Transaction createTransaction(TransactionRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElse(null);
        }

        if ("EXPENSE".equalsIgnoreCase(request.getType())) {
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        } else if ("INCOME".equalsIgnoreCase(request.getType())) {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        }
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        if (request.getTransactionDate() != null && !request.getTransactionDate().isEmpty()) {
            transaction.setTransactionDate(LocalDateTime.parse(request.getTransactionDate()));
        } else {
            transaction.setTransactionDate(LocalDateTime.now());
        }
        transaction.setWallet(wallet);
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(request.getType());
        transaction.setUserId(user.getId());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this transaction");
        }

        Wallet wallet = transaction.getWallet();
        if ("EXPENSE".equalsIgnoreCase(transaction.getTransactionType())) {
            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
        } else if ("INCOME".equalsIgnoreCase(transaction.getTransactionType())) {
            wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
        }
        walletRepository.save(wallet);
        transactionRepository.delete(transaction);
    }

    public Transaction getTransactionById(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return transaction;
    }

    @Transactional
    public Transaction updateTransaction(Long id, TransactionRequest request) {
        // A. Ambil Data Lama
        Transaction transaction = getTransactionById(id); // Sudah sekalian cek user auth
        Wallet oldWallet = transaction.getWallet();
        BigDecimal oldAmount = transaction.getAmount();
        String oldType = transaction.getTransactionType();

        // B. KEMBALIKAN SALDO LAMA (Revert Balance)
        if ("EXPENSE".equalsIgnoreCase(oldType)) {
            oldWallet.setBalance(oldWallet.getBalance().add(oldAmount)); // Uang dikembalikan
        } else {
            oldWallet.setBalance(oldWallet.getBalance().subtract(oldAmount)); // Saldo ditarik kembali
        }
        walletRepository.save(oldWallet);

        // C. Update Data Baru
        Wallet newWallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("New Wallet not found"));

        Category newCategory = null;
        if (request.getCategoryId() != null) {
            newCategory = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }

        transaction.setWallet(newWallet);
        transaction.setCategory(newCategory);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(request.getType());

        if (request.getTransactionDate() != null && !request.getTransactionDate().isEmpty()) {
            transaction.setTransactionDate(LocalDateTime.parse(request.getTransactionDate()));
        }

        // D. POTONG SALDO BARU (Apply New Balance)
        if ("EXPENSE".equalsIgnoreCase(request.getType())) {
            newWallet.setBalance(newWallet.getBalance().subtract(request.getAmount()));
        } else {
            newWallet.setBalance(newWallet.getBalance().add(request.getAmount()));
        }
        walletRepository.save(newWallet);

        return transactionRepository.save(transaction);
    }
}