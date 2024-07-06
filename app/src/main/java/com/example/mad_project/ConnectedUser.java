package com.example.mad_project;

public class ConnectedUser {
    private String connectedUserId;
    private String connectedUserName;


    public ConnectedUser() {
    }

    public ConnectedUser(String connectedUserId, String connectedUserName) {
        this.connectedUserId = connectedUserId;
        this.connectedUserName = connectedUserName;
    }

    public String getConnectedUserId() {
        return connectedUserId;
    }

    public void setConnectedUserId(String connectedUserId) {
        this.connectedUserId = connectedUserId;
    }

    public String getConnectedUserName() {
        return connectedUserName;
    }

    public void setConnectedUserName(String connectedUserName) {
        this.connectedUserName = connectedUserName;
    }
}

