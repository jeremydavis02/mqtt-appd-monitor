package com.appdynamics.monitors.mqtt;

public class Constant {

    /*public static String USER_NAME;
    public static String PASSWORD;
    public static String ENCRYPTED_PASSWORD;
    public static String ENCRYPTION_KEY;*/
    public static String METRIC_SEPARATOR;
    public static String METRIC_PREFIX;
    public static String MONITOR_NAME;
    public static final String CFG_DISPLAY_NAME = "displayName";
    public static final String CFG_SERVERS = "servers";
    public static final String CFG_HOST = "host";
    public static final String CFG_VERBOSE = "verbose";
    public static final String CFG_CLIENT_ID = "clientID";
    public static final String CFG_METRIC_PREFIX = "metricPrefix";
    public static final String CFG_TIMEOUT = "timeout";
    public static final String CFG_KEEP_ALIVE = "keepAlive";
    public static final String CFG_USERNAME = "username";
    public static final String CFG_PASSWORD = "password";
    public static final String CFG_QOS = "qos";
    public static final String CFG_CLEAN_SESSION = "clean-session";
    public static final String CFG_AUTOMATIC_RECONNECT = "automatic-reconnect";
    public static final String CFG_TOPICS = "topics";
    public static final String CFG_METRIC_NAME = "metric_name";
    public static final String CFG_METRIC_TOPIC = "metric_topic";

    static {
        MONITOR_NAME = "Mqtt Monitor";
        METRIC_SEPARATOR = "|";

    }


}
