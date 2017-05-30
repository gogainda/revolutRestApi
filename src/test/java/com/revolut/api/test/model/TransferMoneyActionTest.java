package com.revolut.api.test.model;

import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class TransferMoneyActionTest {
    @Test
    public void transferActionShouldNotProduceInconsistentData() throws ExecutionException, InterruptedException {
        clearDb();
        createUser(new User("fromUser", 100000));
        createUser(new User("toUser", 0));
        TransferRequest request = new TransferRequest("fromUser", "toUser", 10000);
        ExecutorService pool = Executors.newFixedThreadPool(4);

        List<Future> futures = new ArrayList<Future>(10);

        for(int i = 0; i < 10; i++){
            futures.add(pool.submit(new TransferMoneyAction(request)));
        }

        for(Future future : futures){
            future.get();
        }

        pool.shutdown();
        DB db = DBMaker
                .fileDB(".db.file")
                .make();
        ConcurrentMap map = db.hashMap("users").createOrOpen();

        assertEquals(0, ((User) map.get("fromUser")).getMoneyInCents());
        assertEquals(100000, ((User) map.get("toUser")).getMoneyInCents());

        db.close();
    }

    private void clearDb() {
        DB db = DBMaker
                .fileDB(".db.file")
                .make();
        ConcurrentMap map = db.hashMap("users").createOrOpen();
        map.clear();
        db.commit();
        db.close();
    }

    private void createUser(User user) {
        DB db = DBMaker
                .fileDB(".db.file")
                .make();
        ConcurrentMap map = db.hashMap("users").createOrOpen();
        map.put(user.getName(), user);
        db.commit();
        db.close();
    }

}