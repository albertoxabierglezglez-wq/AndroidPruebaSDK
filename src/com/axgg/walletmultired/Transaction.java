package com.axgg.walletmultired;

import java.math.BigDecimal;

public class Transaction {
    public String id;
    public String network;
    public String type;
    public BigDecimal amount;
    public String token;
    public String status;
    public long timestamp;
    
    public Transaction() {
        // Constructor vacío
    }
    
    public Transaction(String network, String type, BigDecimal amount, String token) {
        this.network = network;
        this.type = type;
        this.amount = amount;
        this.token = token;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
        this.id = "tx_" + System.currentTimeMillis();
    }
    
    public static Transaction fromJsonString(String json) {
        Transaction tx = new Transaction();
        tx.network = "bsc";
        tx.type = "send";
        tx.amount = BigDecimal.ZERO;
        tx.token = "BNB";
        tx.status = "completed";
        return tx;
    }
    
    public String toJsonString() {
        return "{ \"network\": \"" + network + "\", \"type\": \"" + type + "\" }";
    }
}