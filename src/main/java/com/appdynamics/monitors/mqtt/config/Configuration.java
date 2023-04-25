/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.monitors.mqtt.config;


import java.util.ArrayList;
import java.util.Set;

import static com.appdynamics.monitors.mqtt.Constant.METRIC_SEPARATOR;


/**
 * An object holder for the configuration file
 */
public class Configuration {

    String metricPrefix;
    ArrayList<Server> servers;

    long timeout = 60000;


    public ArrayList<Server> getServers() {
        return servers;
    }

    public void setServers(ArrayList<Server> servers) {
        this.servers = servers;
    }

    public String getMetricPrefix() {
        return metricPrefix;
    }

    public void setMetricPrefix(String metricPrefix) {
        if(!metricPrefix.endsWith(METRIC_SEPARATOR)){
            metricPrefix = metricPrefix + METRIC_SEPARATOR;
        }
        this.metricPrefix = metricPrefix;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


}
