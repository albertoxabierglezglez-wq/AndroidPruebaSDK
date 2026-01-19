package com.axgg.walletmultired;

import android.content.Context;
import android.content.SharedPreferences;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;

public class WalletManager {
    
    private static final String PREFS_NAME = "MyWalletPrefs";
    private static final String KEY_MNEMONIC = "mnemonic_phrase";
    private static final String KEY_ADDRESS_BSC = "address_bsc";
    private static final String KEY_ADDRESS_ETH = "address_eth";
    private static final String KEY_IS_BACKED_UP = "is_backed_up";
    
    private Context context;
    private SharedPreferences prefs;
    
    public WalletManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    // OPERACIONES PRINCIPALES DE WALLET
    
    public boolean createNewWallet() {
        try {
            String mnemonic = generateMnemonic();
            String bscAddress = "0xBSC_" + generateAddressHash(mnemonic, "bsc");
            String ethAddress = "0xETH_" + generateAddressHash(mnemonic, "eth");
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_MNEMONIC, mnemonic);
            editor.putString(KEY_ADDRESS_BSC, bscAddress);
            editor.putString(KEY_ADDRESS_ETH, ethAddress);
            editor.putBoolean(KEY_IS_BACKED_UP, false);
            editor.apply();
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean restoreWallet(String mnemonic) {
        if (mnemonic == null || mnemonic.trim().isEmpty()) {
            return false;
        }
        
        String[] words = mnemonic.trim().split("\\s+");
        if (words.length != 12) {
            return false;
        }
        
        try {
            String bscAddress = "0xBSC_" + generateAddressHash(mnemonic, "bsc");
            String ethAddress = "0xETH_" + generateAddressHash(mnemonic, "eth");
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_MNEMONIC, mnemonic);
            editor.putString(KEY_ADDRESS_BSC, bscAddress);
            editor.putString(KEY_ADDRESS_ETH, ethAddress);
            editor.putBoolean(KEY_IS_BACKED_UP, true);
            editor.apply();
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getAddress(String network) {
        switch (network.toLowerCase()) {
            case "bsc":
                return prefs.getString(KEY_ADDRESS_BSC, null);
            case "eth":
                return prefs.getString(KEY_ADDRESS_ETH, null);
            default:
                return null;
        }
    }
    
    public String getMnemonic() {
        return prefs.getString(KEY_MNEMONIC, null);
    }
    
    public boolean walletExists() {
        return prefs.contains(KEY_MNEMONIC);
    }
    
    public void markAsBackedUp() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_BACKED_UP, true);
        editor.apply();
    }
    
    public boolean isBackedUp() {
        return prefs.getBoolean(KEY_IS_BACKED_UP, false);
    }
    
    public void clearWallet() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
    
    // OPERACIONES DE SALDO Y TRANSACCIONES (SIMULADAS)
    
    public BigDecimal getBalance(String network) {
        if (walletExists()) {
            switch (network.toLowerCase()) {
                case "bsc":
                    return new BigDecimal("5.75");
                case "eth":
                    return new BigDecimal("1.20");
            }
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalBalance() {
        BigDecimal bscBalance = getBalance("bsc");
        BigDecimal ethBalance = getBalance("eth");
        return bscBalance.add(ethBalance);
    }
    
    public Transaction createTransaction(String network, String type, BigDecimal amount, String tokenSymbol) {
        Transaction newTx = new Transaction(network, type, amount, tokenSymbol);
        return newTx;
    }
    
    public ArrayList<Transaction> getTransactions() {
        ArrayList<Transaction> list = new ArrayList<>();
        
        if (walletExists()) {
            list.add(new Transaction("bsc", "receive", new BigDecimal("2.5"), "BNB"));
            list.add(new Transaction("eth", "send", new BigDecimal("0.5"), "ETH"));
        }
        
        return list;
    }
    
    // FUNCIONES INTERNAS DE UTILERIA
    
    private String generateMnemonic() {
        String[] wordList = {
            "alpha", "bravo", "charli", "delta", "echo", "foxtrot",
            "golf", "hotel", "india", "juliet", "kilo", "lima",
            "mike", "november", "oscar", "papa", "quebec", "romeo",
            "sierra", "tango", "uniform", "victor", "whiskey", "xray"
        };
        
        SecureRandom random = new SecureRandom();
        StringBuilder mnemonic = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            if (i > 0) mnemonic.append(" ");
            mnemonic.append(wordList[random.nextInt(wordList.length)]);
        }
        
        return mnemonic.toString();
    }
    
    private String generateAddressHash(String input, String network) throws Exception {
        String data = input + "|" + network;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes("UTF-8"));
        
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < 10 && i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
}