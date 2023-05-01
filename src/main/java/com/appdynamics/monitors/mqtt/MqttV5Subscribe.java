package com.appdynamics.monitors.mqtt;
import com.appdynamics.monitors.mqtt.config.MetricTopic;
import com.appdynamics.monitors.mqtt.config.Server;


public class MqttV5Subscribe {
    private String topic = "world";
    private int qos = 0;
    private boolean verbose = false;

    public MqttV5Subscribe(Server serverConfig, MetricTopic metricTopic) {
        topic = metricTopic.getMetric_topic();

        if(serverConfig.hasQos()) {
            qos = serverConfig.getQos();
        }

        if(serverConfig.hasVerbose()) {
            verbose = serverConfig.getVerbose();
        }
    }

    public String getTopic() {
        return topic;
    }

    public int getQos() {
        return qos;
    }

    public boolean isVerbose() {
        return verbose;
    }

}