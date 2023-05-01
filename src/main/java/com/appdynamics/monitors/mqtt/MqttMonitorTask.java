package com.appdynamics.monitors.mqtt;

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
    private final MetricWriteHelper metricWriteHelper;
    private final MonitorContextConfiguration contextConfiguration;
    private final String metricPathPrefix;
    private final Configuration config;
    private ArrayList<MqttV5Executor> topic_listeners;

    public MqttMonitorTask(MonitorContextConfiguration contextConfiguration, MetricWriteHelper metricWriteHelper, Server server, Configuration config) {
        this.server = server;
        this.contextConfiguration = contextConfiguration;
        this.metricWriteHelper = metricWriteHelper;
        this.metricPathPrefix = this.contextConfiguration.getMetricPrefix() + METRIC_SEPARATOR + server.getDisplayName() + METRIC_SEPARATOR;
        this.config = config;
    }

    @Override
    public void onTaskComplete() {

    }

    @Override
    public void run() {
        //get metrics
        try {
            for (MetricTopic topic : this.server.getTopics()){

            }
        }
        catch(Exception e){
            logger.error("Unable to collect mqtt metrics ", e);
        }

    }

    /*private InstanceMetric collectMetrics() throws Exception {
        MemcachedClient memcachedClient = null;
        try {
            memcachedClient = getMemcachedClient();

            Map<InetSocketAddress, Map<String, String>> stats = memcachedClient.getStats(config.getTimeout());
            //task structure is one server at a time so we don't really need anymore then the stats map
            if(stats != null) {
                Iterator<InetSocketAddress> it = stats.keySet().iterator();
                if (it.hasNext()) {
                    InetSocketAddress sockAddress = it.next();
                    Map<String, String>statsmap = stats.get(sockAddress);
                    InstanceMetric server_instance = new InstanceMetric(this.server.getDisplayName(), statsmap, this.server, this.config, this.deltaMetricsCalculator);
                    server_instance.populateMetrics();
                    this.server_stats = server_instance;
                } else {
                    logger.error("Unable to get stats for " + server.getServer());
                }
            }
            return this.server_stats;
        }
        catch(Exception e){
            logger.error("Unable to collect memcached metrics ", e);
            throw e;
        }
        finally {
            if (memcachedClient != null) {
                memcachedClient.shutdown();
            }
        }
    }*/

    /**
     * Builds a memcached client.
     * @return MemcachedClient
     * @throws IOException
     */
    /*private MqttClient getMemcachedClient() throws IOException {
        String[] host_port = this.server.getServer().split(":");
        XMemcachedClient client = new XMemcachedClient(host_port[0],Integer.parseInt(host_port[1]));
        return client;
    }
*/
}
