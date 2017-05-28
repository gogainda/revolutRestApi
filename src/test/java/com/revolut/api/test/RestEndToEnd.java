package com.revolut.api.test;


import com.revolut.api.test.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by igor on 2017-05-28.
 */

public class RestEndToEnd {

    private RestServer server;
    private ApplicationRunner application;

    @Before
    public void startServer() throws InterruptedException {
        server = new RestServer();
        application = new ApplicationRunner(server);
        application.start();
        application.clearDb();
    }

    @Test
    public void serverHealthCheck() throws Exception {
        application.getsResponseFromServer();
    }

    @Test
    public void creationOfUser() throws Exception {
        long TEN_DOLLARS   = 10000;
        User user  = new User("UserName", TEN_DOLLARS);
        application.createsUser(user);
        application.checksUserInfo(user);
    }


    @Test
    public void transferMoneyBetweenUsers() throws Exception {
        long TEN_DOLLARS   = 10000;
        long FIVE_DOLLARS   = 5000;
        long NOTHING   = 0;
        User user1  = new User("user1", TEN_DOLLARS);
        User user2  = new User("user2", NOTHING);
        application.createsUser(user1);
        application.createsUser(user2);
        application.transfersMoneySuccessfully(user1, user2, FIVE_DOLLARS);

        application.checksUserAccount(user1, FIVE_DOLLARS);
        application.checksUserAccount(user2, FIVE_DOLLARS);
    }

    @Test
    public void transferFails() throws Exception {
        long TEN_DOLLARS   = 10000;
        long NOTHING   = 0;
        User user1  = new User("user1", NOTHING);
        User user2  = new User("user2", NOTHING);
        application.createsUser(user1);
        application.createsUser(user2);
        application.transferMoneyFailed(user1, user2, TEN_DOLLARS);
        application.checksUserAccount(user1, NOTHING);
        application.checksUserAccount(user2, NOTHING);
    }



    @After
    public void stopServer() throws InterruptedException {
        application.stop();
    }


}
