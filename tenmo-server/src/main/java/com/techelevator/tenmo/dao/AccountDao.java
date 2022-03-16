package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;

public interface AccountDao {

    Balance getBalance(String username);
    Account getAccountWithAccountId(Long accountId);
    Account getAccountWithUserId(Long userId);

}
