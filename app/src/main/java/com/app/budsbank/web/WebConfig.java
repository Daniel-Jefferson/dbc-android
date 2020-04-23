package com.app.budsbank.web;

public class WebConfig {
    private static final String BASE_URL                             = "http://ec2-18-188-251-85.us-east-2.compute.amazonaws.com/";
//    private static final String BASE_URL                             = "http://192.168.6.150:3300/";
    public static final String API_URL                               = BASE_URL;

    // TODO: Update for local
    // private static final String BASE_URL                             = "http://178.128.187.104";
    // public static final String API_URL                               = BASE_URL + ":3300/";


    public static class Headers {
        static final String AUTHORIZATION                            = "Authorization";
        public static final String SESSION_TOKEN                     = "session_token";
    }

    public static class BudsBankConfigurations {

        public static final String USERNAME                          = "budsBank";
        public static final String PASSWORD                          = "budsBank007";
    }
}
