package com.app.budbank.utils;

import com.app.budbank.models.LoginModel;

public class AppConstants {

    final public static String ISFROM = "isFrom";
    final public static String CANCEL = "cancel";
    public static final String JPEG_FILE_PREFIX = "IMG_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";
    final public static int UPDATE_INTERVAL = 1000;
    final public static int FASTEST_INTERVAL = 1000;
    final public static int PAGE_SIZE = 20;

    public enum IsFrom {

        DISPENSARY_FRAGMENT("dispensary_fragment"),
        QUIZ_FRAGMENT("quiz_fragment"),
        HOME_FRAGMENT("home_fragment"),
        UNREAD_NOTIFICATION_FRAGMENT("unread_notification_fragmetn"),
        READ_NOTIFICATION_FRAGMENT("read_notification_fragmetn"),
        INBOX_ACTIVITY("inbox_activity"),
        SEARCH_ACTIVITY("search_activity"),
        BOARDING_ACTIVTY("boarding_activity"),
        LOGIN_ACTIVTY("login_activity"),

        SIGNUP_ACTIVTY("signup_activity");

        private String value;

        IsFrom(String i) {
            value = i;
        }

        public String getValue() {
            return this.value;
        }

    }

    public enum SharedPreferencesKeys {

        SESSION_TOKEN("session_token"),
        IS_LOGGED_IN("is_logged_in"),
        USER_ID("user_id"),
        PHONE_NUMBER("phone_number"),
        EMAIL("user_email"),
        EMAIL_VERIFIED_AT("user_email_verified_at"),
        USERNAME("username"),
        FIRST_NAME("user_first_name"),
        LAST_NAME("üser_last_name"),
        PROFILE_IMAGE("user_image"),
        COINS_EARNED("user_coins_earned");
        private String value;

        SharedPreferencesKeys(String i) {
            value = i;
        }

        public String getValue() {
            return this.value;
        }

    }


    public enum StatusCodes {
        SUCCESS(200),
        FAILURE(400),
        NOT_FOUND(404),
        AUTHENTICATION_FAILED(401);

        int mValue;

        StatusCodes(int value) {
            this.mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    public enum RequestCodes {

        REQUEST_CAMERA(9161),
        REQUEST_GALLERY(9162),
        REQUEST_VIDEO(9163),
        REQUEST_PERMISSION(256),
        REQUEST_PROFILE(300),
        REQUEST_SOURCE_LOCATION(310),
        REQUEST_SYSTEM_SETTINGS(320),
        PLAY_SERVICES_RESOLUTION_REQUEST(5000);

        int mValue;

        RequestCodes(int value) {
            this.mValue = value;
        }

        public int getCode() {
            return mValue;
        }
    }

    public enum MediaType {
        IMAGE("IMAGE"),
        VIDEO("VIDEO");

        String mValue;

        MediaType(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }

    public enum IntentKeys {
        DISPENSARY_ID("dispensary_id"),
        DISPENSARY_MODEL("dispensary_model"),
        QUIZ_QUESTIONS("quiz_questions"),
        VOUCHER_MODEL("voucher_model"),
        UNREAD_NOTIFICATIONS("unread_notifications"),
        READ_NOTIFICATIONS("read_notifications"),
        CORRECT_ANSWERS_COUNT("correct_answers_count"),
        EVENT_RESPONSE("event-response"),
        NOTIFICATION_MODEL("notification_model"),
        FRAGMENT_TO_OPEN("fragment_to_open"),
        USER_DISABLED_DISPENSARIES("user_disabled_dispensaries");

        String mValue;

        IntentKeys(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }

    public enum Actions {
        UPDATE_COINS("update_coins"),
        UPDATE_NOTIFICATIONS("update_notifications"),
        UPDATE_PROFILE("update_profile"),
        COMPLETED_DISPENSARY("completed_dispensary"),
        REDEEM_DISPENSARY("redeem_dispensary"),
        AVAILABLE_VOUCHER("available_voucher"),
        READ_NOTIFICATION("read_notification"),
        UNREAD_DELETE_NOTIFICATION("unread_delete_notification"),
        READ_DELETE_NOTIFICATION("read_delete_notification"),
        FOLLOW_UNFOLLOW("follow_unfollow")
        ;

        String mValue;

        Actions(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }
}
