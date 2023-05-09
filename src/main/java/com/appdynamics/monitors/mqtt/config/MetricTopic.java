package com.appdynamics.monitors.mqtt.config;

import com.appdynamics.extensions.metrics.DeltaMetricsCalculator;
import com.appdynamics.monitors.mqtt.MqttV5Subscribe;
import com.singularity.ee.agent.systemagent.api.MetricWriter;

public class MetricTopic {

    private String metricPath;
    private String metric_name;
    private String metric_topic;



    private String metric_time_rollup_type = MetricWriter.METRIC_TIME_ROLLUP_TYPE_CURRENT;
    private String metric_aggregation_type = MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION;
    private String metric_cluster_rollup_type = MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL;
    private Boolean metric_delta = false;



    private DeltaMetricsCalculator deltaMetricsCalculator;



    private Integer deltaCacheDuration = 300;

    private MqttV5Subscribe subscribeObj;
    public Integer getDeltaCacheDuration() {
        return deltaCacheDuration;
    }

    public void setDeltaCacheDuration(Integer deltaCacheDuration) {
        this.deltaCacheDuration = deltaCacheDuration;
    }
    public Boolean getMetric_delta() {
        return metric_delta;
    }

    public void setMetric_delta(Boolean metric_delta) {
        this.metric_delta = metric_delta;
        if(this.metric_delta){
            //instantiate calculator
            setDeltaMetricsCalculator(new DeltaMetricsCalculator(getDeltaCacheDuration()));
        }
    }

    public String getMetric_time_rollup_type() {
        return metric_time_rollup_type;
    }
    public DeltaMetricsCalculator getDeltaMetricsCalculator() {
        return deltaMetricsCalculator;
    }

    public void setDeltaMetricsCalculator(DeltaMetricsCalculator deltaMetricsCalculator) {
        this.deltaMetricsCalculator = deltaMetricsCalculator;
    }

    public void setMetric_time_rollup_type(String metric_time_rollup_type) {
        if(metric_time_rollup_type.toLowerCase().contains("current")){
            this.metric_time_rollup_type = MetricWriter.METRIC_TIME_ROLLUP_TYPE_CURRENT;
        }
        if(metric_time_rollup_type.toLowerCase().contains("sum")){
            this.metric_time_rollup_type = MetricWriter.METRIC_TIME_ROLLUP_TYPE_SUM;
        }
        if(metric_time_rollup_type.toLowerCase().contains("average")){
            this.metric_time_rollup_type = MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE;
        }
    }

    public String getMetric_aggregation_type() {
        return metric_aggregation_type;
    }

    public void setMetric_aggregation_type(String metric_aggregation_type) {
        if(metric_aggregation_type.toLowerCase().contains("average")){
            this.metric_aggregation_type = MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE;
        }
        if(metric_aggregation_type.toLowerCase().contains("sum")){
            this.metric_aggregation_type = MetricWriter.METRIC_AGGREGATION_TYPE_SUM;
        }
        if(metric_aggregation_type.toLowerCase().contains("observation")){
            this.metric_aggregation_type = MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION;
        }
    }

    public String getMetric_cluster_rollup_type() {
        return metric_cluster_rollup_type;
    }

    public void setMetric_cluster_rollup_type(String metric_cluster_rollup_type) {
        if(metric_cluster_rollup_type.toLowerCase().contains("individual")){
            this.metric_cluster_rollup_type = MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL;
        }
        if(metric_cluster_rollup_type.toLowerCase().contains("collective")){
            this.metric_cluster_rollup_type = MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE;
        }
    }

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
