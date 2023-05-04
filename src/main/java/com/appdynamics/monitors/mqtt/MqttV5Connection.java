package com.appdynamics.monitors.mqtt;
import java.net.URISyntaxException;
import java.sql.Timestamp;

import com.appdynamics.monitors.mqtt.config.MetricTopic;
import com.appdynamics.monitors.mqtt.config.Server;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttMessage;

public class MqttV5Connection {

    private String hostURI;
    private String clientID;
    private MqttConnectionOptions conOpts = new MqttConnectionOptions();
    private boolean automaticReconnect = false;

    /**
     * Initialises an MQTTv5 Connection Object which holds the configuration
     * required to open a connection to an MQTTv5 server
     *
     * @param serverConfig
     *            - The individual server configuration from config.yml.
     * @throws URISyntaxException
     */
    public MqttV5Connection(Server serverConfig, MetricTopic metricTopic) {

        // Get the Host URI
        if (serverConfig.hasHost()) {
            conOpts.setServerURIs(new String[] { serverConfig.getHost() });
            this.hostURI = serverConfig.getHost();
        }

        if (serverConfig.hasClientID()) {
            clientID = serverConfig.getClientID()+'_'+metricTopic.getMetric_name();
        }

        if (serverConfig.hasKeepAlive()) {
            conOpts.setKeepAliveInterval(serverConfig.getKeepAlive());
        }

        if (serverConfig.hasPassword()) {
            conOpts.setPassword(serverConfig.getPassword().getBytes());
        }
        if (serverConfig.hasUsername()) {
            conOpts.setUserName(serverConfig.getUsername());
        }


        if (serverConfig.hasCleanSession()) {
            conOpts.setCleanStart(serverConfig.getCleanSession());
        }

        if (serverConfig.hasAutomaticReconnect()) {
            conOpts.setAutomaticReconnect(serverConfig.getAutomaticReconnect());
            this.automaticReconnect = serverConfig.getAutomaticReconnect();
        }

        // If the client ID was not set, generate one ourselves
        if (clientID == null || clientID == "") {
            // No client ID provided, generate one from the process ID
            long pid = Thread.currentThread().getId(); //ProcessHandle.current().pid();
            clientID = serverConfig.getDisplayName()+'_'+metricTopic.getMetric_name()+'_'+ new Timestamp(System.currentTimeMillis()).toString();
        }


    }

    public String getHostURI() {
        return hostURI;
    }

    public String getClientID() {
        return clientID;
    }

    public MqttConnectionOptions getConOpts() {
        return conOpts;
    }

    public boolean isAutomaticReconnectEnabled() {
        return this.automaticReconnect;
    }

}