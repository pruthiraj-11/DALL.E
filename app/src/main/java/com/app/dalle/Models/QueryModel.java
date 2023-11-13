package com.app.dalle.Models;

public class QueryModel {
    public static String SENT_BY_USER="user";
    public static String SENT_BY_CHATGPT="chatgpt";

    public String message;
    public String sentBy;

    public QueryModel(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}
