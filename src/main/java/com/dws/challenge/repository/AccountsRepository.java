package com.dws.challenge.repository;

import java.util.Optional;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);
  
  Optional<Account> findById(Account accountId); // New method

  void clearAccounts();
}
