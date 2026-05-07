package com.homeservices.user.ejb;

import jakarta.ejb.Stateful;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.homeservices.user.entity.User;
import com.homeservices.user.entity.Transaction;
import com.homeservices.user.repository.UserRepository;
import com.homeservices.user.repository.TransactionRepository;
import java.time.LocalDateTime;

@Stateful
@Component
@Getter
public class WalletBean {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Double tempDeductedAmount = 0.0;
    private Long tempUserId = null;
    private Long tempBookingId = null;

    public boolean validateBalance(Long userId, Double amount) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        return user.getWalletBalance() >= amount;
    }

    public void holdAmount(Long userId, Double amount, Long bookingId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (user.getWalletBalance() < amount) {
            throw new Exception("Insufficient balance");
        }

        this.tempDeductedAmount = amount;
        this.tempUserId = userId;
        this.tempBookingId = bookingId;

        user.setWalletBalance(user.getWalletBalance() - amount);
        userRepository.save(user);
    }

    public void confirmDeduction() throws Exception {
        if (tempUserId != null && tempDeductedAmount > 0) {
            Transaction transaction = new Transaction();
            transaction.setUserId(tempUserId);
            transaction.setAmount(tempDeductedAmount);
            transaction.setTransactionType("BOOKING");
            transaction.setBookingId(tempBookingId);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setStatus("SUCCESS");

            transactionRepository.save(transaction);

            tempDeductedAmount = 0.0;
            tempUserId = null;
            tempBookingId = null;
        }
    }

    public void rollbackDeduction() throws Exception {
        if (tempUserId != null && tempDeductedAmount > 0) {
            User user = userRepository.findById(tempUserId)
                    .orElseThrow(() -> new Exception("User not found"));

            user.setWalletBalance(user.getWalletBalance() + tempDeductedAmount);
            userRepository.save(user);

            Transaction transaction = new Transaction();
            transaction.setUserId(tempUserId);
            transaction.setAmount(tempDeductedAmount);
            transaction.setTransactionType("BOOKING");
            transaction.setBookingId(tempBookingId);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setStatus("FAILED");

            transactionRepository.save(transaction);

            tempDeductedAmount = 0.0;
            tempUserId = null;
            tempBookingId = null;
        }
    }

    public void addFunds(Long userId, Double amount) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        user.setWalletBalance(user.getWalletBalance() + amount);
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setTransactionType("ADD_FUNDS");
        transaction.setBookingId(0L);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");
        transactionRepository.save(transaction);
    }

    public Double getBalance(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        return user.getWalletBalance();
    }
}
