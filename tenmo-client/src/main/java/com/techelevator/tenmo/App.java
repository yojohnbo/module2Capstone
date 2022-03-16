package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;
import io.cucumber.java.bs.A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final TenmoService tenmoService = new TenmoService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private boolean end = false;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        while (end == false) {
            loginMenu();
            if (currentUser != null) {
                mainMenu();
            }
        }

    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 0) {
                end = true;
            }
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }


    // TODO make the logout work so you can log back in
    private void handleLogout() {
        currentUser = null;
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
        handleLogout();
    }

    private void viewCurrentBalance() {
        Balance balance = tenmoService.getBalance(currentUser);
        System.out.println("Your current account balance is: $" + balance.getBalance());

    }

    private void viewTransferHistory() {
        Account userAccount = tenmoService.getAccountByUserId(currentUser, currentUser.getUser().getId());
        int userAccountId = userAccount.getAccountId();
        Transfer[] transfers = tenmoService.getAllTransfers(currentUser, userAccountId);

        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID               From/To             Amount");
        System.out.println("-------------------------------------------");
        for (Transfer transfer : transfers) {
            if (transfer.getAccountFrom() == userAccountId) {
                String username = tenmoService.getUserByUserId(currentUser,
                        tenmoService.getAccountByAccountId(currentUser, transfer.getAccountTo()).getUserId()).getUsername();
                System.out.println(transfer.getTransferId() + "              To: " + username + "             $" + transfer.getAmount());
            }
            if (transfer.getAccountTo() == userAccountId) {
                String username = tenmoService.getUserByUserId(currentUser,
                        tenmoService.getAccountByAccountId(currentUser, transfer.getAccountFrom()).getUserId()).getUsername();
                System.out.println(transfer.getTransferId() + "              From: " + username + "           $" + transfer.getAmount());
            }
        }
        int userInput = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        if (userInput == 0) {
            System.out.println("View details cancelled");
        } else {
            Transfer transfer = tenmoService.getTransferByTransferId(currentUser, userInput);
            System.out.println("-------------------------------------------");
            System.out.println("Transfer Details");
            System.out.println("-------------------------------------------");
            System.out.println("ID: " + transfer.getTransferId());
            System.out.println("From: " + tenmoService.getUserByUserId(currentUser,
                    tenmoService.getAccountByAccountId(currentUser, transfer.getAccountFrom()).getUserId()).getUsername());
            System.out.println("To: " + tenmoService.getUserByUserId(currentUser,
                    tenmoService.getAccountByAccountId(currentUser, transfer.getAccountTo()).getUserId()).getUsername());
            System.out.println("Type: " + tenmoService.getTransferTypeByTransferTypeId(currentUser, transfer.getTransferTypeId()).getTransferTypeDescription());
            System.out.println("Status: " + tenmoService.getTransferStatusByTransferStatusId(currentUser, transfer.getTransferStatusId()).getTransferStatusDescription());
            System.out.println("Amount: $" + transfer.getAmount());
        }
    }


    private void viewPendingRequests() {
        Account userAccount = tenmoService.getAccountByUserId(currentUser, currentUser.getUser().getId());
        int userAccountId = userAccount.getAccountId();
        Transfer[] transfers = tenmoService.getAllTransfers(currentUser, userAccountId);

        System.out.println("-------------------------------------------");
        System.out.println("Pending Transfers");
        System.out.println("ID                    To             Amount");
        System.out.println("-------------------------------------------");
        for (Transfer transfer : transfers) {
            if (transfer.getAccountFrom() == userAccountId && transfer.getTransferStatusId() == 1) {
                String username = tenmoService.getUserByUserId(currentUser,
                        tenmoService.getAccountByAccountId(currentUser, transfer.getAccountTo()).getUserId()).getUsername();
                System.out.println(transfer.getTransferId() + "              To: " + username + "             $" + transfer.getAmount());
            }
        }
        int transferId = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        if (transferId == 0) {
            System.out.println("View details cancelled");
        } else {
            Transfer transfer = tenmoService.getTransferByTransferId(currentUser, transferId);
            System.out.println("-------------------------------------------");
            System.out.println("Pending Transfer Details");
            System.out.println("-------------------------------------------");
            System.out.println("ID: " + transfer.getTransferId());
            System.out.println("From: " + tenmoService.getUserByUserId(currentUser,
                    tenmoService.getAccountByAccountId(currentUser, transfer.getAccountFrom()).getUserId()).getUsername());
            System.out.println("To: " + tenmoService.getUserByUserId(currentUser,
                    tenmoService.getAccountByAccountId(currentUser, transfer.getAccountTo()).getUserId()).getUsername());
            System.out.println("Type: " + tenmoService.getTransferTypeByTransferTypeId(currentUser, transfer.getTransferTypeId()).getTransferTypeDescription());
            System.out.println("Status: " + tenmoService.getTransferStatusByTransferStatusId(currentUser, transfer.getTransferStatusId()).getTransferStatusDescription());
            System.out.println("Amount: $" + transfer.getAmount());

            int transferIdConfirm = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
            while (transferIdConfirm != transferId && transferIdConfirm != 0) {
                transferIdConfirm = consoleService.promptForInt("Invalid command. Please enter transfer ID to approve/reject (0 to cancel): ");
            }
            if (transferIdConfirm == 0) {
                System.out.println("View details cancelled");
            } else {
                System.out.println("1: Approve");
                System.out.println("2: Reject");
                System.out.println("0: Don't Approve or Reject");

                int approveReject = consoleService.promptForInt("Please enter choice to approve/reject (0 to cancel): ");
                if (transfer.getAmount().compareTo(tenmoService.getBalance(currentUser).getBalance()) > 0) {
                    approveReject = 0;
                    System.out.println("Balance insufficient.");
                }
                if (approveReject == 0) {  //functionally does nothing except cancel
                    System.out.println("Request remains pending");
                }
                if (approveReject == 1) {
                    tenmoService.approveTransfer(currentUser, transfer);
                    System.out.println("Request approved");
                }
                if (approveReject == 2) {
                    tenmoService.rejectTransfer(currentUser, transfer);
                    System.out.println("Request rejected");
                }
            }
        }
    }


    private void sendBucks() {
        User[] users = tenmoService.getAllUsers(currentUser);
        printUsers(users);

        int userInput = consoleService.promptForInt("Enter the ID of the user you want to send money to (enter 0 to cancel): ");
        if (userInput == 0 || userInput == currentUser.getUser().getId()) {
            System.out.println("Transaction cancelled");
        } else {
            int toId = 0;
            for (User user : users) {
                if (user.getId() == userInput) {
                    toId = userInput;
                }
            }

            BigDecimal transferAmount = consoleService.promptForBigDecimal("Enter amount to send: ");
            while (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                transferAmount = consoleService.promptForBigDecimal("Transfer amount must be more than 0. Enter amount to transfer: ");
            }
            while (transferAmount.compareTo(tenmoService.getBalance(currentUser).getBalance()) == 1) {
                transferAmount = consoleService.promptForBigDecimal("Transfer amount cannot be more than user balance. Enter amount to transfer: ");
            }
            Transfer transfer = createTransfer(2, 2, toId, transferAmount);
            tenmoService.transfer(currentUser, transfer);

            Balance newBalance = tenmoService.getBalance(currentUser);
            checkBalance(newBalance);
            System.out.println("Successfully sent: $" + transferAmount);
            System.out.println("Your new balance is: $" + checkBalance(newBalance));

        }
    }

    private void requestBucks() {
        User[] users = tenmoService.getAllUsers(currentUser);
        printUsers(users);
        int userInput = consoleService.promptForInt("Enter the ID of the user you want to request money from (enter 0 to cancel): ");
        if (userInput == 0 || userInput == currentUser.getUser().getId()) {
            System.out.println("Transaction cancelled");
        } else {
            int fromId = 0;
            for (User user : users) {
                if (user.getId() == userInput) {
                    fromId = userInput;
                }
            }
            BigDecimal transferAmount = consoleService.promptForBigDecimal("Enter amount to request: ");
            while (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                transferAmount = consoleService.promptForBigDecimal("Transfer amount must be more than 0. Enter amount to transfer: ");
            }

            Transfer transfer = createTransfer(1, 1, fromId, transferAmount);
            tenmoService.transfer(currentUser, transfer);

            Balance currentBalance = tenmoService.getBalance(currentUser);
            checkBalance(currentBalance);
            System.out.println("Successfully requested: $" + transferAmount + ". Request Pending.");
            System.out.println("Your current balance is: $" + checkBalance(currentBalance));
        }
    }

    private Transfer createTransfer(int status, int type, int nonUserId, BigDecimal transferAmount) {
        Transfer transfer = new Transfer();
        transfer.setTransferStatusId(status);
        transfer.setTransferTypeId(type);

        Account fromAccount = tenmoService.getAccountByUserId(currentUser, nonUserId);
        Account toAccount = tenmoService.getAccountByUserId(currentUser, currentUser.getUser().getId());

        if (type == 2) {
            fromAccount = tenmoService.getAccountByUserId(currentUser, currentUser.getUser().getId());
            toAccount = tenmoService.getAccountByUserId(currentUser, nonUserId);
        }
        transfer.setAccountFrom(fromAccount.getAccountId());
        transfer.setAccountTo(toAccount.getAccountId());
        transfer.setAmount(transferAmount);
        return transfer;
    }


    private void printUsers(User[] users) {
        System.out.println("Choose from the following users: ");
        for (User user : users) {
            if (user.getUsername().equals(currentUser.getUser().getUsername())) {
                continue;
            }
            System.out.println(user.getId() + ": " + user.getUsername());
        }
    }


    private BigDecimal checkBalance(Balance newBalance) {
        BigDecimal balance = BigDecimal.valueOf(0.00);
        if (!newBalance.getBalance().equals(BigDecimal.ZERO)) {
            balance = newBalance.getBalance();
        }
        return balance;
    }


}
