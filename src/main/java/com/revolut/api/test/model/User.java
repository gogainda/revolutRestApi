package com.revolut.api.test.model;

import javax.ws.rs.QueryParam;
import java.io.Serializable;

/**
 * Created by igor on 2017-05-28.
 */
public class User implements Serializable {
    private String name;
    private long moneyInCents;

    public User() {
    }

    public User(@QueryParam("name")String name, @QueryParam("moneyInCents")long moneyInCents) {
        this.name = name;
        this.moneyInCents = moneyInCents;
    }

    public String getName() {
        return name;
    }

    public long getMoneyInCents() {
        return moneyInCents;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoneyInCents(long moneyInCents) {
        this.moneyInCents = moneyInCents;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", moneyInCents=" + moneyInCents +
                '}';
    }

    public void sendsMoneyTo(User toUser, long amount) {
        long myMoneyAfterTransaction = moneyInCents - amount;
        long toUserMoneyAfterTransaction = toUser.getMoneyInCents() + amount;
        setMoneyInCents(myMoneyAfterTransaction);
        toUser.setMoneyInCents(toUserMoneyAfterTransaction);
    }
}
