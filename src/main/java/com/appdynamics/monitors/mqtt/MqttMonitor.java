/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.monitors.mqtt;


import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.metrics.DeltaMetricsCalculator;
import com.appdynamics.extensions.util.AssertUtils;

import com.appdynamics.monitors.mqtt.config.Configuration;
import com.appdynamics.monitors.mqtt.config.MetricTopic;
import com.appdynamics.monitors.mqtt.config.Server;

import com.google.common.collect.Maps;

import org.slf4j.Logger;

import java.util.*;

import static com.appdynamics.monitors.mqtt.Constant.*;



/**
 * An entry point into AppDynamics extensions.
 */
public class MqttMonitor extends ABaseMonitor {

    public static final String CONFIG_ARG = "config-file";
    public static final Logger logger = ExtensionsLoggerFactory.getLogger(MqttMonitor.class);
    public static final String METRICS_COLLECTION_SUCCESSFUL = "Metrics Collection Successful";
    public static final String FAILED = "0";
    public static final String SUCCESS = "1";
    private volatile boolean initialized;
    private MonitorContextConfiguration monitorContextConfiguration;
    private Map<String, ?> configYml = Maps.newHashMap();
    private Configuration config;


    @Override
    protected String getDefaultMetricPrefix() {
        return METRIC_PREFIX;
    }

    @Override
    public String getMonitorName() {
        return MONITOR_NAME;
    }

    @Override
    protected List<Map<String, ?>> getServers() {
        List<Map<String, ?>> servers = (List<Map<String, ?>>) getContextConfiguration().getConfigYml().get(CFG_SERVERS);
        AssertUtils.assertNotNull(servers, "The 'servers' section in config.yml is not initialised");
        return servers;
    }

    @Override
    protected void initializeMoreStuff(Map<String, String> args) {
        monitorContextConfiguration = getContextConfiguration();
        configYml = monitorContextConfiguration.getConfigYml();

    }

    @Override
    protected void doRun(TasksExecutionServiceProvider serviceProvider) {
        try {
            getConfig();
            if (this.config != null) {

                if (this.config.getServers().size() > 0) {
                    for (Server server : this.config.getServers()) {
                        AssertUtils.assertNotNull(server.getDisplayName(), CFG_DISPLAY_NAME + " can not be null in the config.yml");
                        AssertUtils.assertNotNull(server.getHost(), CFG_HOST + " can not be null in the config.yml");
                        logger.info("Starting the Mqtt Task for server : " + server.getDisplayName());
                        MqttMonitorTask task = new MqttMonitorTask(this.monitorContextConfiguration, serviceProvider.getMetricWriteHelper(), server, this.config);
                        serviceProvider.submit(server.getDisplayName(), task);
                    }
                }
            } else {
                logger.error("The config.yml is not loaded due to previous errors.The task will not run");
            }
        }
        catch (Exception e) {
            logger.error("Mqtt Extension can not proceed due to errors in the config.", e);
        }
    }
    private Configuration getConfig(){
        if(this.config != null){
            return this.config;
        }
        //turn getServers map list into Server object list to add to Configuration object
        List<Map<String, ?>> map_servers = this.getServers();
        ArrayList<Server> server_arr = new ArrayList<>();
        for (Map<String, ?> server : map_servers) {
            Server single_server = new Server() {{
                setHost((String) server.get(CFG_HOST));
                setDisplayName((String) server.get(CFG_DISPLAY_NAME));
                setClientID((String) server.get(CFG_CLIENT_ID));
                setKeepAlive((int) server.get(CFG_KEEP_ALIVE));
                setUsername((String) server.get(CFG_USERNAME));
                setPassword((String) server.get(CFG_PASSWORD));
                setQos((int) server.get(CFG_QOS));
                setCleanSession((boolean) server.get(CFG_CLEAN_SESSION));
                setAutomaticReconnect((boolean) server.get(CFG_AUTOMATIC_RECONNECT));

            }};
            ArrayList<MetricTopic> metricTopics = new ArrayList<>();
            List<Map<String, ?>> topics = (List<Map<String, ?>>) server.get(CFG_TOPICS);
            for (Map<String, ?> topic: topics){
                MetricTopic metricTopic = new MetricTopic();
                metricTopic.setMetric_name((String) topic.get(CFG_METRIC_NAME));
                metricTopic.setMetric_topic((String) topic.get(CFG_METRIC_TOPIC));
                metricTopic.setSubscribeObj(new MqttV5Subscribe(single_server, metricTopic));
                metricTopics.add(metricTopic);
            }
            single_server.setTopics(metricTopics);
            server_arr.add(single_server);
        }
        this.config = new Configuration() {{
            setMetricPrefix((String) configYml.get(CFG_METRIC_PREFIX));
            setServers(server_arr);
            Integer t = (Integer) configYml.get(CFG_TIMEOUT);
            setTimeout(t.longValue());

        }};
        return this.config;
    }

}
