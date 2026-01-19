package com.axgg.walletmultired.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    
    private String hash;
    private String network;
    private String type; // "sent", "received"
    private BigDecimal amount;
    private String token;
    private long timestamp;
    private String status; // "pending", "confirmed", "failed"
    private String fromAddress;
    private String toAddress;
    private BigDecimal gasFee;
    private String gasToken;
    
    public Transaction(String hash, String network, String type, BigDecimal amount, 
                      String token, long timestamp) {
        this.hash = hash;
        this.network = network;
        this.type = type;
        this.amount = amount.setScale(6, RoundingMode.HALF_UP);
        this.token = token;
        this.timestamp = timestamp;
        this.status = "confirmed"; // Por defecto
        this.fromAddress = "";
        this.toAddress = "";
        this.gasFee = BigDecimal.ZERO;
        this.gasToken = network.equals("BSC") ? "BNB" : "ETH";
    }
    
    // Getters
    public String getHash() { return hash; }
    public String getNetwork() { return network; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getToken() { return token; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public String getFromAddress() { return fromAddress; }
    public String getToAddress() { return toAddress; }
    public BigDecimal getGasFee() { return gasFee; }
    public String getGasToken() { return gasToken; }
    
    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
    public void setToAddress(String toAddress) { this.toAddress = toAddress; }
    public void setGasFee(BigDecimal gasFee) { this.gasFee = gasFee; }
    
    // Métodos de utilidad
    public String getFormattedDate() {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
    
    public String getShortHash() {
        if (hash.length() > 16) {
            return hash.substring(0, 8) + "..." + hash.substring(hash.length() - 8);
        }
        return hash;
    }
    
    public String getDisplayAmount() {
        String symbol = type.equals("sent") ? "-" : "+";
        return symbol + amount.setScale(6, RoundingMode.HALF_UP).toString() + " " + token;
    }
    
    public BigDecimal getUsdValue() {
        // Valores aproximados para demo
        BigDecimal rate = BigDecimal.ONE;
        if (token.equals("BNB")) {
            rate = new BigDecimal("300");
        } else if (token.equals("ETH")) {
            rate = new BigDecimal("2000");
        } else if (token.equals("USDT") || token.equals("USDC")) {
            rate = BigDecimal.ONE;
        }
        
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
    
    public String toJsonString() {
        return String.format(
            "{\"hash\":\"%s\",\"network\":\"%s\",\"type\":\"%s\",\"amount\":\"%s\"," +
            "\"token\":\"%s\",\"timestamp\":%d,\"status\":\"%s\",\"fromAddress\":\"%s\"," +
            "\"toAddress\":\"%s\",\"gasFee\":\"%s\",\"gasToken\":\"%s\"}",
            hash, network, type, amount.toString(), token, timestamp, status,
            fromAddress, toAddress, gasFee.toString(), gasToken
        );
    }
    
    public static Transaction fromJsonString(String json) {
        try {
            // Parseo simplificado para demo
            json = json.replace("{", "").replace("}", "").replace("\"", "");
            String[] parts = json.split(",");
            
            String hash = "", network = "", type = "", token = "", status = "";
            String fromAddress = "", toAddress = "", gasToken = "";
            BigDecimal amount = BigDecimal.ZERO, gasFee = BigDecimal.ZERO;
            long timestamp = 0;
            
            for (String part : parts) {
                String[] keyValue = part.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    
                    switch (key) {
                        case "hash": hash = value; break;
                        case "network": network = value; break;
                        case "type": type = value; break;
                        case "token": token = value; break;
                        case "status": status = value; break;
                        case "fromAddress": fromAddress = value; break;
                        case "toAddress": toAddress = value; break;
                        case "gasToken": gasToken = value; break;
                        case "amount": amount = new BigDecimal(value); break;
                        case "gasFee": gasFee = new BigDecimal(value); break;
                        case "timestamp": timestamp = Long.parseLong(value); break;
                    }
                }
            }
            
            Transaction tx = new Transaction(hash, network, type, amount, token, timestamp);
            tx.status = status;
            tx.fromAddress = fromAddress;
            tx.toAddress = toAddress;
            tx.gasFee = gasFee;
            tx.gasToken = gasToken;
            
            return tx;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Enums para tipos y estados
    public static class Type {
        public static final String SENT = "sent";
        public static final String RECEIVED = "received";
    }
    
    public static class Status {
        public static final String PENDING = "pending";
        public static final String CONFIRMED = "confirmed";
        public static final String FAILED = "failed";
    }
}