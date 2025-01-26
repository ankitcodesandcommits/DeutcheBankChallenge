package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferRequest;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;
  

  @MockBean
  private NotificationService notificationService;
  
  @MockBean
  private AccountsRepository accountRepository;
  
  
  private Account accountFrom;
  private Account accountTo;

  
  @BeforeEach
  public void setUp() {
	  Account rootAccount1 = new Account(Account.DEFAULT_ACCOUNT);
	  Account rootAccount2 = new Account(Account.DEFAULT_ACCOUNT);
      accountFrom = new Account(rootAccount1, new BigDecimal("600.0"));
      accountTo = new Account(rootAccount2, new BigDecimal("500.0"));
  }
  
//Test Case for Successful transfer
  @Test
  public void testSuccessfulTransfer() {
	// Arrange: Mock repository responses
	    Mockito.when(accountRepository.findById(accountFrom)).thenReturn(Optional.of(accountFrom));
	    Mockito.when(accountRepository.findById(accountTo)).thenReturn(Optional.of(accountTo));

	    // Create transfer request
	    TransferRequest transferRequest = new TransferRequest();
	    transferRequest.setAccountFromId(accountFrom);
	    transferRequest.setAccountToId(accountTo);
	    transferRequest.setAmount(new BigDecimal("400"));

	    // Act: Perform the transfer
	    accountsService.transferMoney(transferRequest);

	    // Assert: Verify balances
	    assertEquals(new BigDecimal("200.0"), accountFrom.getBalance());
	    assertEquals(new BigDecimal("900.0"), accountTo.getBalance());

		
		 // Assert: Verify notifications //
		 Mockito.verify(notificationService).notifyAboutTransfer(
		  accountFrom.getAccountId(), "Transferred " + transferRequest.getAmount() + " to account " + accountTo.getAccountId());
		 //,accountTo.getAccountId()) );
		 Mockito.verify(notificationService).notifyAboutTransfer(
		 accountTo.getAccountId(),  "Received " + transferRequest.getAmount() + " from account " + accountFrom.getAccountId()
		 //,accountFrom.getAccountId())
		 
		 );
		 
  }

//Test Case for to check Insufficient Funds
  
  @Test
  public void testInsufficientFunds() {
      Mockito.when(accountRepository.findById(accountFrom)).thenReturn(Optional.of(accountFrom));
      Mockito.when(accountRepository.findById(accountTo)).thenReturn(Optional.of(accountTo));

      TransferRequest transferRequest = new TransferRequest();
      transferRequest.setAccountFromId(accountFrom);
      transferRequest.setAccountToId(accountTo);
      transferRequest.setAmount(new BigDecimal("100"));

     assertThrows(IllegalStateException.class, () -> accountsService.transferMoney(transferRequest));
  }


  @Test
  void addAccount() {
//    Account account = new Account(new Account(Account.DEFAULT_ACCOUNT, new BigDecimal("200.0")));
	  Account account = new Account(Account.DEFAULT_ACCOUNT);
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    Account uniqueId = new Account(null, new BigDecimal("500.0"));
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }
}
