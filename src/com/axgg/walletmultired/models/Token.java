package com.axgg.walletmultired.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Token {
    
    private String symbol;
    private String name;
    private String contractAddress;
    private int decimals;
    private BigDecimal balance;
    private BigDecimal priceUSD;
    private String network;
    
    // Tokens nativos
    public static final Token BNB = new Token("BNB", "Binance Coin", "", 18, "BSC");
    public static final Token ETH = new Token("ETH", "Ethereum", "", 18, "ETH");
    public static final Token USDT_BSC = new Token("USDT", "Tether USD", "0x55d398326f99059fF775485246999027B3197955", 18, "BSC");
    public static final Token USDT_ETH = new Token("USDT", "Tether USD", "0xdAC17F958D2ee523a2206206994597C13D831ec7", 6, "ETH");
    public static final Token USDC_ETH = new Token("USDC", "USD Coin", "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48", 6, "ETH");
    
    public Token(String symbol, String name, String contractAddress, int decimals, String network) {
        this.symbol = symbol;
        this.name = name;
        this.contractAddress = contractAddress;
        this.decimals = decimals;
        this.network = network;
        this.balance = BigDecimal.ZERO;
        this.priceUSD = getDefaultPrice(symbol);
    }
    
    // Getters
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public String getContractAddress() { return contractAddress; }
    public int getDecimals() { return decimals; }
    public String getNetwork() { return network; }
    public BigDecimal getBalance() { return balance; }
    public BigDecimal getPriceUSD() { return priceUSD; }
    
    // Setters
    public void setBalance(BigDecimal balance) { 
        this.balance = balance.setScale(decimals, RoundingMode.HALF_UP);
    }
    
    public void setPriceUSD(BigDecimal priceUSD) {
        this.priceUSD = priceUSD.setScale(6, RoundingMode.HALF_UP);
    }
    
    // Métodos de utilidad
    public BigDecimal getBalanceUSD() {
        return balance.multiply(priceUSD).setScale(2, RoundingMode.HALF_UP);
    }
    
    public String getFormattedBalance() {
        return balance.setScale(getDisplayDecimals(), RoundingMode.HALF_UP).toString() + " " + symbol;
    }
    
    public String getFormattedBalanceUSD() {
        return "$" + getBalanceUSD().toString() + " USD";
    }
    
    private int getDisplayDecimals() {
        if (symbol.equals("USDT") || symbol.equals("USDC")) {
            return 2;
        } else if (symbol.equals("BNB") || symbol.equals("ETH")) {
            return 6;
        }
        return decimals;
    }
    
    private BigDecimal getDefaultPrice(String symbol) {
        switch (symbol) {
            case "BNB":
                return new BigDecimal("300.00");
            case "ETH":
                return new BigDecimal("2000.00");
            case "USDT":
            case "USDC":
                return BigDecimal.ONE;
            default:
                return BigDecimal.ZERO;
        }
    }
    
    public boolean isNative() {
        return contractAddress == null || contractAddress.isEmpty();
    }
    
    public boolean isStablecoin() {
        return symbol.equals("USDT") || symbol.equals("USDC");
    }
    
    public static Token getTokenForNetwork(String symbol, String network) {
        switch (symbol) {
            case "BNB":
                return BNB;
            case "ETH":
                return ETH;
            case "USDT":
                return network.equals("BSC") ? USDT_BSC : USDT_ETH;
            case "USDC":
                return USDC_ETH;
            default:
                return null;
        }
    }
    
    public static Token[] getSupportedTokens(String network) {
        if (network.equals("BSC")) {
            return new Token[]{BNB, USDT_BSC};
        } else if (network.equals("ETH")) {
            return new Token[]{ETH, USDT_ETH, USDC_ETH};
        }
        return new Token[0];
    }
}