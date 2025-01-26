package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferRequest;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountsService {
	
	private final AccountsRepository accountsRepository;
	  
	  private final NotificationService notificationService;
	
    private final Lock lock = new ReentrantLock();

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
      this.accountsRepository = accountsRepository;
      this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
 
  //Method Added for Transfer Money
  public void transferMoney(TransferRequest transferRequest) {
      
      if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
          throw new IllegalArgumentException("Transfer amount must be positive");
      }
      lock.lock();
      try {
    	  Account accountTo = accountsRepository.findById(transferRequest.getAccountToId())
                  .orElseThrow(() -> new IllegalArgumentException("AccountTo not found"));
          Account accountFrom = accountsRepository.findById(transferRequest.getAccountFromId())
              .orElseThrow(() -> new IllegalArgumentException("AccountFrom not found"));
          
          if (accountFrom.getBalance().compareTo(transferRequest.getAmount()) < 0) { 
        	    throw new IllegalStateException("Insufficient funds in AccountFrom"); 
        	}

        	accountFrom.setBalance(accountFrom.getBalance().subtract(transferRequest.getAmount()));
        	accountTo.setBalance(accountTo.getBalance().add(transferRequest.getAmount()));

          notificationService.notifyAboutTransfer(accountFrom.getAccountId(),
                  "Transferred " + transferRequest.getAmount() + " to account " + accountTo.getAccountId());
          notificationService.notifyAboutTransfer(accountTo.getAccountId(),
                  "Received " + transferRequest.getAmount() + " from account " + accountFrom.getAccountId());

      } finally {
          lock.unlock();
      }
  }

public AccountsRepository getAccountsRepository() {
	return accountsRepository;
}

public NotificationService getNotificationService() {
	return notificationService;
}

public Lock getLock() {
	return lock;
}

}
