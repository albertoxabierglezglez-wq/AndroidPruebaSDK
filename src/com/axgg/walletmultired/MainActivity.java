package com.axgg.walletmultired;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends Activity {
    
    // Colores
    private static final int COLOR_BG_DARK = 0xFF121212;
    private static final int COLOR_BG_CARD = 0xFF1E1E2D;
    private static final int COLOR_TEXT_LIGHT = 0xFFCCCCCC;
    private static final int COLOR_PRIMARY = 0xFF4CAF50;
    private static final int COLOR_ACCENT = 0xFFFF4081;
    
    // Componentes UI
    private LinearLayout mainLayout;
    private TextView statusText;
    private TextView balanceText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configurar layout principal
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(COLOR_BG_DARK);
        mainLayout.setPadding(50, 50, 50, 50);
        
        // Crear ScrollView para contenido desplazable
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(mainLayout);
        
        setContentView(scrollView);
        
        // Agregar componentes
        addHeader();
        addWalletSetupSection();
        addStatusSection();
        addBalanceSection();
        addActionButtons();
        addDisconnectButton();
    }
    
    private void addHeader() {
        TextView title = new TextView(this);
        title.setText("Wallet App");
        title.setTextSize(24);
        title.setTextColor(0xFFFFFFFF);
        title.setPadding(0, 0, 0, 40);
        
        mainLayout.addView(title);
    }
    
    private void addWalletSetupSection() {
        // Contenedor
        LinearLayout container = createCardContainer();
        
        // Descripcion
        TextView desc = new TextView(this);
        desc.setText("Multi-chain wallet for BSC and Ethereum");
        desc.setTextColor(COLOR_TEXT_LIGHT);
        desc.setPadding(0, 0, 0, 20);
        container.addView(desc);
        
        // Redes
        TextView networksLabel = new TextView(this);
        networksLabel.setText("Available Networks:");
        networksLabel.setTextColor(0xFFFFFFFF);
        networksLabel.setPadding(0, 0, 0, 10);
        container.addView(networksLabel);
        
        // BSC
        LinearLayout bscRow = new LinearLayout(this);
        bscRow.setOrientation(LinearLayout.HORIZONTAL);
        bscRow.setPadding(0, 0, 0, 5);
        
        TextView bscDot = new TextView(this);
        bscDot.setText("[BSC]");
        bscDot.setTextColor(0xFFF0B90B);
        
        TextView bscText = new TextView(this);
        bscText.setText(" BSC Network");
        bscText.setTextColor(COLOR_TEXT_LIGHT);
        
        bscRow.addView(bscDot);
        bscRow.addView(bscText);
        container.addView(bscRow);
        
        // Ethereum
        LinearLayout ethRow = new LinearLayout(this);
        ethRow.setOrientation(LinearLayout.HORIZONTAL);
        ethRow.setPadding(0, 0, 0, 20);
        
        TextView ethDot = new TextView(this);
        ethDot.setText("[ETH]");
        ethDot.setTextColor(0xFF627EEA);
        
        TextView ethText = new TextView(this);
        ethText.setText(" Ethereum Network");
        ethText.setTextColor(COLOR_TEXT_LIGHT);
        
        ethRow.addView(ethDot);
        ethRow.addView(ethText);
        container.addView(ethRow);
        
        // Botones
        Button createBtn = new Button(this);
        createBtn.setText("Create Wallets");
        createBtn.setBackgroundColor(0xFFFFD700);
        createBtn.setTextColor(0xFF000000);
        createBtn.setPadding(20, 20, 20, 20);
        createBtn.setOnClickListener(v -> showCreateWalletsDialog());
        
        Button importBtn = new Button(this);
        importBtn.setText("Import Wallets");
        importBtn.setBackgroundColor(0xFF333333);
        importBtn.setTextColor(0xFFFFFFFF);
        importBtn.setPadding(20, 20, 20, 20);
        importBtn.setOnClickListener(v -> showImportDialog());
        
        container.addView(createBtn);
        container.addView(importBtn);
        
        mainLayout.addView(container);
    }
    
    private void addStatusSection() {
        LinearLayout container = createCardContainer();
        
        TextView label = new TextView(this);
        label.setText("Status:");
        label.setTextColor(0xFFFFFFFF);
        label.setPadding(0, 0, 0, 10);
        
        statusText = new TextView(this);
        statusText.setText("Disconnected");
        statusText.setTextColor(0xFFFF4444);
        
        container.addView(label);
        container.addView(statusText);
        
        mainLayout.addView(container);
    }
    
    private void addBalanceSection() {
        LinearLayout container = createCardContainer();
        
        TextView label = new TextView(this);
        label.setText("Total Balance:");
        label.setTextColor(0xFFFFFFFF);
        label.setPadding(0, 0, 0, 10);
        
        balanceText = new TextView(this);
        balanceText.setText("$0.00");
        balanceText.setTextSize(28);
        balanceText.setTextColor(COLOR_PRIMARY);
        
        container.addView(label);
        container.addView(balanceText);
        
        mainLayout.addView(container);
    }
    
    private void addActionButtons() {
        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setPadding(0, 20, 0, 0);
        
        Button sendBtn = new Button(this);
        sendBtn.setText("Send");
        sendBtn.setBackgroundColor(0xFF2196F3);
        sendBtn.setTextColor(0xFFFFFFFF);
        sendBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        sendBtn.setOnClickListener(v -> showSendDialog());
        
        Button receiveBtn = new Button(this);
        receiveBtn.setText("Receive");
        receiveBtn.setBackgroundColor(0xFF4CAF50);
        receiveBtn.setTextColor(0xFFFFFFFF);
        receiveBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        receiveBtn.setPadding(10, 0, 10, 0);
        receiveBtn.setOnClickListener(v -> showReceiveDialog());
        
        Button backupBtn = new Button(this);
        backupBtn.setText("Backup");
        backupBtn.setBackgroundColor(0xFFFF9800);
        backupBtn.setTextColor(0xFFFFFFFF);
        backupBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        backupBtn.setOnClickListener(v -> showBackupDialog());
        
        buttonRow.addView(sendBtn);
        buttonRow.addView(receiveBtn);
        buttonRow.addView(backupBtn);
        
        mainLayout.addView(buttonRow);
    }
    
    private void addDisconnectButton() {
        Button disconnectBtn = new Button(this);
        disconnectBtn.setText("Disconnect");
        disconnectBtn.setBackgroundColor(0xFFFF4444);
        disconnectBtn.setTextColor(0xFFFFFFFF);
        disconnectBtn.setPadding(20, 20, 20, 20);
        disconnectBtn.setOnClickListener(v -> finish());
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = 30;
        disconnectBtn.setLayoutParams(params);
        
        mainLayout.addView(disconnectBtn);
    }
    
    private LinearLayout createCardContainer() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackgroundColor(COLOR_BG_CARD);
        container.setPadding(30, 30, 30, 30);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = 20;
        container.setLayoutParams(params);
        
        return container;
    }
    
    // Diálogos básicos
    private void showCreateWalletsDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Create Wallets")
            .setMessage("This will create new wallets for BSC and Ethereum networks.")
            .setPositiveButton("Create", (dialog, which) -> {
                statusText.setText("Connected");
                statusText.setTextColor(COLOR_PRIMARY);
                balanceText.setText("$125.50");
                Toast.makeText(this, "Wallets created successfully", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void showImportDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Import Wallet")
            .setMessage("Select import method:")
            .setPositiveButton("Mnemonic", (dialog, which) -> showMnemonicImportDialog())
            .setNegativeButton("Private Key", (dialog, which) -> showPrivateKeyImportDialog())
            .setNeutralButton("Cancel", null)
            .show();
    }
    
    private void showMnemonicImportDialog() {
        final EditText input = new EditText(this);
        input.setHint("Enter 12-word mnemonic phrase");
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        
        new AlertDialog.Builder(this)
            .setTitle("Import with Mnemonic")
            .setView(input)
            .setPositiveButton("Import", (dialog, which) -> {
                String mnemonic = input.getText().toString().trim();
                if (mnemonic.split(" ").length == 12) {
                    statusText.setText("Connected");
                    statusText.setTextColor(COLOR_PRIMARY);
                    balanceText.setText("$0.00");
                    Toast.makeText(this, "Wallet imported successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Must be 12 words", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void showPrivateKeyImportDialog() {
        final EditText input = new EditText(this);
        input.setHint("Enter private key (0x...)");
        
        new AlertDialog.Builder(this)
            .setTitle("Import with Private Key")
            .setView(input)
            .setPositiveButton("Import", (dialog, which) -> {
                String privateKey = input.getText().toString().trim();
                if (privateKey.startsWith("0x") && privateKey.length() > 10) {
                    statusText.setText("Connected");
                    statusText.setTextColor(COLOR_PRIMARY);
                    Toast.makeText(this, "Wallet imported successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Invalid private key", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void showSendDialog() {
        final EditText amountInput = new EditText(this);
        amountInput.setHint("Amount");
        amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        
        final EditText addressInput = new EditText(this);
        addressInput.setHint("Recipient address (0x...)");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(amountInput);
        layout.addView(addressInput);
        
        new AlertDialog.Builder(this)
            .setTitle("Send Funds")
            .setView(layout)
            .setPositiveButton("Send", (dialog, which) -> {
                String amount = amountInput.getText().toString();
                String address = addressInput.getText().toString();
                if (!amount.isEmpty() && address.startsWith("0x")) {
                    Toast.makeText(this, "Sent " + amount + " to " + address.substring(0, 10) + "...", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void showReceiveDialog() {
        TextView addressText = new TextView(this);
        addressText.setText("0x742d35Cc6634C0532925a3b844Bc9e...");
        addressText.setTextColor(0xFFFFFFFF);
        addressText.setPadding(20, 20, 20, 20);
        addressText.setBackgroundColor(0xFF333333);
        
        new AlertDialog.Builder(this)
            .setTitle("Receive Funds")
            .setMessage("Your wallet address:")
            .setView(addressText)
            .setPositiveButton("Copy", (dialog, which) -> {
                Toast.makeText(this, "Address copied to clipboard", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Close", null)
            .show();
    }
    
    private void showBackupDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Backup Wallet")
            .setMessage("Backup your wallet to secure your funds.")
            .setPositiveButton("Backup Now", (dialog, which) -> {
                Toast.makeText(this, "Backup process started", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Later", null)
            .show();
    }
}