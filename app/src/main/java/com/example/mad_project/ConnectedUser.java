package com.example.mad_project;

public class ConnectedUser {
    private String userId;
    private String name;
    private String connectedTo;
    private String connectedToName;


    public ConnectedUser() {
    }

    public ConnectedUser(String userId, String name, String connectedTo) {
        this.userId = userId;
        this.name = name;
        this.connectedTo = connectedTo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConnectedToName() {
        return connectedToName;
    }

    public void setConnectedToName(String connectedToName) {
        this.connectedToName = connectedToName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectedTo() {
        return connectedTo;
    }

    public void setConnectedTo(String connectedTo) {
        this.connectedTo = connectedTo;
    }
}

