package com.lztimer.server.config;

/**
 * Application constants.
 */
public final class Constants {

    //Regex for acceptable login's
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";

    public static final String ANONYMOUS_USER = "anonymoususer";

    private Constants() {
    }
}
