package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "/tenmo")
public class TenmoController {

    private UserDao userDao;
    private AccountDao accountDao;
    private TransferDao transferDao;
    private TransferStatusDao transferStatusDao;
    private TransferTypeDao transferTypeDao;

    public TenmoController(UserDao userDao, AccountDao accountDao, TransferDao transferDao, TransferStatusDao transferStatusDao, TransferTypeDao transferTypeDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.transferStatusDao = transferStatusDao;
        this.transferTypeDao = transferTypeDao;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public Balance getBalance(Principal principal) {
        return accountDao.getBalance(principal.getName());
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
    public User getUserByUserId(@PathVariable long id) {
        return userDao.findByUserID(id);
    }

    @RequestMapping(path = "/account/{id}", method = RequestMethod.GET)
    public Account getAccountByAccountId(@PathVariable long id) {
        return accountDao.getAccountWithAccountId(id);
    }

    @RequestMapping(path = "/account/users/{id}", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable long id) {
        return accountDao.getAccountWithUserId(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public void transfer(@RequestBody Transfer transfer) {
        transferDao.transfer(transfer);
    }

    @RequestMapping(path = "/transfer/approval", method = RequestMethod.PUT)
    public void approveTransfer(@RequestBody Transfer transfer) {
        transferDao.approveTransfer(transfer);
    }
   
    @RequestMapping(path = "/transfer/rejection", method = RequestMethod.PUT)
    public void rejectTransfer(@RequestBody Transfer transfer) {
        transferDao.rejectTransfer(transfer);
    }


    @RequestMapping(path = "/transfer/view/{id}", method = RequestMethod.GET)
    public List<Transfer> viewAllTransfersByAccountId(@PathVariable("id") Long accountId) {
        return transferDao.viewAllTransfersByAccountId(accountId);
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public Transfer viewTransferByTransferId(@PathVariable("id") Long transferId) {
        return transferDao.viewTransferByTransferId(transferId);
    }

    @RequestMapping(path = "/transfer/status/{id}", method = RequestMethod.GET)
    public TransferStatus viewTransferStatusByTransferStatusId(@PathVariable("id") Long transferStatusId) {
        return transferStatusDao.viewTransferStatusByTransferStatusId(transferStatusId);
    }

    @RequestMapping(path = "/transfer/type/{id}", method = RequestMethod.GET)
    public TransferType viewTransferTypeByTransferTypeId(@PathVariable("id") Long transferTypeId) {
        return transferTypeDao.viewTransferTypeByTransferTypeId(transferTypeId);
    }
}
