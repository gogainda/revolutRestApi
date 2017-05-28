package com.revolut.api.test;

import com.revolut.api.test.model.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;

import static com.revolut.api.test.RestServer.SERVER_PORT;
import static com.revolut.api.test.response.ServerResponses.HEALTH_CHECK_STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


/**
 * Created by igor on 2017-05-28.
 */
public class ApplicationRunner {
    private static final String CREATE_USER_RESOURCE = "http://localhost:8080/users";
    private static final String TRANSFER_MONEY_RESOURCE = "http://localhost:8080/users/transfer";

    private final RestServer server;
    private Runnable runnableServer;
    private Thread serverThread;

    public ApplicationRunner(RestServer server) {
        this.server = server;

    }

    public void start() throws InterruptedException {

        runnableServer = new Runnable() {

            public void run() {
                server.start();
            }


        };
        serverThread = new Thread(runnableServer, "REST server thread");
        serverThread.start();
        waitForPort(SERVER_PORT);
    }

    public void getsResponseFromServer() throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:8080/health_check");
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null) {
            assertEquals(HEALTH_CHECK_STATUS, line);
        }
    }

    public void stop() throws InterruptedException {
        if (serverThread != null) {
            server.stop();
            serverThread.join();
        }
    }


    private static boolean available(int port) {
        try {
            Socket ignored = new Socket("localhost", port);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private static void waitForPort(int port) {
        while (!available(port)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    public void createsUser(User user) throws IOException {
        HttpClient client = new DefaultHttpClient();
        String yourJsonString = String.format("{\"name\":\"%s\",\"moneyInCents\":\"%d\"}", user.getName(), user.getMoneyInCents());

        StringEntity requestEntity = new StringEntity(
                yourJsonString,
                ContentType.APPLICATION_JSON);

        HttpPost postMethod = new HttpPost(CREATE_USER_RESOURCE);
        postMethod.setEntity(requestEntity);

        HttpResponse rawResponse = client.execute(postMethod);
        assertEquals(204, rawResponse.getStatusLine().getStatusCode());
    }

    public void checksUserInfo(User user) throws IOException {
        String result = getUserStringByUserName(user.getName());

        assertThat(result, CoreMatchers.containsString(user.getName()));
        assertThat(result, CoreMatchers.containsString(String.valueOf(user.getMoneyInCents())));
    }

    @NotNull
    private String getUserStringByUserName(String userName) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(String.format("http://localhost:8080/users/%s", userName));
        HttpResponse response = client.execute(request);
        return getResponseAsString(response);
    }

    @NotNull
    private String getResponseAsString(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        StringBuffer result = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public void transfersMoneySuccessfully(User fromUser, User toUser, long amount) throws IOException {
        HttpResponse rawResponse = transferMoneyBetweenUsers(fromUser, toUser, amount);
        assertEquals(200, rawResponse.getStatusLine().getStatusCode());
    }

    private HttpResponse transferMoneyBetweenUsers(User fromUser, User toUser, long amount) throws IOException {
        HttpClient client = new DefaultHttpClient();
        String yourJsonString = String.format("{\"fromUser\":\"%s\",\"toUser\":\"%s\", \"amount\":\"%d\"}",
                fromUser.getName(),
                toUser.getName(),
                amount);

        StringEntity requestEntity = new StringEntity(
                yourJsonString,
                ContentType.APPLICATION_JSON);

        HttpPost postMethod = new HttpPost(TRANSFER_MONEY_RESOURCE);
        postMethod.setEntity(requestEntity);

        return client.execute(postMethod);
    }

    public void checksUserAccount(User user, long five_dollars) throws IOException {
        String userString = getUserStringByUserName(user.getName());
        assertThat(userString, CoreMatchers.containsString(String.valueOf(five_dollars)));
    }

    public void clearDb() {
        DB db = DBMaker
                .fileDB(".db.file")
                .checksumHeaderBypass()
                .make();
        ConcurrentMap map = db.hashMap("users").createOrOpen();
        map.clear();
        db.commit();
        db.close();
    }

    public void transferMoneyFailed(User fromUser, User toUser, long amount) throws IOException {
        HttpResponse rawResponse = transferMoneyBetweenUsers(fromUser, toUser, amount);
        String response = getResponseAsString(rawResponse);
        assertEquals(400, rawResponse.getStatusLine().getStatusCode());
        assertThat(response, CoreMatchers.containsString(String.format("%s doesn't have enough money", fromUser.getName())));
    }
}
