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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        if ("PARENT".equals(user.getRole())) {
            List<User> familyMembers = userRepository.findByFamilyId(user.getFamilyId());
            List<Long> familyUserIds = familyMembers.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            return transactionRepository.findByUserIdIn(familyUserIds);
        } else {
            return transactionRepository.findByUserId(user.getId());
        }
    }

    public Transaction getTransactionById(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if ("PARENT".equals(user.getRole())) {
            List<User> familyMembers = userRepository.findByFamilyId(user.getFamilyId());
            boolean isFamily = familyMembers.stream().anyMatch(m -> m.getId().equals(transaction.getUserId()));
            if (!isFamily) {
                throw new RuntimeException("Unauthorized access to transaction");
            }
        } else {
            if (!transaction.getUserId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access to transaction");
            }
        }
        return transaction;
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

        Transaction transaction = new Transaction();
        transaction.setUserId(user.getId());
        transaction.setWallet(wallet);
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(request.getType());

        if (request.getTransactionDate() != null && !request.getTransactionDate().isEmpty()) {
            transaction.setTransactionDate(LocalDateTime.parse(request.getTransactionDate()));
        } else {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        if ("TRANSFER".equalsIgnoreCase(request.getType())) {
            if (request.getTargetWalletId() == null) {
                throw new RuntimeException("Target Wallet required");
            }
            Wallet targetWallet = walletRepository.findById(request.getTargetWalletId())
                    .orElseThrow(() -> new RuntimeException("Target Wallet not found"));

            if (wallet.getId().equals(targetWallet.getId())) {
                throw new RuntimeException("Cannot transfer to same wallet");
            }

            transaction.setTargetWallet(targetWallet);
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
            targetWallet.setBalance(targetWallet.getBalance().add(request.getAmount()));
            walletRepository.save(targetWallet);

        } else if ("EXPENSE".equalsIgnoreCase(request.getType())) {
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        } else {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        }

        walletRepository.save(wallet);
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

        if ("TRANSFER".equalsIgnoreCase(transaction.getTransactionType())) {
            Wallet targetWallet = transaction.getTargetWallet();
            if (targetWallet != null) {
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                targetWallet.setBalance(targetWallet.getBalance().subtract(transaction.getAmount()));
                walletRepository.save(targetWallet);
            }
        } else if ("EXPENSE".equalsIgnoreCase(transaction.getTransactionType())) {
            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
        } else if ("INCOME".equalsIgnoreCase(transaction.getTransactionType())) {
            wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
        }
        walletRepository.save(wallet);

        transactionRepository.delete(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = getTransactionById(id);
        Wallet oldWallet = transaction.getWallet();
        Wallet oldTargetWallet = transaction.getTargetWallet();
        java.math.BigDecimal oldAmount = transaction.getAmount();
        String oldType = transaction.getTransactionType();

        if ("TRANSFER".equalsIgnoreCase(oldType)) {
            oldWallet.setBalance(oldWallet.getBalance().add(oldAmount));
            if (oldTargetWallet != null) {
                oldTargetWallet.setBalance(oldTargetWallet.getBalance().subtract(oldAmount));
                walletRepository.save(oldTargetWallet);
            }
        } else if ("EXPENSE".equalsIgnoreCase(oldType)) {
            oldWallet.setBalance(oldWallet.getBalance().add(oldAmount));
        } else {
            oldWallet.setBalance(oldWallet.getBalance().subtract(oldAmount));
        }
        walletRepository.save(oldWallet);

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

        if ("TRANSFER".equalsIgnoreCase(request.getType())) {
            if (request.getTargetWalletId() == null) {
                throw new RuntimeException("Target Wallet required");
            }
            Wallet newTargetWallet = walletRepository.findById(request.getTargetWalletId())
                    .orElseThrow(() -> new RuntimeException("Target Wallet not found"));

            transaction.setTargetWallet(newTargetWallet);
            newWallet.setBalance(newWallet.getBalance().subtract(request.getAmount()));
            newTargetWallet.setBalance(newTargetWallet.getBalance().add(request.getAmount()));
            walletRepository.save(newTargetWallet);
        } else {
            transaction.setTargetWallet(null);
            if ("EXPENSE".equalsIgnoreCase(request.getType())) {
                newWallet.setBalance(newWallet.getBalance().subtract(request.getAmount()));
            } else {
                newWallet.setBalance(newWallet.getBalance().add(request.getAmount()));
            }
        }
        walletRepository.save(newWallet);

        return transactionRepository.save(transaction);
    }
}