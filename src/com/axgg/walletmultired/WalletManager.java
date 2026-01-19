package com.axgg.walletmultired;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import androidx.annotation.NonNull;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WalletManager {
    
    // Constantes
    private static final String KEYSTORE_ALIAS = "WalletMultiRed_Key";
    private static final String PREFS_NAME = "WalletData";
    private static final String TRANSACTION_PREFS = "TransactionData";
    
    // Singleton
    private static WalletManager instance;
    
    // Datos
    private Map<String, Wallet> wallets = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();
    private Context context;
    
    // Preferencias
    private SharedPreferences prefs;
    private SharedPreferences transactionPrefs;
    
    // Constructor privado
    private WalletManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.transactionPrefs = context.getSharedPreferences(TRANSACTION_PREFS, Context.MODE_PRIVATE);
        loadWallets();
        loadTransactions();
    }
    
    // Singleton
    public static synchronized WalletManager getInstance(Context context) {
        if (instance == null) {
            instance = new WalletManager(context);
        }
        return instance;
    }
    
    // ===== MÉTODOS DE WALLETS =====
    
    public Wallet createWallet(String network) {
        try {
            // Generar datos aleatorios para demo (en producción usarías librerías crypto reales)
            String address = generateAddress();
            String privateKey = generatePrivateKey();
            String mnemonic = generateMnemonic();
            
            Wallet wallet = new Wallet(address, privateKey, mnemonic, network);
            
            // Asignar balance inicial aleatorio
            Random rand = new Random();
            if (network.equals("BSC")) {
                wallet.setBnbBalance(new BigDecimal(0.1 + rand.nextDouble() * 1.9)
                    .setScale(6, RoundingMode.HALF_UP));
                wallet.setUsdtBalance(new BigDecimal(100 + rand.nextDouble() * 900)
                    .setScale(2, RoundingMode.HALF_UP));
            } else if (network.equals("ETH")) {
                wallet.setEthBalance(new BigDecimal(0.01 + rand.nextDouble() * 0.19)
                    .setScale(6, RoundingMode.HALF_UP));
                wallet.setUsdtBalance(new BigDecimal(50 + rand.nextDouble() * 450)
                    .setScale(2, RoundingMode.HALF_UP));
            }
            
            // Guardar wallet
            wallets.put(network, wallet);
            saveWallet(wallet);
            
            return wallet;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void createMultiChainWallets() {
        // Crear wallet para BSC
        Wallet bscWallet = createWallet("BSC");
        
        // Crear wallet para ETH (usando misma frase mnemónica para demo)
        if (bscWallet != null) {
            Wallet ethWallet = new Wallet(
                generateAddress(),
                generatePrivateKey(),
                bscWallet.getMnemonic(),  // Misma frase para ambas
                "ETH"
            );
            
            // Balance inicial ETH
            Random rand = new Random();
            ethWallet.setEthBalance(new BigDecimal(0.05 + rand.nextDouble() * 0.15)
                .setScale(6, RoundingMode.HALF_UP));
            ethWallet.setUsdtBalance(new BigDecimal(200 + rand.nextDouble() * 800)
                .setScale(2, RoundingMode.HALF_UP));
            
            wallets.put("ETH", ethWallet);
            saveWallet(ethWallet);
        }
    }
    
    public boolean importWalletFromMnemonic(String mnemonic, String network) {
        try {
            // Validar mnemónico (debe tener 12 palabras)
            String[] words = mnemonic.trim().split("\\s+");
            if (words.length != 12) {
                return false;
            }
            
            // Generar wallet desde mnemónico (en demo es simulado)
            String address = generateAddress();
            String privateKey = generatePrivateKey();
            
            Wallet wallet = new Wallet(address, privateKey, mnemonic, network);
            
            // Balance inicial pequeño
            Random rand = new Random();
            if (network.equals("BSC")) {
                wallet.setBnbBalance(new BigDecimal(0.001).setScale(6, RoundingMode.HALF_UP));
            } else if (network.equals("ETH")) {
                wallet.setEthBalance(new BigDecimal(0.0005).setScale(6, RoundingMode.HALF_UP));
            }
            
            wallets.put(network, wallet);
            saveWallet(wallet);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean importWalletFromPrivateKey(String privateKey, String network) {
        try {
            // Validar formato de clave privada
            if (!privateKey.startsWith("0x") || privateKey.length() != 66) {
                return false;
            }
            
            // Generar address desde private key (simulado)
            String address = generateAddressFromPrivateKey(privateKey);
            
            Wallet wallet = new Wallet(address, privateKey, null, network);
            
            // Balance inicial pequeño
            Random rand = new Random();
            if (network.equals("BSC")) {
                wallet.setBnbBalance(new BigDecimal(0.001).setScale(6, RoundingMode.HALF_UP));
            } else if (network.equals("ETH")) {
                wallet.setEthBalance(new BigDecimal(0.0005).setScale(6, RoundingMode.HALF_UP));
            }
            
            wallets.put(network, wallet);
            saveWallet(wallet);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Wallet getWallet(String network) {
        return wallets.get(network);
    }
    
    public Map<String, Wallet> getAllWallets() {
        return new HashMap<>(wallets);
    }
    
    public boolean hasWallet(String network) {
        return wallets.containsKey(network);
    }
    
    public int getConnectedCount() {
        return wallets.size();
    }
    
    public void disconnectAll() {
        wallets.clear();
        transactions.clear();
        
        // Limpiar preferencias
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        
        SharedPreferences.Editor txEditor = transactionPrefs.edit();
        txEditor.clear();
        txEditor.apply();
    }
    
    // ===== MÉTODOS DE BALANCES =====
    
    public BigDecimal getTotalBalanceUSD() {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Wallet wallet : wallets.values()) {
            // Valores aproximados para demo
            BigDecimal bnbValue = wallet.getBnbBalance().multiply(new BigDecimal("300"));
            BigDecimal ethValue = wallet.getEthBalance().multiply(new BigDecimal("2000"));
            BigDecimal usdtValue = wallet.getUsdtBalance();
            
            total = total.add(bnbValue).add(ethValue).add(usdtValue);
        }
        
        return total.setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getNetworkBalance(String network, String token) {
        Wallet wallet = wallets.get(network);
        if (wallet == null) {
            return BigDecimal.ZERO;
        }
        
        switch (token) {
            case "BNB":
                return wallet.getBnbBalance();
            case "ETH":
                return wallet.getEthBalance();
            case "USDT":
                return wallet.getUsdtBalance();
            default:
                return BigDecimal.ZERO;
        }
    }
    
    // ===== MÉTODOS DE TRANSACCIONES =====
    
    public Transaction createTransaction(String network, String type, BigDecimal amount, String token) {
        try {
            Wallet wallet = wallets.get(network);
            if (wallet == null) {
                return null;
            }
            
            // Verificar saldo suficiente
            BigDecimal balance = getNetworkBalance(network, token);
            if (balance.compareTo(amount) < 0) {
                return null;
            }
            
            // Actualizar balance
            switch (token) {
                case "BNB":
                    wallet.setBnbBalance(balance.subtract(amount));
                    break;
                case "ETH":
                    wallet.setEthBalance(balance.subtract(amount));
                    break;
                case "USDT":
                    wallet.setUsdtBalance(balance.subtract(amount));
                    break;
            }
            
            // Crear transacción
            Transaction transaction = new Transaction(
                generateTransactionHash(),
                network,
                type,
                amount,
                token,
                System.currentTimeMillis()
            );
            
            // Agregar a lista
            transactions.add(0, transaction); // Agregar al inicio
            saveTransaction(transaction);
            
            // Guardar wallet actualizada
            saveWallet(wallet);
            
            return transaction;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public List<Transaction> getTransactionsByNetwork(String network) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction tx : transactions) {
            if (tx.getNetwork().equals(network)) {
                result.add(tx);
            }
        }
        return result;
    }
    
    public int getTransactionCount() {
        return transactions.size();
    }
    
    // ===== MÉTODOS DE BACKUP =====
    
    public String getWalletBackupData(String network) {
        Wallet wallet = wallets.get(network);
        if (wallet == null) {
            return null;
        }
        
        StringBuilder backup = new StringBuilder();
        backup.append("=== WALLET BACKUP ").append(network).append(" ===\n\n");
        backup.append("Network: ").append(network).append("\n");
        backup.append("Address: ").append(wallet.getAddress()).append("\n");
        backup.append("Private Key: ").append(wallet.getPrivateKey()).append("\n");
        if (wallet.getMnemonic() != null) {
            backup.append("Mnemonic: ").append(wallet.getMnemonic()).append("\n");
        }
        backup.append("Created: ").append(wallet.getCreatedAt()).append("\n");
        backup.append("\n=== END BACKUP ===\n");
        
        return backup.toString();
    }
    
    public String getAllWalletsBackupData() {
        if (wallets.isEmpty()) {
            return null;
        }
        
        StringBuilder backup = new StringBuilder();
        backup.append("=== MULTICHAIN WALLET BACKUP ===\n\n");
        backup.append("Total Wallets: ").append(wallets.size()).append("\n");
        backup.append("Backup Date: ").append(System.currentTimeMillis()).append("\n\n");
        
        for (Map.Entry<String, Wallet> entry : wallets.entrySet()) {
            backup.append("--- ").append(entry.getKey()).append(" ---\n");
            Wallet wallet = entry.getValue();
            backup.append("Address: ").append(wallet.getAddress()).append("\n");
            backup.append("Private Key: ").append(wallet.getPrivateKey()).append("\n");
            if (wallet.getMnemonic() != null) {
                backup.append("Mnemonic: ").append(wallet.getMnemonic()).append("\n");
            }
            backup.append("\n");
        }
        
        backup.append("=== END BACKUP ===\n");
        return backup.toString();
    }
    
    // ===== MÉTODOS DE ENCRIPTACIÓN (simplificados para demo) =====
    
    private String encryptData(String data) {
        try {
            // En producción usarías Android KeyStore y AES-GCM
            return Base64.encodeToString(data.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        } catch (Exception e) {
            return data; // Fallback sin encriptación
        }
    }
    
    private String decryptData(String encryptedData) {
        try {
            byte[] decoded = Base64.decode(encryptedData, Base64.DEFAULT);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encryptedData; // Fallback
        }
    }
    
    // ===== MÉTODOS DE PERSISTENCIA =====
    
    private void saveWallet(Wallet wallet) {
        try {
            SharedPreferences.Editor editor = prefs.edit();
            
            String key = "wallet_" + wallet.getNetwork();
            String encryptedData = encryptData(wallet.toJsonString());
            
            editor.putString(key, encryptedData);
            editor.apply();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadWallets() {
        try {
            wallets.clear();
            
            // Cargar BSC wallet
            String bscData = prefs.getString("wallet_BSC", null);
            if (bscData != null) {
                String decrypted = decryptData(bscData);
                Wallet bscWallet = Wallet.fromJsonString(decrypted);
                if (bscWallet != null) {
                    wallets.put("BSC", bscWallet);
                }
            }
            
            // Cargar ETH wallet
            String ethData = prefs.getString("wallet_ETH", null);
            if (ethData != null) {
                String decrypted = decryptData(ethData);
                Wallet ethWallet = Wallet.fromJsonString(decrypted);
                if (ethWallet != null) {
                    wallets.put("ETH", ethWallet);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void saveTransaction(Transaction transaction) {
        try {
            String key = "tx_" + transaction.getHash();
            String encryptedData = encryptData(transaction.toJsonString());
            
            SharedPreferences.Editor editor = transactionPrefs.edit();
            editor.putString(key, encryptedData);
            editor.apply();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadTransactions() {
        try {
            transactions.clear();
            
            Map<String, ?> allEntries = transactionPrefs.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getKey().startsWith("tx_")) {
                    String encryptedData = (String) entry.getValue();
                    String decrypted = decryptData(encryptedData);
                    Transaction tx = Transaction.fromJsonString(decrypted);
                    if (tx != null) {
                        transactions.add(tx);
                    }
                }
            }
            
            // Ordenar por fecha (más reciente primero)
            transactions.sort((t1, t2) -> Long.compare(t2.getTimestamp(), t1.getTimestamp()));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ===== MÉTODOS DE GENERACIÓN (simulados para demo) =====
    
    private String generateAddress() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return "0x" + bytesToHex(bytes);
    }
    
    private String generatePrivateKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return "0x" + bytesToHex(bytes);
    }
    
    private String generateMnemonic() {
        String[] wordList = {
            "abandon", "ability", "able", "about", "above", "absent", "absorb", "abstract", "absurd", "abuse",
            "access", "accident", "account", "accuse", "achieve", "acid", "acoustic", "acquire", "across", "act"
        };
        
        SecureRandom random = new SecureRandom();
        StringBuilder mnemonic = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            if (i > 0) mnemonic.append(" ");
            mnemonic.append(wordList[random.nextInt(wordList.length)]);
        }
        
        return mnemonic.toString();
    }
    
    private String generateTransactionHash() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return "0x" + bytesToHex(bytes);
    }
    
    private String generateAddressFromPrivateKey(String privateKey) {
        // En demo, generamos una address aleatoria
        return generateAddress();
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    // ===== CLASE INTERNA WALLET =====
    public static class Wallet {
        private String address;
        private String privateKey;
        private String mnemonic;
        private String network;
        private BigDecimal bnbBalance = BigDecimal.ZERO;
        private BigDecimal ethBalance = BigDecimal.ZERO;
        private BigDecimal usdtBalance = BigDecimal.ZERO;
        private long createdAt;
        
        public Wallet(String address, String privateKey, String mnemonic, String network) {
            this.address = address;
            this.privateKey = privateKey;
            this.mnemonic = mnemonic;
            this.network = network;
            this.createdAt = System.currentTimeMillis();
        }
        
        // Getters y Setters
        public String getAddress() { return address; }
        public String getPrivateKey() { return privateKey; }
        public String getMnemonic() { return mnemonic; }
        public String getNetwork() { return network; }
        public BigDecimal getBnbBalance() { return bnbBalance; }
        public BigDecimal getEthBalance() { return ethBalance; }
        public BigDecimal getUsdtBalance() { return usdtBalance; }
        public long getCreatedAt() { return createdAt; }
        
        public void setBnbBalance(BigDecimal balance) { this.bnbBalance = balance; }
        public void setEthBalance(BigDecimal balance) { this.ethBalance = balance; }
        public void setUsdtBalance(BigDecimal balance) { this.usdtBalance = balance; }
        
        public String toJsonString() {
            return String.format(
                "{\"address\":\"%s\",\"privateKey\":\"%s\",\"mnemonic\":\"%s\",\"network\":\"%s\"," +
                "\"bnbBalance\":\"%s\",\"ethBalance\":\"%s\",\"usdtBalance\":\"%s\",\"createdAt\":%d}",
                address, privateKey, mnemonic != null ? mnemonic : "", network,
                bnbBalance.toString(), ethBalance.toString(), usdtBalance.toString(), createdAt
            );
        }
        
        public static Wallet fromJsonString(String json) {
            try {
                // Parseo simplificado para demo
                json = json.replace("{", "").replace("}", "").replace("\"", "");
                String[] parts = json.split(",");
                
                String address = "", privateKey = "", mnemonic = "", network = "";
                BigDecimal bnbBalance = BigDecimal.ZERO, ethBalance = BigDecimal.ZERO, usdtBalance = BigDecimal.ZERO;
                long createdAt = 0;
                
                for (String part : parts) {
                    String[] keyValue = part.split(":", 2);
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        String value = keyValue[1].trim();
                        
                        switch (key) {
                            case "address": address = value; break;
                            case "privateKey": privateKey = value; break;
                            case "mnemonic": mnemonic = value; break;
                            case "network": network = value; break;
                            case "bnbBalance": bnbBalance = new BigDecimal(value); break;
                            case "ethBalance": ethBalance = new BigDecimal(value); break;
                            case "usdtBalance": usdtBalance = new BigDecimal(value); break;
                            case "createdAt": createdAt = Long.parseLong(value); break;
                        }
                    }
                }
                
                Wallet wallet = new Wallet(address, privateKey, mnemonic.isEmpty() ? null : mnemonic, network);
                wallet.bnbBalance = bnbBalance;
                wallet.ethBalance = ethBalance;
                wallet.usdtBalance = usdtBalance;
                wallet.createdAt = createdAt;
                
                return wallet;
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}