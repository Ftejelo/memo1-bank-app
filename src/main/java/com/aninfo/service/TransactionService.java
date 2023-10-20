package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.repository.AccountRepository;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Transaction createWithdraw(Long cbu, Double amount) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        return getTransaction(account, amount, "W");
    }

    public Transaction createDeposit(Long cbu, Double amount) {

        if (amount <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }
        Account account = accountRepository.findAccountByCbu(cbu);
        return getTransaction(account, amount, "D");
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getAllTransactionsFromAccount(Long cbu) {
        Account accountFound = accountRepository.findAccountByCbu(cbu);
        return accountFound.getTransactions();
    }

    private Transaction getTransaction(Account account, Double amount, String transType) {

        Transaction transaction = new Transaction();
        transaction.setTransactionType(transType);
        transaction.setAccount(account);
        transaction.setAmount(amount);
        account.addTransaction(transaction);
        transactionRepository.save(transaction);

        return transaction;
    }
}
