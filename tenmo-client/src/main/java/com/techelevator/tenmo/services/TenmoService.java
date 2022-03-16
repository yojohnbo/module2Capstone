package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class TenmoService {

    private final String baseUrl = "http://localhost:8080/tenmo";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;


    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public Balance getBalance(AuthenticatedUser authenticatedUser) {
        Balance balance = null;
        try {
            ResponseEntity<Balance> response = restTemplate.exchange(baseUrl + "/balance", HttpMethod.GET, makeAuthEntity(authenticatedUser), Balance.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public User[] getAllUsers(AuthenticatedUser authenticatedUser) {
        User[] users = null;
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(baseUrl + "/users", HttpMethod.GET, makeAuthEntity(authenticatedUser), User[].class);
            users = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    public User getUserByUserId(AuthenticatedUser authenticatedUser, int userId) {
        User user = null;
        try {
            ResponseEntity<User> response = restTemplate.exchange(baseUrl + "/users/" + userId, HttpMethod.GET, makeAuthEntity(authenticatedUser), User.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public void transfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        try {
            restTemplate.exchange(baseUrl + "/transfer", HttpMethod.POST, makeTransferAuthEntity(authenticatedUser, transfer), Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void approveTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        try {
            restTemplate.exchange(baseUrl + "/transfer/approval", HttpMethod.PUT, makeTransferAuthEntity(authenticatedUser, transfer), Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void rejectTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        try {
            restTemplate.exchange(baseUrl + "/transfer/rejection", HttpMethod.PUT, makeTransferAuthEntity(authenticatedUser, transfer), Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public Account getAccountByUserId(AuthenticatedUser authenticatedUser, int userId) {
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "/account/users/" + userId, HttpMethod.GET, makeAuthEntity(authenticatedUser), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public Account getAccountByAccountId(AuthenticatedUser authenticatedUser, int accountId) {
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "/account/" + accountId, HttpMethod.GET, makeAuthEntity(authenticatedUser), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public Transfer[] getAllTransfers(AuthenticatedUser authenticatedUser, int accountId) {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "/transfer/view/" + accountId, HttpMethod.GET, makeAuthEntity(authenticatedUser), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    public Transfer getTransferByTransferId(AuthenticatedUser authenticatedUser, int transferId) {
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "/transfer/" + transferId, HttpMethod.GET, makeAuthEntity(authenticatedUser), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    public TransferStatus getTransferStatusByTransferStatusId (AuthenticatedUser authenticatedUser, int transferStatusId){
        TransferStatus transferStatus = null;
        try {
            ResponseEntity<TransferStatus> response = restTemplate.exchange(baseUrl + "/transfer/status/" + transferStatusId, HttpMethod.GET, makeAuthEntity(authenticatedUser), TransferStatus.class);
            transferStatus = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferStatus;
    }

    public TransferType getTransferTypeByTransferTypeId (AuthenticatedUser authenticatedUser, int transferTypeId){
        TransferType transferType = null;
        try {
            ResponseEntity<TransferType> response = restTemplate.exchange(baseUrl + "/transfer/type/" + transferTypeId, HttpMethod.GET, makeAuthEntity(authenticatedUser), TransferType.class);
            transferType = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferType;
    }

    private HttpEntity makeAuthEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity(headers);
    }

    private HttpEntity<Transfer> makeTransferAuthEntity(AuthenticatedUser authenticatedUser, Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity(transfer, headers);
        return entity;
    }

}
