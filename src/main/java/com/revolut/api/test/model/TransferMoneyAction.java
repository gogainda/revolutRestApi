package com.revolut.api.test.model;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.concurrent.ConcurrentMap;

public class TransferMoneyAction implements Runnable {
    private final TransferRequest transferRequest;
    private String validationMessage;

    public TransferMoneyAction(TransferRequest transferRequest) {
        this.transferRequest = transferRequest;
    }

    public void perform() {
        DB db = DBMaker
                .fileDB(".db.file").fileLockDisable()
                .make();
        synchronized (TransferMoneyAction.class) {
            ConcurrentMap map = db.hashMap("users").createOrOpen();
            User fromUser = (User) map.get(transferRequest.getFromUser());
            User toUser = (User) map.get(transferRequest.getToUser());

            validationMessage = prepareResponseMessage(transferRequest, fromUser, toUser);

            if (canTransfer(transferRequest, fromUser)) {
                fromUser.sendsMoneyTo(toUser, transferRequest.getAmount());
                map.replace(fromUser.getName(), fromUser);
                map.replace(toUser.getName(), toUser);
            }

            db.commit();
            db.close();
        }
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    private boolean canTransfer(TransferRequest transferRequest, User fromUser) {
        return fromUser.getMoneyInCents() >= transferRequest.getAmount();
    }

    private String prepareResponseMessage(TransferRequest transferRequest, User fromUser, User toUser) {
        String responseMessage = "";
        if (fromUser == null) {
            responseMessage = String.format("User %s not found", transferRequest.getFromUser());
        }
        if (toUser == null) {
            responseMessage = String.format("User %s not found", transferRequest.getToUser());
        }

        if (!canTransfer(transferRequest, fromUser)) {
            responseMessage = String.format("%s doesn't have enough money", transferRequest.getFromUser());
        }
        return responseMessage;
    }

    public void run() {
        perform();
    }


}
