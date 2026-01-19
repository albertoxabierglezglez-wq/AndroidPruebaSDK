package com.axgg.walletmultired;

import java.math.BigDecimal;

public class Transaction {
    
    public static final String TYPE_SEND = "send";
    public static final String TYPE_RECEIVE = "receive";
    public static final String TYPE_SWAP = "swap";
    
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";
    
    public String id;
    public String network;
    public String type;
    public BigDecimal amount;
    public String tokenSymbol;
    public String status;
    public long timestamp;
    public String fromAddress;
    public String toAddress;
    public String txHash;
    
    public Transaction() {
        this.timestamp = System.currentTimeMillis();
        this.id = generateId();
        this.status = STATUS_PENDING;
    }
    
    public Transaction(String network, String type, BigDecimal amount, String tokenSymbol) {
        this();
        this.network = network;
        this.type = type;
        this.amount = amount;
        this.tokenSymbol = tokenSymbol;
        
        this.txHash = "0xTX" + generateId().toUpperCase();
        this.fromAddress = "0xSender_" + generateId().substring(0, 8);
        this.toAddress = "0xReceiver_" + generateId().substring(8, 16);
    }
    
    private String generateId() {
        return "tx_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 1000);
    }
    
    public void markAsCompleted() {
        this.status = STATUS_COMPLETED;
    }
    
    public void markAsFailed() {
        this.status = STATUS_FAILED;
    }
    
    public String getTypeDescription() {
        switch (type) {
            case TYPE_SEND:
                return "Envio";
            case TYPE_RECEIVE:
                return "Recepcion";
            case TYPE_SWAP:
                return "Intercambio";
            default:
                return "Transaccion";
        }
    }
    
    public String getStatusDescription() {
        switch (status) {
            case STATUS_PENDING:
                return "Pendiente";
            case STATUS_COMPLETED:
                return "Completada";
            case STATUS_FAILED:
                return "Fallida";
            default:
                return "Desconocido";
        }
    }
    
    public String toSimpleString() {
        return String.format("%s: %s %s %s - %s", 
            getTypeDescription(),
            amount.toPlainString(),
            tokenSymbol,
            network.toUpperCase(),
            getStatusDescription());
    }
    
    public static Transaction fromSimpleJson(String json) {
        Transaction tx = new Transaction();
        tx.network = "bsc";
        tx.type = TYPE_RECEIVE;
        tx.amount = new BigDecimal("1.5");
        tx.tokenSymbol = "BNB";
        tx.status = STATUS_COMPLETED;
        tx.txHash = "0xabc123...";
        return tx;
    }
    
    public String toSimpleJson() {
        return String.format(
            "{\"id\":\"%s\",\"network\":\"%s\",\"type\":\"%s\",\"amount\":%s,\"token\":\"%s\",\"status\":\"%s\"}",
            id, network, type, amount.toPlainString(), tokenSymbol, status
        );
    }
}