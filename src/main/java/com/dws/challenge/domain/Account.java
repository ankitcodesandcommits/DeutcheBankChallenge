package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data // Generates getters, setters, equals, hashCode, and toString
@Builder // Builder pattern for object creation
@ToString(onlyExplicitlyIncluded = true) // Customize toString generation
public class Account {
	
	public static final Account DEFAULT_ACCOUNT = new Account(); // Default placeholder account

  

@NotNull
  @NotEmpty
  private final Account accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  public Account(@NotNull @NotEmpty Account accountId) {
    this.accountId = DEFAULT_ACCOUNT;
    this.balance = BigDecimal.ZERO;
  }

//  @JsonCreator
//  public Account(@JsonProperty("accountId") @NotNull @NotEmpty Account accountId,
//    @JsonProperty("balance") BigDecimal balance) {
//    this.accountId = accountId;
//    this.balance = balance;
//  }
  
//Private no-argument constructor for the default account
private Account() {
   this.accountId = null; // Default account has no parent
   this.balance = BigDecimal.ZERO; // Default balance is zero
}
  
  // Constructor that takes another Account object
  public Account(Account accountId, BigDecimal balance) {
      this.accountId = (accountId != null) ? accountId : DEFAULT_ACCOUNT; // Use default if null
      this.balance = (balance != null) ? balance : BigDecimal.ZERO; // Handle null balance
  }
  

  @Override
  public String toString() {
      if (this == DEFAULT_ACCOUNT) {
          return "Default Account";
      }
      String parentAccountId = (accountId != null && accountId != DEFAULT_ACCOUNT) 
              ? "ParentAccountID=" + accountId.hashCode() 
              : "NoParent";
      return "Account[" + parentAccountId + ", Balance=" + balance + "]";
  }



}
