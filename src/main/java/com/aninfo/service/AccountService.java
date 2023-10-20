package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.model.Account;
import com.aninfo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        accountRepository.deleteById(cbu);
    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - sum);
        accountRepository.save(account);

        return account;
    }

    @Transactional
    public Account deposit(Long cbu, Double sum) {

        if (sum <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }
        Account account = accountRepository.findAccountByCbu(cbu);

        if (sum >= Promo.MIN_THRESHOLD) {
            applyPromo(sum, account);
        }
        account.setBalance(account.getBalance() + sum);
        accountRepository.save(account);

        return account;
    }

    public void applyPromo(Double depositAmount, Account account) {
        double promoAmount = depositAmount * Promo.MULTIPLIER;
        if (promoToApplyLowerThanCap(promoAmount, account.getCurrentPromoGiven())) {
            account.setCurrentPromoGiven(promoAmount);
            account.setBalance(account.getBalance() + promoAmount);
        } else {
            account.setBalance(account.getBalance() + (Promo.MAX_AMOUNT - account.getCurrentPromoGiven()));
        }
    }

    private boolean promoToApplyLowerThanCap(Double promoAmount, Double currentPromoGiven) {
        return promoAmount < currentPromoGiven + Promo.MAX_AMOUNT;
    }

}
