package com.appdynamics.monitors.mqtt.config;

import com.appdynamics.monitors.mqtt.MqttV5Subscribe;

public class MetricTopic {

    private String metricPath;
    private String metric_name;
    private String metric_topic;


    private MqttV5Subscribe subscribeObj;

    public String getMetricPath() {
        return metricPath;
    }

    public void setMetricPath(String metricPath) {
        this.metricPath = metricPath;
    }
    public String getMetric_name() {
        return metric_name;
    }

    public void setMetric_name(String metric_name) {
        this.metric_name = metric_name;
    }

    public String getMetric_topic() {
        return metric_topic;
    }

    public void setMetric_topic(String metric_topic) {
        this.metric_topic = metric_topic;
    }

    public MqttV5Subscribe getSubscribeObj() {
        return subscribeObj;
    }

    public void setSubscribeObj(MqttV5Subscribe subscribeObj) {
        this.subscribeObj = subscribeObj;
    }

    public String toString() {
        return "{metric_name:"+this.metric_name+", metric_topic:"+this.metric_topic+"}";
    }


}
