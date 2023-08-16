package com.appdynamics.monitors.mqtt;
import com.appdynamics.monitors.mqtt.config.MetricTopic;
import com.appdynamics.monitors.mqtt.config.Server;


public class MqttV5Subscribe {
    private final String topic;
    private int qos = 0;


    public MqttV5Subscribe(Server serverConfig, MetricTopic metricTopic) {
        topic = metricTopic.getMetric_topic();

        if(serverConfig.hasQos()) {
            qos = serverConfig.getQos();
        }

    }

    public String getTopic() {
        return topic;
    }

    public int getQos() {
        return qos;
    }


}