/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.monitors.mqtt.config;


import java.util.ArrayList;

public class Server {

    private String displayName;
    private String host;



    private String clientID;
    private int keepAlive = 60;
    private String username;
    private String password;
    private Integer qos;

    private Boolean automaticReconnect;
    private Boolean cleanSession;

    private ArrayList<MetricTopic> topics;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean hasHost(){
        return (this.host != null && this.host.length() > 0);
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public boolean hasClientID(){
        return (this.clientID != null && this.clientID.length() > 0);
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean hasKeepAlive(){
        return (this.clientID != null && this.clientID.length() > 0);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean hasUsername(){
        return (this.username != null && this.username.length() > 0);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasPassword(){
        return (this.password != null && this.password.length() > 0);
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean hasQos() {
        return this.qos != null;
    }

    public boolean getAutomaticReconnect() {
        return automaticReconnect;
    }

    public void setAutomaticReconnect(boolean automaticReconnect) {
        this.automaticReconnect = automaticReconnect;
    }

    public boolean hasAutomaticReconnect() {
        return this.automaticReconnect != null;
    }

    public boolean getCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public boolean hasCleanSession() {
        return this.cleanSession != null;
    }

    public ArrayList<MetricTopic> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<MetricTopic> topics) {
        this.topics = topics;
    }

}

