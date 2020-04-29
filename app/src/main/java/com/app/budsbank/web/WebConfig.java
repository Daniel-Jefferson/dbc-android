package com.app.budsbank.web;

public class WebConfig {
    private static final String BASE_URL                             = "http://api.budsbank.com/";
    public static final String API_URL                               = BASE_URL;

    // TODO: Update for local
    // private static final String BASE_URL                             = "http://10.0.2.2";
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
