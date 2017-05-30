package com.revolut.api.test.model;

/**
 * Created by igor on 2017-05-28.
 */
public class TransferRequest {
    private String fromUser;
    private String toUser;
    private long amount;

    public TransferRequest(String fromUser, String toUser, long amount) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
    }

    public TransferRequest() {
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransferRequest{" +
                "fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", amount=" + amount +
                '}';
    }
}
