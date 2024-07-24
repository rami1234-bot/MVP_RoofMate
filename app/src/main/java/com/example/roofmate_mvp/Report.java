package com.example.roofmate_mvp;

import java.io.Serializable;

public class Report implements Serializable {
    private String userId;
    private String username;
    private String reportText;
    private long timestamp;

    public Report() {
        // Default constructor required for calls to DataSnapshot.getValue(Report.class)
    }

    public Report(String userId, String username, String reportText, long timestamp) {
        this.userId = userId;
        this.username = username;
        this.reportText = reportText;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getReportText() {
        return reportText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}