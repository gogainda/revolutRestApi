package com.revolut.api.test.controller;


import com.revolut.api.test.model.TransferRequest;
import com.revolut.api.test.model.User;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentMap;

import static com.revolut.api.test.response.ServerResponses.HEALTH_CHECK_STATUS;
import static com.revolut.api.test.response.ServerResponses.HELP_INFO;

@Path("/")
public class UserController {
    static {
        System.setProperty("com.sun.jersey.api.json.POJOMappingFeature", "true");
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public String help() {
        return HELP_INFO;
    }


    @GET
    @Path("health_check")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return HEALTH_CHECK_STATUS;
    }

    @POST
    @Path("users")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createUser(User user) {
        DB db = DBMaker
                .fileDB(".db.file")
                .make();
        ConcurrentMap map = db.hashMap("users").createOrOpen();
        map.put(user.getName(), user);
        db.close();
    }

    @GET
    @Path("users/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserById(@PathParam("name") String name) {
        DB db = DBMaker
                .fileDB(".db.file")
                .make();
        ConcurrentMap map = db.hashMap("users").createOrOpen();
        User user = (User) map.get(name);
        db.close();
        return user;
    }

    @POST
    @Path("users/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response transferMoney(TransferRequest transferRequest) {
        DB db = DBMaker
                .fileDB(".db.file")
                .make();
        ConcurrentMap map = db.hashMap("users").createOrOpen();

        User fromUser = (User) map.get(transferRequest.getFromUser());
        User toUser = (User) map.get(transferRequest.getToUser());

        String responseMessage = prepareResponseMessage(transferRequest, fromUser, toUser);

        if (canTransfer(transferRequest, fromUser)) {
            fromUser.sendsMoneyTo(toUser, transferRequest.getAmount());
            map.replace(fromUser.getName(), fromUser);
            map.replace(toUser.getName(), toUser);
        }

        db.commit();
        db.close();

        return changeResponseCodeIfNeeded(responseMessage);
    }

    private Response changeResponseCodeIfNeeded(String responseMessage) {
        if(responseMessage.length() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(responseMessage).build();
        } else {
            return  Response.ok().build();
        }
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
}