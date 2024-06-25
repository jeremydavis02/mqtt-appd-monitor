package com.appdynamics.monitors.mqtt;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.monitors.mqtt.config.Configuration;
import com.appdynamics.monitors.mqtt.config.MetricTopic;
import com.appdynamics.monitors.mqtt.config.Server;
import com.appdynamics.extensions.metrics.DeltaMetricsCalculator;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static com.appdynamics.monitors.mqtt.Constant.*;
import static com.appdynamics.monitors.mqtt.Constant.METRIC_SEPARATOR;

public class MqttMonitorTask implements AMonitorTaskRunnable {
    public static final Logger logger = ExtensionsLoggerFactory.getLogger(MqttMonitorTask.class);
    private final Server server;
    private ABaseMonitor monitor;
    private final MonitorContextConfiguration contextConfiguration;
    private final String metricPathPrefix;
    private final Configuration config;
    private ArrayList<MqttV5Executor> topic_listeners;
    private ArrayList<Thread> topic_threads;

    public MqttMonitorTask(ABaseMonitor monitor, MonitorContextConfiguration contextConfiguration, Server server, Configuration config) {
        this.server = server;
        this.contextConfiguration = contextConfiguration;
        this.monitor = monitor;
        this.metricPathPrefix = this.contextConfiguration.getMetricPrefix() + METRIC_SEPARATOR + server.getDisplayName() + METRIC_SEPARATOR;
        this.config = config;
        this.topic_listeners = new ArrayList<MqttV5Executor>();
        this.topic_threads = new ArrayList<Thread>();
    }

    @Override
    public void onTaskComplete() {

    }

    @Override
    public void run() {
        //get metrics
        try {
            for (MetricTopic topic : this.server.getTopics()) {
                //for a given server we spin up an MQTTExecutor to subscribe to each metric topic listed
                //this class is implementing the paho callback interface so acts as client and handler of
                //messages
                logger.debug("Creating subscriber for topic: "+topic.toString());
                MqttV5Executor mqttV5Executor = new MqttV5Executor(this.monitor, this.server, topic, Mode.SUB, this.config.getTimeout());
                //Thread t = new Thread(mqttV5Executor::execute);
                mqttV5Executor.execute();
                //t.start();
                logger.debug("Topic subscriber started: "+topic.toString());
                this.topic_listeners.add(mqttV5Executor);
                //this.topic_threads.add(t);
            }
        } catch (Exception e) {
            logger.error("Unable to collect mqtt metrics ", e);
        }

    }

}

