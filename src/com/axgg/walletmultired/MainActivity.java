package com.axgg.walletmultired;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    
    // Constantes de colores (igual que tu HTML)
    private static final int COLOR_GOLD = Color.parseColor("#D4AF37");
    private static final int COLOR_GOLD_LIGHT = Color.parseColor("#F7EF8A");
    private static final int COLOR_GOLD_DARK = Color.parseColor("#B8860B");
    private static final int COLOR_BG_DARK = Color.parseColor("#0F0F0F");
    private static final int COLOR_BG_CARD = Color.parseColor("#1A1A1A");
    private static final int COLOR_TEXT_LIGHT = Color.parseColor("#F5F5F5");
    private static final int COLOR_TEXT_GRAY = Color.parseColor("#A0A0A0");
    private static final int COLOR_SUCCESS = Color.parseColor("#10b981");
    private static final int COLOR_WARNING = Color.parseColor("#f59e0b");
    private static final int COLOR_DANGER = Color.parseColor("#ef4444");
    private static final int COLOR_BSC = Color.parseColor("#F3BA2F");
    private static final int COLOR_ETH = Color.parseColor("#6978FF");
    
    // Componentes UI
    private LinearLayout mainLayout;
    private ScrollView scrollView;
    private LinearLayout setupLayout;
    private LinearLayout dashboardLayout;
    
    // Datos de la aplicación
    private Map<String, WalletData> wallets = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();
    private boolean isBSCConnected = false;
    private boolean isETHConnected = false;
    
    // Preferencias
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "WalletMultiRedPrefs";
    
    // Clase interna para datos de wallet
    private class WalletData {
        String address;
        String privateKey;
        String mnemonic;
        BigDecimal bnbBalance = BigDecimal.ZERO;
        BigDecimal ethBalance = BigDecimal.ZERO;
        BigDecimal usdtBalance = BigDecimal.ZERO;
        Date createdAt;
        
        WalletData(String address, String privateKey, String mnemonic) {
            this.address = address;
            this.privateKey = privateKey;
            this.mnemonic = mnemonic;
            this.createdAt = new Date();
        }
    }
    
    // Clase interna para transacción
    private class Transaction {
        String hash;
        String network;
        String type; // "sent" o "received"
        BigDecimal amount;
        String token;
        Date date;
        String status; // "pending", "confirmed", "failed"
        
        Transaction(String network, String type, BigDecimal amount, String token) {
            this.network = network;
            this.type = type;
            this.amount = amount;
            this.token = token;
            this.date = new Date();
            this.status = "confirmed";
            this.hash = generateRandomHash();
        }
        
        private String generateRandomHash() {
            Random rand = new Random();
            return "0x" + String.format("%064x", rand.nextLong());
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configurar pantalla completa
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Inicializar preferencias
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Crear layout principal
        createMainLayout();
        
        // Cargar datos guardados
        loadSavedData();
        
        // Mostrar pantalla inicial
        showSetupScreen();
    }
    
    private void createMainLayout() {
        // Layout principal con ScrollView
        mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(COLOR_BG_DARK);
        
        // ScrollView para contenido
        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        
        // Layout de contenido
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(16, 16, 16, 16);
        
        // Layout para pantalla de configuración
        setupLayout = new LinearLayout(this);
        setupLayout.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        setupLayout.setOrientation(LinearLayout.VERTICAL);
        
        // Layout para dashboard
        dashboardLayout = new LinearLayout(this);
        dashboardLayout.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        dashboardLayout.setOrientation(LinearLayout.VERTICAL);
        dashboardLayout.setVisibility(View.GONE);
        
        contentLayout.addView(setupLayout);
        contentLayout.addView(dashboardLayout);
        scrollView.addView(contentLayout);
        mainLayout.addView(scrollView);
        
        setContentView(mainLayout);
    }
    
    private void showSetupScreen() {
        setupLayout.removeAllViews();
        dashboardLayout.setVisibility(View.GONE);
        setupLayout.setVisibility(View.VISIBLE);
        
        // Card principal
        CardView setupCard = createCard();
        setupCard.setCardBackgroundColor(COLOR_BG_CARD);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 24);
        setupCard.setLayoutParams(cardParams);
        
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(24, 24, 24, 24);
        
        // Icono
        TextView icon = new TextView(this);
        icon.setText("👛");
        icon.setTextSize(48);
        icon.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        iconParams.gravity = Gravity.CENTER_HORIZONTAL;
        iconParams.setMargins(0, 0, 0, 16);
        icon.setLayoutParams(iconParams);
        
        // Título
        TextView title = new TextView(this);
        title.setText("Wallet Multi-Red AXGG®");
        title.setTextColor(COLOR_GOLD);
        title.setTextSize(24);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, 0, 0, 8);
        title.setLayoutParams(titleParams);
        
        // Subtítulo
        TextView subtitle = new TextView(this);
        subtitle.setText("Gestiona BSC y Ethereum en una sola wallet");
        subtitle.setTextColor(COLOR_TEXT_GRAY);
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.setMargins(0, 0, 0, 24);
        subtitle.setLayoutParams(subtitleParams);
        
        // Badges de redes
        LinearLayout badgesLayout = new LinearLayout(this);
        badgesLayout.setOrientation(LinearLayout.HORIZONTAL);
        badgesLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams badgesParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        badgesParams.setMargins(0, 0, 0, 32);
        badgesParams.gravity = Gravity.CENTER_HORIZONTAL;
        badgesLayout.setLayoutParams(badgesParams);
        
        // Badge BSC
        TextView bscBadge = createBadge("BSC", COLOR_BSC);
        LinearLayout.LayoutParams bscParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        bscParams.setMargins(0, 0, 8, 0);
        bscBadge.setLayoutParams(bscParams);
        
        // Badge ETH
        TextView ethBadge = createBadge("ETH", COLOR_ETH);
        
        badgesLayout.addView(bscBadge);
        badgesLayout.addView(ethBadge);
        
        // Botón Crear Wallets
        Button createBtn = createButton("➕ Crear Wallets Multi-Red", COLOR_GOLD, COLOR_BG_DARK);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMultiChainWallets();
            }
        });
        LinearLayout.LayoutParams createParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        createParams.setMargins(0, 0, 0, 12);
        createBtn.setLayoutParams(createParams);
        
        // Botón Importar
        Button importBtn = createButton("📁 Importar Wallets", COLOR_BG_CARD, COLOR_TEXT_LIGHT);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportDialog();
            }
        });
        
        // Información
        TextView infoText = new TextView(this);
        infoText.setText("⚠️ Se crearán wallets separadas para BSC y Ethereum automáticamente");
        infoText.setTextColor(COLOR_TEXT_GRAY);
        infoText.setTextSize(12);
        infoText.setGravity(Gravity.CENTER);
        infoText.setPadding(16, 16, 16, 16);
        infoText.setBackgroundColor(Color.parseColor("#2A1A1F"));
        infoText.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        
        // Agregar elementos al card
        cardContent.addView(icon);
        cardContent.addView(title);
        cardContent.addView(subtitle);
        cardContent.addView(badgesLayout);
        cardContent.addView(createBtn);
        cardContent.addView(importBtn);
        cardContent.addView(infoText);
        
        setupCard.addView(cardContent);
        setupLayout.addView(setupCard);
    }
    
    private void showDashboard() {
        setupLayout.setVisibility(View.GONE);
        dashboardLayout.removeAllViews();
        dashboardLayout.setVisibility(View.VISIBLE);
        
        // Header con logo y estado
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);
        headerLayout.setPadding(0, 0, 0, 24);
        
        // Logo
        LinearLayout logoLayout = new LinearLayout(this);
        logoLayout.setOrientation(LinearLayout.HORIZONTAL);
        logoLayout.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView logoIcon = new TextView(this);
        logoIcon.setText("Ξ");
        logoIcon.setTextColor(COLOR_GOLD);
        logoIcon.setTextSize(20);
        
        TextView logoText = new TextView(this);
        logoText.setText("Wallet Multi-Red AXGG®");
        logoText.setTextColor(COLOR_GOLD);
        logoText.setTextSize(16);
        logoText.setTypeface(null, Typeface.BOLD);
        logoText.setPadding(8, 0, 0, 0);
        
        logoLayout.addView(logoIcon);
        logoLayout.addView(logoText);
        
        // Estado de conexión
        TextView statusText = new TextView(this);
        int connectedCount = (isBSCConnected ? 1 : 0) + (isETHConnected ? 1 : 0);
        statusText.setText(connectedCount + "/2 Conectadas");
        statusText.setTextColor(connectedCount == 2 ? COLOR_SUCCESS : 
                               connectedCount == 1 ? COLOR_WARNING : COLOR_DANGER);
        statusText.setTextSize(12);
        statusText.setPadding(16, 8, 16, 8);
        statusText.setBackgroundColor(Color.parseColor("#1A1A1A"));
        statusText.setGravity(Gravity.CENTER);
        
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        statusParams.gravity = Gravity.END;
        statusText.setLayoutParams(statusParams);
        
        headerLayout.addView(logoLayout);
        headerLayout.addView(statusText);
        dashboardLayout.addView(headerLayout);
        
        // Card de Balance Total
        CardView totalBalanceCard = createCard();
        totalBalanceCard.setCardBackgroundColor(COLOR_BG_CARD);
        
        LinearLayout balanceLayout = new LinearLayout(this);
        balanceLayout.setOrientation(LinearLayout.HORIZONTAL);
        balanceLayout.setPadding(24, 24, 24, 24);
        balanceLayout.setGravity(Gravity.CENTER_VERTICAL);
        
        LinearLayout balanceTextLayout = new LinearLayout(this);
        balanceTextLayout.setOrientation(LinearLayout.VERTICAL);
        
        TextView balanceLabel = new TextView(this);
        balanceLabel.setText("Balance Total");
        balanceLabel.setTextColor(COLOR_TEXT_GRAY);
        balanceLabel.setTextSize(14);
        
        TextView balanceValue = new TextView(this);
        BigDecimal total = calculateTotalBalance();
        balanceValue.setText("$" + total.setScale(2, RoundingMode.HALF_UP).toString() + " USD");
        balanceValue.setTextColor(COLOR_TEXT_LIGHT);
        balanceValue.setTextSize(28);
        balanceValue.setTypeface(null, Typeface.BOLD);
        
        balanceTextLayout.addView(balanceLabel);
        balanceTextLayout.addView(balanceValue);
        
        TextView chartIcon = new TextView(this);
        chartIcon.setText("📊");
        chartIcon.setTextSize(32);
        chartIcon.setPadding(16, 0, 0, 0);
        
        balanceLayout.addView(balanceTextLayout);
        balanceLayout.addView(chartIcon);
        totalBalanceCard.addView(balanceLayout);
        dashboardLayout.addView(totalBalanceCard);
        
        // Grid de balances por red
        LinearLayout gridLayout = new LinearLayout(this);
        gridLayout.setOrientation(LinearLayout.HORIZONTAL);
        gridLayout.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        
        // Card BSC
        CardView bscCard = createBalanceCard("BSC", "BNB", 
            wallets.containsKey("BSC") ? wallets.get("BSC").bnbBalance : BigDecimal.ZERO, 
            COLOR_BSC, isBSCConnected);
        bscCard.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
        ));
        
        // Card ETH
        CardView ethCard = createBalanceCard("ETH", "ETH", 
            wallets.containsKey("ETH") ? wallets.get("ETH").ethBalance : BigDecimal.ZERO, 
            COLOR_ETH, isETHConnected);
        ethCard.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
        ));
        
        gridLayout.addView(bscCard);
        gridLayout.addView(ethCard);
        dashboardLayout.addView(gridLayout);
        
        // Grid de acciones
        LinearLayout actionsGrid = new LinearLayout(this);
        actionsGrid.setOrientation(LinearLayout.HORIZONTAL);
        actionsGrid.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        
        String[] actions = {"Enviar", "Recibir", "Backup", "Desconectar"};
        String[] icons = {"✈️", "📱", "🛡️", "🚪"};
        int[] colors = {COLOR_GOLD, COLOR_SUCCESS, Color.parseColor("#8B5CF6"), COLOR_DANGER};
        
        for (int i = 0; i < actions.length; i++) {
            CardView actionCard = createActionCard(icons[i], actions[i], colors[i]);
            actionCard.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
            ));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) actionCard.getLayoutParams();
            params.setMargins(i == 0 ? 0 : 8, 0, i == actions.length - 1 ? 0 : 8, 0);
            actionCard.setLayoutParams(params);
            
            final int index = i;
            actionCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleActionClick(index);
                }
            });
            
            actionsGrid.addView(actionCard);
        }
        dashboardLayout.addView(actionsGrid);
        
        // Historial de transacciones
        CardView historyCard = createCard();
        historyCard.setCardBackgroundColor(COLOR_BG_CARD);
        LinearLayout.LayoutParams historyParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        historyParams.setMargins(0, 24, 0, 0);
        historyCard.setLayoutParams(historyParams);
        
        LinearLayout historyContent = new LinearLayout(this);
        historyContent.setOrientation(LinearLayout.VERTICAL);
        historyContent.setPadding(16, 16, 16, 16);
        
        // Header del historial
        LinearLayout historyHeader = new LinearLayout(this);
        historyHeader.setOrientation(LinearLayout.HORIZONTAL);
        historyHeader.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView historyTitle = new TextView(this);
        historyTitle.setText("Historial de Transacciones");
        historyTitle.setTextColor(COLOR_GOLD);
        historyTitle.setTextSize(16);
        historyTitle.setTypeface(null, Typeface.BOLD);
        
        TextView transactionCount = new TextView(this);
        transactionCount.setText(transactions.size() + " transacciones");
        transactionCount.setTextColor(COLOR_TEXT_GRAY);
        transactionCount.setTextSize(12);
        
        LinearLayout.LayoutParams countParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        countParams.gravity = Gravity.END;
        transactionCount.setLayoutParams(countParams);
        
        historyHeader.addView(historyTitle);
        historyHeader.addView(transactionCount);
        
        historyContent.addView(historyHeader);
        
        // Lista de transacciones
        if (transactions.isEmpty()) {
            LinearLayout emptyLayout = new LinearLayout(this);
            emptyLayout.setOrientation(LinearLayout.VERTICAL);
            emptyLayout.setGravity(Gravity.CENTER);
            emptyLayout.setPadding(0, 32, 0, 32);
            
            TextView emptyIcon = new TextView(this);
            emptyIcon.setText("💱");
            emptyIcon.setTextSize(48);
            emptyIcon.setGravity(Gravity.CENTER);
            
            TextView emptyTitle = new TextView(this);
            emptyTitle.setText("No hay transacciones");
            emptyTitle.setTextColor(COLOR_TEXT_LIGHT);
            emptyTitle.setTextSize(16);
            emptyTitle.setGravity(Gravity.CENTER);
            emptyTitle.setPadding(0, 16, 0, 8);
            
            TextView emptySubtitle = new TextView(this);
            emptySubtitle.setText("Realiza tu primera transacción para ver el historial");
            emptySubtitle.setTextColor(COLOR_TEXT_GRAY);
            emptySubtitle.setTextSize(12);
            emptySubtitle.setGravity(Gravity.CENTER);
            
            emptyLayout.addView(emptyIcon);
            emptyLayout.addView(emptyTitle);
            emptyLayout.addView(emptySubtitle);
            historyContent.addView(emptyLayout);
        } else {
            for (Transaction tx : transactions) {
                historyContent.addView(createTransactionItem(tx));
            }
        }
        
        historyCard.addView(historyContent);
        dashboardLayout.addView(historyCard);
    }
    
    private CardView createBalanceCard(String network, String symbol, BigDecimal balance, int color, boolean isConnected) {
        CardView card = createCard();
        card.setCardBackgroundColor(color);
        
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(16, 16, 16, 16);
        
        // Network indicator
        TextView networkIndicator = new TextView(this);
        networkIndicator.setText(network);
        networkIndicator.setTextColor(Color.BLACK);
        networkIndicator.setTextSize(10);
        networkIndicator.setTypeface(null, Typeface.BOLD);
        networkIndicator.setPadding(8, 4, 8, 4);
        networkIndicator.setBackgroundColor(Color.parseColor("#40000000"));
        networkIndicator.setGravity(Gravity.CENTER);
        
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        indicatorParams.gravity = Gravity.END;
        networkIndicator.setLayoutParams(indicatorParams);
        
        // Balance info
        LinearLayout balanceInfo = new LinearLayout(this);
        balanceInfo.setOrientation(LinearLayout.HORIZONTAL);
        balanceInfo.setGravity(Gravity.CENTER_VERTICAL);
        
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        
        TextView label = new TextView(this);
        label.setText("Saldo " + network);
        label.setTextColor(Color.parseColor("#80000000"));
        label.setTextSize(12);
        
        TextView value = new TextView(this);
        if (isConnected) {
            value.setText(balance.setScale(6, RoundingMode.HALF_UP).toString() + " " + symbol);
            value.setTextColor(Color.BLACK);
        } else {
            value.setText("No conectada");
            value.setTextColor(Color.parseColor("#60000000"));
        }
        value.setTextSize(16);
        value.setTypeface(null, Typeface.BOLD);
        
        TextView usdValue = new TextView(this);
        BigDecimal usd = balance.multiply(new BigDecimal(network.equals("BSC") ? "300" : "2000"));
        usdValue.setText("$" + usd.setScale(2, RoundingMode.HALF_UP).toString() + " USD");
        usdValue.setTextColor(Color.parseColor("#80000000"));
        usdValue.setTextSize(12);
        
        textLayout.addView(label);
        textLayout.addView(value);
        textLayout.addView(usdValue);
        
        TextView icon = new TextView(this);
        icon.setText(network.equals("BSC") ? "B" : "Ξ");
        icon.setTextSize(24);
        icon.setTextColor(Color.parseColor("#80000000"));
        icon.setPadding(16, 0, 0, 0);
        
        balanceInfo.addView(textLayout);
        balanceInfo.addView(icon);
        
        content.addView(networkIndicator);
        content.addView(balanceInfo);
        card.addView(content);
        
        return card;
    }
    
    private CardView createActionCard(String icon, String text, int color) {
        CardView card = createCard();
        card.setCardBackgroundColor(COLOR_BG_CARD);
        
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);
        content.setPadding(16, 16, 16, 16);
        
        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(24);
        iconView.setTextColor(color);
        iconView.setGravity(Gravity.CENTER);
        
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(COLOR_TEXT_LIGHT);
        textView.setTextSize(12);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 8, 0, 0);
        
        content.addView(iconView);
        content.addView(textView);
        card.addView(content);
        
        return card;
    }
    
    private LinearLayout createTransactionItem(Transaction tx) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(12, 12, 12, 12);
        item.setBackgroundColor(COLOR_BG_CARD);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        item.setLayoutParams(params);
        
        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView typeBadge = new TextView(this);
        typeBadge.setText(tx.type.equals("received") ? "↓ Recibido" : "↑ Enviado");
        typeBadge.setTextColor(tx.type.equals("received") ? COLOR_SUCCESS : COLOR_DANGER);
        typeBadge.setTextSize(12);
        typeBadge.setTypeface(null, Typeface.BOLD);
        
        TextView networkBadge = createBadge(tx.network, 
            tx.network.equals("BSC") ? COLOR_BSC : COLOR_ETH);
        networkBadge.setTextSize(10);
        
        TextView dateText = new TextView(this);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        dateText.setText(sdf.format(tx.date));
        dateText.setTextColor(COLOR_TEXT_GRAY);
        dateText.setTextSize(10);
        
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dateParams.gravity = Gravity.END;
        dateText.setLayoutParams(dateParams);
        
        header.addView(typeBadge);
        header.addView(networkBadge);
        header.addView(dateText);
        
        // Body
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        body.setGravity(Gravity.CENTER_VERTICAL);
        body.setPadding(0, 8, 0, 8);
        
        TextView hashText = new TextView(this);
        hashText.setText(tx.hash.substring(0, 10) + "..." + tx.hash.substring(tx.hash.length() - 8));
        hashText.setTextColor(COLOR_TEXT_GRAY);
        hashText.setTextSize(10);
        hashText.setTypeface(Typeface.MONOSPACE);
        
        TextView amountText = new TextView(this);
        amountText.setText((tx.type.equals("received") ? "+" : "-") + 
                          tx.amount.setScale(6, RoundingMode.HALF_UP).toString() + " " + tx.token);
        amountText.setTextColor(tx.type.equals("received") ? COLOR_SUCCESS : COLOR_DANGER);
        amountText.setTextSize(14);
        amountText.setTypeface(null, Typeface.BOLD);
        
        LinearLayout.LayoutParams amountParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        amountParams.gravity = Gravity.END;
        amountText.setLayoutParams(amountParams);
        
        body.addView(hashText);
        body.addView(amountText);
        
        // Footer
        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        footer.setGravity(Gravity.END);
        
        TextView statusBadge = new TextView(this);
        statusBadge.setText(tx.status.equals("confirmed") ? "✅ Confirmado" : 
                           tx.status.equals("pending") ? "⏳ Pendiente" : "❌ Fallido");
        statusBadge.setTextColor(tx.status.equals("confirmed") ? COLOR_SUCCESS : 
                                tx.status.equals("pending") ? COLOR_WARNING : COLOR_DANGER);
        statusBadge.setTextSize(10);
        statusBadge.setPadding(8, 4, 8, 4);
        statusBadge.setBackgroundColor(Color.parseColor("#20000000"));
        
        footer.addView(statusBadge);
        
        item.addView(header);
        item.addView(body);
        item.addView(footer);
        
        return item;
    }
    
    private void handleActionClick(int index) {
        switch (index) {
            case 0: // Enviar
                showSendDialog();
                break;
            case 1: // Recibir
                showReceiveDialog();
                break;
            case 2: // Backup
                showBackupDialog();
                break;
            case 3: // Desconectar
                showDisconnectDialog();
                break;
        }
    }
    
    private void createMultiChainWallets() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🚀 Crear Wallets Multi-Red");
        builder.setMessage("¿Crear nuevas wallets para BSC y Ethereum?\n\nSe generarán automáticamente direcciones separadas para cada red.");
        
        builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Generar datos de prueba (en producción usarías crypto real)
                String bscAddress = "0x" + generateRandomHex(40);
                String ethAddress = "0x" + generateRandomHex(40);
                String mnemonic = generateMnemonic();
                
                // Crear wallets
                wallets.put("BSC", new WalletData(bscAddress, "private_key_bsc", mnemonic));
                wallets.put("ETH", new WalletData(ethAddress, "private_key_eth", mnemonic));
                
                // Asignar balances de prueba
                wallets.get("BSC").bnbBalance = new BigDecimal("0.543210");
                wallets.get("BSC").usdtBalance = new BigDecimal("1250.75");
                wallets.get("ETH").ethBalance = new BigDecimal("0.275000");
                wallets.get("ETH").usdtBalance = new BigDecimal("850.50");
                
                isBSCConnected = true;
                isETHConnected = true;
                
                // Guardar datos
                saveData();
                
                // Mostrar dashboard
                showDashboard();
                
                Toast.makeText(MainActivity.this, "✅ Wallets creadas exitosamente", Toast.LENGTH_LONG).show();
                
                // Mostrar backup automático
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showBackupDialog();
                    }
                }, 1000);
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void showImportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📁 Importar Wallet");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        
        TextView info = new TextView(this);
        info.setText("Selecciona el método de importación:");
        info.setTextColor(COLOR_TEXT_LIGHT);
        info.setTextSize(16);
        info.setPadding(0, 0, 0, 24);
        
        Button btnMnemonic = createButton("📝 Frase de 12 palabras", COLOR_BG_CARD, COLOR_TEXT_LIGHT);
        btnMnemonic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMnemonicImportDialog();
            }
        });
        
        Button btnPrivateKey = createButton("🔑 Clave Privada", COLOR_BG_CARD, COLOR_TEXT_LIGHT);
        btnPrivateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrivateKeyImportDialog();
            }
        });
        
        layout.addView(info);
        layout.addView(btnMnemonic);
        layout.addView(btnPrivateKey);
        
        builder.setView(layout);
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    private void showSendDialog() {
        if (!isBSCConnected && !isETHConnected) {
            Toast.makeText(this, "Primero conecta al menos una wallet", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✈️ Enviar Fondos");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);
        
        // Selector de red
        TextView networkLabel = new TextView(this);
        networkLabel.setText("Red:");
        networkLabel.setTextColor(COLOR_TEXT_LIGHT);
        networkLabel.setPadding(0, 0, 0, 8);
        
        final EditText networkInput = new EditText(this);
        networkInput.setHint("BSC o ETH");
        networkInput.setTextColor(COLOR_TEXT_LIGHT);
        networkInput.setHintTextColor(COLOR_TEXT_GRAY);
        networkInput.setBackground(createEditTextBackground());
        
        // Dirección destino
        TextView addressLabel = new TextView(this);
        addressLabel.setText("Dirección destino:");
        addressLabel.setTextColor(COLOR_TEXT_LIGHT);
        addressLabel.setPadding(0, 16, 0, 8);
        
        final EditText addressInput = new EditText(this);
        addressInput.setHint("0x...");
        addressInput.setTextColor(COLOR_TEXT_LIGHT);
        addressInput.setHintTextColor(COLOR_TEXT_GRAY);
        addressInput.setBackground(createEditTextBackground());
        
        // Cantidad
        TextView amountLabel = new TextView(this);
        amountLabel.setText("Cantidad:");
        amountLabel.setTextColor(COLOR_TEXT_LIGHT);
        amountLabel.setPadding(0, 16, 0, 8);
        
        final EditText amountInput = new EditText(this);
        amountInput.setHint("0.0");
        amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        amountInput.setTextColor(COLOR_TEXT_LIGHT);
        amountInput.setHintTextColor(COLOR_TEXT_GRAY);
        amountInput.setBackground(createEditTextBackground());
        
        layout.addView(networkLabel);
        layout.addView(networkInput);
        layout.addView(addressLabel);
        layout.addView(addressInput);
        layout.addView(amountLabel);
        layout.addView(amountInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String network = networkInput.getText().toString().trim().toUpperCase();
                String address = addressInput.getText().toString().trim();
                String amountStr = amountInput.getText().toString().trim();
                
                if (network.isEmpty() || address.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (!network.equals("BSC") && !network.equals("ETH")) {
                    Toast.makeText(MainActivity.this, "Red debe ser BSC o ETH", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (!address.startsWith("0x") || address.length() != 42) {
                    Toast.makeText(MainActivity.this, "Dirección inválida", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                try {
                    BigDecimal amount = new BigDecimal(amountStr);
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        Toast.makeText(MainActivity.this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Crear transacción
                    Transaction tx = new Transaction(network, "sent", amount, network.equals("BSC") ? "BNB" : "ETH");
                    transactions.add(0, tx); // Agregar al inicio
                    
                    // Actualizar balance
                    if (network.equals("BSC") && wallets.containsKey("BSC")) {
                        wallets.get("BSC").bnbBalance = wallets.get("BSC").bnbBalance.subtract(amount);
                    } else if (network.equals("ETH") && wallets.containsKey("ETH")) {
                        wallets.get("ETH").ethBalance = wallets.get("ETH").ethBalance.subtract(amount);
                    }
                    
                    // Guardar y actualizar UI
                    saveData();
                    showDashboard();
                    
                    Toast.makeText(MainActivity.this, "✅ Transacción enviada", Toast.LENGTH_LONG).show();
                    
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    private void showReceiveDialog() {
        if (!isBSCConnected && !isETHConnected) {
            Toast.makeText(this, "Primero conecta al menos una wallet", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📱 Recibir Fondos");
        
        // Crear pestañas
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        
        // Tabs
        LinearLayout tabLayout = new LinearLayout(this);
        tabLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        final Button btnBSC = createTabButton("BSC");
        final Button btnETH = createTabButton("ETH");
        
        btnBSC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBSC.setBackgroundColor(COLOR_GOLD);
                btnBSC.setTextColor(COLOR_BG_DARK);
                btnETH.setBackgroundColor(COLOR_BG_CARD);
                btnETH.setTextColor(COLOR_TEXT_LIGHT);
                // Aquí cambiarías el contenido según la pestaña
            }
        });
        
        btnETH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnETH.setBackgroundColor(COLOR_GOLD);
                btnETH.setTextColor(COLOR_BG_DARK);
                btnBSC.setBackgroundColor(COLOR_BG_CARD);
                btnBSC.setTextColor(COLOR_TEXT_LIGHT);
            }
        });
        
        tabLayout.addView(btnBSC);
        tabLayout.addView(btnETH);
        
        // Contenido
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);
        content.setPadding(32, 32, 32, 32);
        
        // QR Placeholder
        TextView qrPlaceholder = new TextView(this);
        qrPlaceholder.setText("🔲\nQR Code");
        qrPlaceholder.setTextSize(48);
        qrPlaceholder.setGravity(Gravity.CENTER);
        qrPlaceholder.setPadding(0, 0, 0, 24);
        
        // Dirección
        TextView addressLabel = new TextView(this);
        addressLabel.setText("Tu dirección:");
        addressLabel.setTextColor(COLOR_TEXT_GRAY);
        addressLabel.setGravity(Gravity.CENTER);
        
        final TextView addressText = new TextView(this);
        String address = isBSCConnected ? wallets.get("BSC").address : wallets.get("ETH").address;
        addressText.setText(address);
        addressText.setTextColor(COLOR_TEXT_LIGHT);
        addressText.setTextSize(12);
        addressText.setTypeface(Typeface.MONOSPACE);
        addressText.setGravity(Gravity.CENTER);
        addressText.setPadding(0, 8, 0, 24);
        
        // Botón copiar
        Button copyBtn = createButton("📋 Copiar Dirección", COLOR_GOLD, COLOR_BG_DARK);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboard = 
                    (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Address", addressText.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "✅ Dirección copiada", Toast.LENGTH_SHORT).show();
            }
        });
        
        content.addView(qrPlaceholder);
        content.addView(addressLabel);
        content.addView(addressText);
        content.addView(copyBtn);
        
        mainLayout.addView(tabLayout);
        mainLayout.addView(content);
        
        builder.setView(mainLayout);
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }
    
    private void showBackupDialog() {
        if (!isBSCConnected && !isETHConnected) {
            Toast.makeText(this, "No hay wallets para hacer backup", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🛡️ Backup Multi-Red");
        
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);
        
        for (Map.Entry<String, WalletData> entry : wallets.entrySet()) {
            String network = entry.getKey();
            WalletData wallet = entry.getValue();
            
            // Card por red
            CardView card = createCard();
            card.setCardBackgroundColor(COLOR_BG_CARD);
            card.setPadding(16, 16, 16, 16);
            
            LinearLayout cardContent = new LinearLayout(this);
            cardContent.setOrientation(LinearLayout.VERTICAL);
            
            // Título de red
            TextView networkTitle = new TextView(this);
            networkTitle.setText("Red " + network);
            networkTitle.setTextColor(network.equals("BSC") ? COLOR_BSC : COLOR_ETH);
            networkTitle.setTextSize(18);
            networkTitle.setTypeface(null, Typeface.BOLD);
            networkTitle.setPadding(0, 0, 0, 16);
            
            // Dirección
            TextView addressLabel = new TextView(this);
            addressLabel.setText("Dirección:");
            addressLabel.setTextColor(COLOR_TEXT_GRAY);
            
            TextView addressValue = new TextView(this);
            addressValue.setText(wallet.address);
            addressValue.setTextColor(COLOR_TEXT_LIGHT);
            addressValue.setTypeface(Typeface.MONOSPACE);
            addressValue.setTextSize(12);
            addressValue.setPadding(0, 4, 0, 16);
            
            // Frase mnemónica (oculta por defecto)
            TextView mnemonicLabel = new TextView(this);
            mnemonicLabel.setText("Frase de recuperación:");
            mnemonicLabel.setTextColor(COLOR_TEXT_GRAY);
            
            final TextView mnemonicValue = new TextView(this);
            mnemonicValue.setText("•••• •••• •••• •••• •••• •••• •••• •••• •••• •••• •••• ••••");
            mnemonicValue.setTextColor(COLOR_TEXT_GRAY);
            mnemonicValue.setPadding(0, 4, 0, 16);
            
            Button showMnemonicBtn = createButton("👁️ Mostrar Frase", COLOR_BG_CARD, COLOR_TEXT_LIGHT);
            showMnemonicBtn.setOnClickListener(new View.OnClickListener() {
                boolean isVisible = false;
                
                @Override
                public void onClick(View v) {
                    if (!isVisible) {
                        mnemonicValue.setText(wallet.mnemonic);
                        mnemonicValue.setTextColor(COLOR_SUCCESS);
                        showMnemonicBtn.setText("🙈 Ocultar Frase");
                    } else {
                        mnemonicValue.setText("•••• •••• •••• •••• •••• •••• •••• •••• •••• •••• •••• ••••");
                        mnemonicValue.setTextColor(COLOR_TEXT_GRAY);
                        showMnemonicBtn.setText("👁️ Mostrar Frase");
                    }
                    isVisible = !isVisible;
                }
            });
            
            cardContent.addView(networkTitle);
            cardContent.addView(addressLabel);
            cardContent.addView(addressValue);
            cardContent.addView(mnemonicLabel);
            cardContent.addView(mnemonicValue);
            cardContent.addView(showMnemonicBtn);
            
            card.addView(cardContent);
            
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 16);
            card.setLayoutParams(cardParams);
            
            layout.addView(card);
        }
        
        // Advertencia de seguridad
        TextView warning = new TextView(this);
        warning.setText("⚠️ GUARDA ESTA INFORMACIÓN EN UN LUGAR SEGURO. Cualquiera con acceso a estos datos puede controlar tus fondos.");
        warning.setTextColor(COLOR_WARNING);
        warning.setTextSize(12);
        warning.setPadding(16, 16, 16, 16);
        warning.setBackgroundColor(Color.parseColor("#332A1A1F"));
        
        layout.addView(warning);
        
        scrollView.addView(layout);
        builder.setView(scrollView);
        
        builder.setPositiveButton("✅ Entendido", null);
        builder.show();
    }
    
    private void showDisconnectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🚪 Desconectar");
        builder.setMessage("¿Desconectar todas las wallets?\n\nSe cerrarán todas las sesiones y se eliminarán los datos locales. Necesitarás importar tus wallets nuevamente.");
        
        builder.setPositiveButton("Desconectar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wallets.clear();
                isBSCConnected = false;
                isETHConnected = false;
                transactions.clear();
                
                // Limpiar preferencias
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                
                showSetupScreen();
                Toast.makeText(MainActivity.this, "✅ Todas las wallets desconectadas", Toast.LENGTH_LONG).show();
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    // ===== MÉTODOS UTILITARIOS =====
    
    private CardView createCard() {
        CardView card = new CardView(this);
        card.setCardElevation(8);
        card.setRadius(16);
        card.setCardBackgroundColor(COLOR_BG_CARD);
        card.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return card;
    }
    
    private Button createButton(String text, int bgColor, int textColor) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(bgColor);
        button.setTextColor(textColor);
        button.setAllCaps(false);
        button.setPadding(16, 16, 16, 16);
        button.setTextSize(14);
        button.setTypeface(null, Typeface.BOLD);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        button.setLayoutParams(params);
        
        return button;
    }
    
    private Button createTabButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(COLOR_BG_CARD);
        button.setTextColor(COLOR_TEXT_LIGHT);
        button.setAllCaps(false);
        button.setPadding(24, 12, 24, 12);
        button.setTextSize(14);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
        );
        button.setLayoutParams(params);
        
        return button;
    }
    
    private TextView createBadge(String text, int color) {
        TextView badge = new TextView(this);
        badge.setText(text);
        badge.setTextColor(color);
        badge.setTextSize(10);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setPadding(8, 4, 8, 4);
        badge.setBackgroundColor(Color.argb(30, Color.red(color), Color.green(color), Color.blue(color)));
        badge.setGravity(Gravity.CENTER);
        return badge;
    }
    
    private android.graphics.drawable.Drawable createEditTextBackground() {
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        shape.setCornerRadius(8);
        shape.setColor(Color.parseColor("#1A1A1A"));
        shape.setStroke(2, Color.parseColor("#D4AF37"));
        return shape;
    }
    
    private BigDecimal calculateTotalBalance() {
        BigDecimal total = BigDecimal.ZERO;
        
        for (WalletData wallet : wallets.values()) {
            total = total.add(wallet.bnbBalance.multiply(new BigDecimal("300"))); // BNB ≈ $300
            total = total.add(wallet.ethBalance.multiply(new BigDecimal("2000"))); // ETH ≈ $2000
            total = total.add(wallet.usdtBalance); // USDT 1:1
        }
        
        return total;
    }
    
    private String generateRandomHex(int length) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(Integer.toHexString(rand.nextInt(16)));
        }
        return sb.toString();
    }
    
    private String generateMnemonic() {
        String[] words = {"alpha", "bravo", "charlie", "delta", "echo", "foxtrot", 
                         "golf", "hotel", "india", "juliett", "kilo", "lima"};
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(words[rand.nextInt(words.length)]);
            if (i < 11) sb.append(" ");
        }
        return sb.toString();
    }
    
    private void saveData() {
        SharedPreferences.Editor editor = prefs.edit();
        
        // Guardar estado de conexión
        editor.putBoolean("isBSCConnected", isBSCConnected);
        editor.putBoolean("isETHConnected", isETHConnected);
        
        // Guardar wallets (simplificado - en producción usarías encriptación)
        if (isBSCConnected && wallets.containsKey("BSC")) {
            editor.putString("BSC_address", wallets.get("BSC").address);
            editor.putString("BSC_balance", wallets.get("BSC").bnbBalance.toString());
        }
        
        if (isETHConnected && wallets.containsKey("ETH")) {
            editor.putString("ETH_address", wallets.get("ETH").address);
            editor.putString("ETH_balance", wallets.get("ETH").ethBalance.toString());
        }
        
        editor.apply();
    }
    
    private void loadSavedData() {
        isBSCConnected = prefs.getBoolean("isBSCConnected", false);
        isETHConnected = prefs.getBoolean("isETHConnected", false);
        
        if (isBSCConnected) {
            String address = prefs.getString("BSC_address", "0x" + generateRandomHex(40));
            String balanceStr = prefs.getString("BSC_balance", "0.5");
            WalletData wallet = new WalletData(address, "saved_private_key", "saved_mnemonic");
            wallet.bnbBalance = new BigDecimal(balanceStr);
            wallets.put("BSC", wallet);
        }
        
        if (isETHConnected) {
            String address = prefs.getString("ETH_address", "0x" + generateRandomHex(40));
            String balanceStr = prefs.getString("ETH_balance", "0.2");
            WalletData wallet = new WalletData(address, "saved_private_key", "saved_mnemonic");
            wallet.ethBalance = new BigDecimal(balanceStr);
            wallets.put("ETH", wallet);
        }
        
        // Generar algunas transacciones de ejemplo
        if (isBSCConnected || isETHConnected) {
            transactions.add(new Transaction("BSC", "received", new BigDecimal("0.5"), "BNB"));
            transactions.add(new Transaction("ETH", "sent", new BigDecimal("0.1"), "ETH"));
        }
    }
    
    private void showMnemonicImportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📝 Importar con Frase Mnemónica");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        
        TextView info = new TextView(this);
        info.setText("Ingresa tus 12 palabras separadas por espacios:");
        info.setTextColor(COLOR_TEXT_LIGHT);
        info.setPadding(0, 0, 0, 16);
        
        final EditText input = new EditText(this);
        input.setHint("palabra1 palabra2 ... palabra12");
        input.setTextColor(COLOR_TEXT_LIGHT);
        input.setHintTextColor(COLOR_TEXT_GRAY);
        input.setBackground(createEditTextBackground());
        input.setMinHeight(100);
        
        layout.addView(info);
        layout.addView(input);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Importar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mnemonic = input.getText().toString().trim();
                if (mnemonic.split(" ").length == 12) {
                    // Simular importación exitosa
                    String address = "0x" + generateRandomHex(40);
                    wallets.put("BSC", new WalletData(address, "imported_key", mnemonic));
                    wallets.get("BSC").bnbBalance = new BigDecimal("1.0");
                    isBSCConnected = true;
                    
                    saveData();
                    showDashboard();
                    Toast.makeText(MainActivity.this, "✅ Wallet BSC importada", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "❌ Deben ser 12 palabras", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    private void showPrivateKeyImportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🔑 Importar con Clave Privada");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        
        TextView info = new TextView(this);
        info.setText("Ingresa tu clave privada (0x...):");
        info.setTextColor(COLOR_TEXT_LIGHT);
        info.setPadding(0, 0, 0, 16);
        
        final EditText input = new EditText(this);
        input.setHint("0x...");
        input.setTextColor(COLOR_TEXT_LIGHT);
        input.setHintTextColor(COLOR_TEXT_GRAY);
        input.setBackground(createEditTextBackground());
        
        layout.addView(info);
        layout.addView(input);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Importar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String privateKey = input.getText().toString().trim();
                if (privateKey.startsWith("0x") && privateKey.length() == 66) {
                    // Simular importación exitosa
                    String address = "0x" + generateRandomHex(40);
                    wallets.put("ETH", new WalletData(address, privateKey, null));
                    wallets.get("ETH").ethBalance = new BigDecimal("0.5");
                    isETHConnected = true;
                    
                    saveData();
                    showDashboard();
                    Toast.makeText(MainActivity.this, "✅ Wallet ETH importada", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "❌ Clave privada inválida", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}