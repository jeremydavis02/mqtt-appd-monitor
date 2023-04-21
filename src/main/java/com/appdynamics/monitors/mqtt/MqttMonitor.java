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

import com.appdynamics.extensions.memcached.config.Configuration;
import com.appdynamics.extensions.memcached.config.Server;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.appdynamics.extensions.memcached.Constant.*;



/**
 * An entry point into AppDynamics extensions.
 */
public class MemcachedMonitor extends ABaseMonitor {

    public static final String CONFIG_ARG = "config-file";
    public static final Logger logger = ExtensionsLoggerFactory.getLogger(MemcachedMonitor.class);
    public static final String METRICS_COLLECTION_SUCCESSFUL = "Metrics Collection Successful";
    public static final String FAILED = "0";
    public static final String SUCCESS = "1";
    private volatile boolean initialized;
    private MonitorContextConfiguration monitorContextConfiguration;
    private Map<String, ?> configYml = Maps.newHashMap();
    private Configuration config;
    private DeltaMetricsCalculator deltaCalculator;

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
        this.deltaCalculator = new DeltaMetricsCalculator(300);
    }

    @Override
    protected void doRun(TasksExecutionServiceProvider serviceProvider) {
        try {
            getConfig();
            if (this.config != null) {

                if (this.config.getServers().size() > 0) {
                    for (Server server : this.config.getServers()) {
                        AssertUtils.assertNotNull(server.getDisplayName(), CFG_DISPLAY_NAME + " can not be null in the config.yml");
                        AssertUtils.assertNotNull(server.getServer(), CFG_SERVER + " can not be null in the config.yml");
                        logger.info("Starting the Memcached Task for server : " + server.getDisplayName());
                        MemcachedMonitorTask task = new MemcachedMonitorTask(this.monitorContextConfiguration, serviceProvider.getMetricWriteHelper(), server, this.config, this.deltaCalculator);
                        serviceProvider.submit(server.getDisplayName(), task);
                    }
                }
            } else {
                logger.error("The config.yml is not loaded due to previous errors.The task will not run");
            }
        }
        catch (Exception e) {
            logger.error("Memcached Extension can not proceed due to errors in the config.", e);
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
            server_arr.add(new Server() {{
                setServer((String) server.get(CFG_SERVER));
                setDisplayName((String) server.get(CFG_DISPLAY_NAME));
            }});
        }
        this.config = new Configuration() {{
            setMetricPrefix((String) configYml.get(CFG_METRIC_PREFIX));
            setServers(server_arr);
            Integer t = (Integer) configYml.get(CFG_TIMEOUT);
            ArrayList<String> ignoreDeltas = (ArrayList<String>) configYml.get(CFG_IGNORE_DELTA);
            ArrayList<String> ignoreMetrics = (ArrayList<String>) configYml.get(CFG_IGNORE_METRIC);
            setTimeout(t.longValue());
            setIgnoreDelta(new HashSet<>(ignoreDeltas));
            setIgnoreMetric(new HashSet<>(ignoreMetrics));
        }};
        return this.config;
    }

}
