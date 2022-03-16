package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;

import java.util.List;

public interface TransferDao {

    void transfer (Transfer transfer);
    void approveTransfer(Transfer transfer);
    void rejectTransfer(Transfer transfer);
    List<Transfer> viewAllTransfersByAccountId(Long accountId);
    Transfer viewTransferByTransferId(Long transferId);



}
