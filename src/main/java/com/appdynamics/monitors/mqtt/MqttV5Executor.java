package com.appdynamics.monitors.mqtt;


import com.appdynamics.extensions.metrics.DeltaMetricsCalculator;
import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.MetricWriteHelper;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.monitors.mqtt.config.MetricTopic;
import com.appdynamics.monitors.mqtt.config.Server;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;

import java.math.BigDecimal;

import static com.appdynamics.monitors.mqtt.Constant.METRIC_SEPARATOR;


public class MqttV5Executor implements MqttCallback {

    MqttV5Connection connectionParams;
    //MqttV5Publish publishParams;
    MqttV5Subscribe subscribeParams;
    Server server; //to store server config reference
    MetricTopic metricTopic; //to store topic config reference
    MetricWriteHelper metricWriteHelper;
    MetricWriter metricWriter;
    boolean quiet = false;
    boolean debug = false;
    MqttAsyncClient v5Client;
    Mode mode;
    private long actionTimeout;

    // To allow for a graceful disconnect
    final Thread mainThread = Thread.currentThread();
    static volatile boolean keepRunning = true;

    public static final Logger logger = ExtensionsLoggerFactory.getLogger(MqttV5Executor.class);

    /**
     * Initialises the MQTTv5 Executor
     *
     * @param serverConfig
     *            - Individual Server config from config.yml
     * @param mode
     *            - The mode to run in (PUB / SUB)
     * @param debug
     *            - Whether to print debug data to the console
     * @param quiet
     *            - Whether to hide error messages
     * @param actionTimeout
     *            - How long to wait to complete an action before failing.
     */
    public MqttV5Executor(ABaseMonitor monitor, Server serverConfig, MetricTopic metricTopic, Mode mode, long actionTimeout) {

        this.connectionParams = new MqttV5Connection(serverConfig, metricTopic);
        /*if (mode == Mode.PUB) {
            this.publishParams = new MqttV5Publish(commandLineParams);
        }*/
        if (mode == Mode.SUB) {
            this.subscribeParams = metricTopic.getSubscribeObj();
        }
        this.debug = serverConfig.getVerbose();
        this.quiet = false; //I don't think we want no error printing
        this.mode = mode;
        this.actionTimeout = actionTimeout;
        //hold on to server and metric config references
        this.server = serverConfig;
        this.metricTopic = metricTopic;


        this.metricWriter = monitor.getMetricWriter(
                this.metricTopic.getMetricPath() + this.metricTopic.getMetric_name(),
                this.metricTopic.getMetric_aggregation_type(),
                this.metricTopic.getMetric_time_rollup_type(),
                this.metricTopic.getMetric_cluster_rollup_type()
        );

    }

    public void execute() {
        try {
            // Create the client.
            MemoryPersistence persistence = new MemoryPersistence();
            this.v5Client = new MqttAsyncClient(connectionParams.getHostURI(), connectionParams.getClientID(), persistence);
            this.v5Client.setCallback(this);

            // Connect to the server
            logMessage(String.format("Connecting to MQTT Broker %s, Client ID: %s", v5Client.getServerURI(),
                    v5Client.getClientId()), true);

            IMqttToken connectToken = v5Client.connect(connectionParams.getConOpts());
            connectToken.waitForCompletion(actionTimeout);

            if(mode == Mode.SUB) {
                logMessage(String.format("Subscribing to %s with QoS %d.", subscribeParams.getTopic(), subscribeParams.getQos()), true);
                IMqttToken subToken = this.v5Client.subscribe(subscribeParams.getTopic(), subscribeParams.getQos());
                subToken.waitForCompletion(actionTimeout);
                addShutdownHook();
                while(keepRunning) {
                    // Do nothing
                }
                disconnectClient();
                closeClientAndExit();
            }

        } catch (MqttException ex) {
            logError(String.format("Exception occured whilst attempting to publish: %s", ex.getMessage()));
            ex.printStackTrace();
            closeClientAndExit();
        }

    }



    /**
     * Log a message to the console, nothing fancy.
     *
     * @param message
     * @param isDebug
     */
    private void logMessage(String message, boolean isDebug) {
        if ((this.debug == true && isDebug == true) || isDebug == false) {
            logger.debug(message);
        }
    }

    /**
     * Log an error to the console
     *
     * @param error
     */
    private void logError(String error) {
        if (this.quiet == false) {
            logger.error(error);
        }
    }

    /**
     * Adds a shutdown hook, that will gracefully disconnect the client when a
     * CTRL+C rolls in.
     */
    public void addShutdownHook() {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                keepRunning = false;
            }
        });
    }

    private void disconnectClient() throws MqttException {
        // Disconnect
        logMessage("Disconnecting from server.", true);
        IMqttToken disconnectToken = v5Client.disconnect();
        disconnectToken.waitForCompletion(actionTimeout);
    }

    private void closeClientAndExit() {
        // Close the client
        logMessage("Closing Connection.", true);
        try {
            this.v5Client.close();
            logMessage("Client Closed.", true);
            System.exit(0);
            mainThread.join();
        } catch (MqttException | InterruptedException e) {
            // End the Application
            System.exit(1);
        }

    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        String cause = null;
        if (disconnectResponse.getException().getMessage() != null) {
            cause = disconnectResponse.getException().getMessage();
        } else {
            cause = disconnectResponse.getReasonString();
        }
        if (connectionParams.isAutomaticReconnectEnabled()) {
            logMessage(String.format("The connection to the server was lost, cause: %s. Waiting to reconnect.", cause),
                    true);
        } else {
            logMessage(String.format("The connection to the server was lost, cause: %s. Closing Client", cause), true);
            closeClientAndExit();
        }

    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        logError(String.format("An MQTT error occurred: %s", exception.getMessage()));
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String messageContent = new String(message.getPayload());
        //Unfortunately we get floats conversion will lose precision
        Float f = Float.valueOf(messageContent);
        Long l = f.longValue();
        logMessage("MessageContent: '"+messageContent+"' with length: "+messageContent.length(), true);
        //in theory on continuous running each instance of this executor should be able to do a metric writer call here
        if(this.metricTopic.getMetric_delta()){
            //this is a delta calculation
            l = this.metricTopic.getDeltaMetricsCalculator()
                    .calculateDelta(this.metricTopic.getMetricPath(), BigDecimal.valueOf(l))
                    .longValue();
        }

        this.metricWriter.printMetric(l.toString());
        if (subscribeParams.isVerbose()) {
            logMessage(String.format("%s %s", topic, l.toString()), false);
        } else {
            logMessage(l.toString(), false);
        }
    }

    @Override
    public void deliveryComplete(IMqttToken token) {
        logMessage(String.format("Message %d was delivered.", token.getMessageId()), true);
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        logMessage(String.format("Connection to %s complete. Reconnect=%b", serverURI, reconnect), true);
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
        logError(String.format("Auth packet received, this client does not currently support them. Reason Code: %d.",
                reasonCode));
    }

}