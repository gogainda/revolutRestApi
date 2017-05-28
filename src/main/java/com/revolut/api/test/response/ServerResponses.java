package com.revolut.api.test.response;

/**
 * Created by igor on 2017-05-28.
 */
public class ServerResponses {
    public static final String HEALTH_CHECK_STATUS = "Running";
    public static final String HELP_INFO = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Untitled Document.md</title><style></style></head><body id=\"preview\">\n" +
            "<h1><a id=\"Help_0\"></a>Help</h1>\n" +
            "<p>Hello!</p>\n" +
            "<p>Current implementation of restful api contains the following functionality:</p>\n" +
            "<h2><a id=\"Create_new_user_5\"></a>Create new user</h2>\n" +
            "<p>Send the POST request to the url</p>\n" +
            "<pre><code>\\users\n" +
            "</code></pre>\n" +
            "<p>with JSON like</p>\n" +
            "<pre><code class=\"language-javascript\">{\n" +
            "    <span class=\"hljs-string\">\"name\"</span>:<span class=\"hljs-string\">\"UserName\"</span>,\n" +
            "    <span class=\"hljs-string\">\"moneyInCents\"</span>:<span class=\"hljs-string\">\"10000\"</span>\n" +
            "}\n" +
            "</code></pre>\n" +
            "<h3><a id=\"Check_user_info_20\"></a>Check user info</h3>\n" +
            "<p>Send the GET request to the url</p>\n" +
            "<pre><code class=\"language-javascript\">\\users\\{user_name}\n" +
            "</code></pre>\n" +
            "<h3><a id=\"Make_transfer_between_users_26\"></a>Make transfer between users</h3>\n" +
            "<p>Send the POST request to the url</p>\n" +
            "<pre><code>\\users\\transfer\n" +
            "</code></pre>\n" +
            "<p>with JSON like</p>\n" +
            "<pre><code class=\"language-javascript\">{\n" +
            "    <span class=\"hljs-string\">\"fromUser\"</span>:<span class=\"hljs-string\">\"FromUserName\"</span>,\n" +
            "    <span class=\"hljs-string\">\"toUser\"</span>:<span class=\"hljs-string\">\"ToUserName\"</span>, \n" +
            "    <span class=\"hljs-string\">\"amount\"</span>:<span class=\"hljs-string\">\"10000\"</span>\n" +
            "}\n" +
            "</code></pre>\n" +
            "\n" +
            "</body></html>";
}
