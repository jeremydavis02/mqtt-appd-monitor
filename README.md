mqtt-appd-monitor
==============================
An AppDynamics monitor to be used with a stand alone Java machine agent to provide metrics from mqtt brokers.

Written and tested against mosquitto mqtt broker.

https://github.com/jeremydavis02/mqtt-appd-monitor

## Metrics Provided ##
```
Mosquitto documentation publishes a set of $SYS topic queues with potential metrics: https://mosquitto.org/man/mosquitto-8.html

However the extension is written as a topic client, so technically any topic that provides numerical messages
can be configured and subscribed to.

```
## Installation ##

1. Clone the "mqtt-appd-monitor" repo using `git clone <repoUrl>` command.
2. Run 'mvn clean install' from "mqtt-appd-monitor". This will produce a MqttMonitor-VERSION.zip in the target directory.
3. Unzip as "MqttMonitor" from targets folder and copy the "MqttMonitor" directory to `<MACHINE_AGENT_HOME>/monitors`
4.  Edit the config.yml file. An example config.yml file follows these installation instructions.
5. Verify the extension output in workbench mode and make sure desired metrics are reported. Check in WorkBench section for details.
6.  Restart the Machine Agent.


## Configuration ##

**Note**: Please make sure to not use tab (\t) while editing yaml files. You may want to validate the yaml file using a [yaml validator](https://jsonformatter.org/yaml-validator).

**Note**: Please use workbench mode to verify the metrics while experimenting with various configurations in config.yml to arrive at the desired result.

Edit the config.yml file in `<MACHINE_AGENT_HOME>/monitors/MqttMonitor/` to update the following.

1.  `enabled`: true | false - tells the machine agent to run this extension or not.
2.  `metricPrefix`: If you wish to report metrics only to the tier which this MachineAgent is reporting to, please comment the second metricPrefix and update the "<Component-ID>" with TierID or TierName in the first metricPrefix.
   ```
   metricPrefix: "Server|Component:<Component-ID>|Custom Metrics|Process Monitor|"
   metricPrefix: "Custom Metrics|Process Monitor|"
   ```
3.  `servers` : is an yaml array of mqtt brokers you want to connect to.
4. For each mqtt broker configure this set of values as needed
    ```
     - host: "tcp://127.0.0.1:1883"
       displayName: "localhost_test1" #will be used in metric browser so make sure unique
       clientID: "localtest_1" #unique id for client, if empty uses displayName and timestamp and append metric name
       keepAlive: 60 #keep alive in seconds, if nothing set 60 is default
       username: #if broker requires set here
       password: #if broker requires set here
       qos: 0 #we don't want retention etc so valid values are 0, 1, 2
       clean-session: true #client connects with clean session, we don't expect to pickup stored messages
       automatic-reconnect: true #auto reconnect on which is likely what we always want for this
       topics: # a list of topics, we make configurable here, but it's likely following closely to the relevant sys topics outlined: https://github.com/mqtt/mqtt.org/wiki/SYS-Topics
   
   ```
5. `topics` : is a yaml array of topics/metrics you want to subscribe to and report values for. Each iteration should consist of:

    ```
         - metric_name: "messages_received"
           metric_topic: "$SYS/broker/messages/received"
           metric_type: "OBSERVATION"
           metric_time_roll_up: "CURRENT"
           metric_delta: true
           metric_delta_cache_in_seconds: 300
   ```
6.  `metric_name` : will be what shows under this broker displayName in metric browser. 
7. `metric_topic` : is the topic to subscribe to. 
8. `metric_type` : valid values are ["AVERAGE" | "SUM" | "OBSERVATION"] is how you want appd to treat the metric referred to as aggregator type here: https://docs.appdynamics.com/appd/24.x/24.2/en/infrastructure-visibility/machine-agent/extensions-and-custom-metrics/build-a-monitoring-extension-using-java#BuildaMonitoringExtensionUsingJava-AggregationQualifier
9. `metric_time_roll_up` : valid values are ["CURRENT" | "SUM" | "AVERAGE"] is how you want appd to treat the metric referred to as time rollup here: https://docs.appdynamics.com/appd/24.x/24.2/en/infrastructure-visibility/machine-agent/extensions-and-custom-metrics/build-a-monitoring-extension-using-java#BuildaMonitoringExtensionUsingJava-TimeRoll-Up
10. `metric_cluster_roll_up` : valid values are ["INDIVIDUAL" | "COLLECTIVE"] is how you want appd to treat metric aggregation for the tier referred to here: https://docs.appdynamics.com/appd/24.x/24.2/en/infrastructure-visibility/machine-agent/extensions-and-custom-metrics/build-a-monitoring-extension-using-java#BuildaMonitoringExtensionUsingJava-ClusterRoll-Up
11. `metric_delta` : is true or false and determines if the metric is a continuously increasing number that you want a delta used for.
12. `metric_delta_cache_in_seconds` : is the seconds you want internal cache to retain the last number to calculate a delta from.
