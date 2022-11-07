package com.example.passwordmanager;

public class LoginData {
    private String appName;
    private String userName;
    private String password;

    public LoginData(String appName, String userName, String password) {
        this.appName = appName;
        this.userName = userName;
        this.password = password;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
