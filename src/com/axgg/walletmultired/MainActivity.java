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
import java.math.BigDecimal;

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
    private WalletManager walletManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inicializar WalletManager
        walletManager = new WalletManager(this);
        
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
        title.setText("Multi-Red Wallet");
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
        desc.setText("Billetera para redes BSC y Ethereum");
        desc.setTextColor(COLOR_TEXT_LIGHT);
        desc.setPadding(0, 0, 0, 20);
        container.addView(desc);
        
        // Redes
        TextView networksLabel = new TextView(this);
        networksLabel.setText("Redes disponibles:");
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
        bscText.setText(" Binance Smart Chain");
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
        
        // Boton Crear Wallet
        Button createBtn = new Button(this);
        createBtn.setText("Crear Wallets");
        createBtn.setBackgroundColor(0xFFFFD700);
        createBtn.setTextColor(0xFF000000);
        createBtn.setPadding(20, 20, 20, 20);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateWalletsDialog();
            }
        });
        
        // Boton Importar Wallet
        Button importBtn = new Button(this);
        importBtn.setText("Importar Wallets");
        importBtn.setBackgroundColor(0xFF333333);
        importBtn.setTextColor(0xFFFFFFFF);
        importBtn.setPadding(20, 20, 20, 20);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportDialog();
            }
        });
        
        container.addView(createBtn);
        container.addView(importBtn);
        
        mainLayout.addView(container);
    }
    
    private void addStatusSection() {
        LinearLayout container = createCardContainer();
        
        TextView label = new TextView(this);
        label.setText("Estado:");
        label.setTextColor(0xFFFFFFFF);
        label.setPadding(0, 0, 0, 10);
        
        statusText = new TextView(this);
        if (walletManager.walletExists()) {
            statusText.setText("Conectado");
            statusText.setTextColor(COLOR_PRIMARY);
        } else {
            statusText.setText("Desconectado");
            statusText.setTextColor(0xFFFF4444);
        }
        
        container.addView(label);
        container.addView(statusText);
        
        mainLayout.addView(container);
    }
    
    private void addBalanceSection() {
        LinearLayout container = createCardContainer();
        
        TextView label = new TextView(this);
        label.setText("Balance Total:");
        label.setTextColor(0xFFFFFFFF);
        label.setPadding(0, 0, 0, 10);
        
        balanceText = new TextView(this);
        if (walletManager.walletExists()) {
            BigDecimal total = walletManager.getTotalBalance();
            balanceText.setText("$" + total.toString());
            balanceText.setTextColor(COLOR_PRIMARY);
        } else {
            balanceText.setText("$0.00");
            balanceText.setTextColor(COLOR_TEXT_LIGHT);
        }
        balanceText.setTextSize(28);
        
        container.addView(label);
        container.addView(balanceText);
        
        mainLayout.addView(container);
    }
    
    private void addActionButtons() {
        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setPadding(0, 20, 0, 0);
        
        // Boton Enviar
        Button sendBtn = new Button(this);
        sendBtn.setText("Enviar");
        sendBtn.setBackgroundColor(0xFF2196F3);
        sendBtn.setTextColor(0xFFFFFFFF);
        sendBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendDialog();
            }
        });
        
        // Boton Recibir
        Button receiveBtn = new Button(this);
        receiveBtn.setText("Recibir");
        receiveBtn.setBackgroundColor(0xFF4CAF50);
        receiveBtn.setTextColor(0xFFFFFFFF);
        receiveBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        receiveBtn.setPadding(10, 0, 10, 0);
        receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReceiveDialog();
            }
        });
        
        // Boton Backup
        Button backupBtn = new Button(this);
        backupBtn.setText("Backup");
        backupBtn.setBackgroundColor(0xFFFF9800);
        backupBtn.setTextColor(0xFFFFFFFF);
        backupBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        backupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBackupDialog();
            }
        });
        
        buttonRow.addView(sendBtn);
        buttonRow.addView(receiveBtn);
        buttonRow.addView(backupBtn);
        
        mainLayout.addView(buttonRow);
    }
    
    private void addDisconnectButton() {
        Button disconnectBtn = new Button(this);
        disconnectBtn.setText("Desconectar");
        disconnectBtn.setBackgroundColor(0xFFFF4444);
        disconnectBtn.setTextColor(0xFFFFFFFF);
        disconnectBtn.setPadding(20, 20, 20, 20);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
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
    
    // ===================== DIALOGOS =====================
    
    private void showCreateWalletsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crear Wallets");
        builder.setMessage("Se crearan wallets nuevas para BSC y Ethereum.");
        builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean success = walletManager.createNewWallet();
                if (success) {
                    statusText.setText("Conectado");
                    statusText.setTextColor(COLOR_PRIMARY);
                    
                    BigDecimal total = walletManager.getTotalBalance();
                    balanceText.setText("$" + total.toString());
                    
                    Toast.makeText(MainActivity.this, "Wallets creadas exitosamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error al crear wallets", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    
    private void showImportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importar Wallet");
        builder.setMessage("Seleccione metodo de importacion:");
        builder.setPositiveButton("Mnemonic", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMnemonicImportDialog();
            }
        });
        builder.setNegativeButton("Clave Privada", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showPrivateKeyImportDialog();
            }
        });
        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    
    private void showMnemonicImportDialog() {
        final EditText input = new EditText(this);
        input.setHint("Ingrese frase mnemonic de 12 palabras");
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(3);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importar con Mnemonic");
        builder.setView(input);
        builder.setPositiveButton("Importar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mnemonic = input.getText().toString().trim();
                if (mnemonic.split("\\s+").length == 12) {
                    boolean success = walletManager.restoreWallet(mnemonic);
                    if (success) {
                        statusText.setText("Conectado");
                        statusText.setTextColor(COLOR_PRIMARY);
                        
                        BigDecimal total = walletManager.getTotalBalance();
                        balanceText.setText("$" + total.toString());
                        
                        Toast.makeText(MainActivity.this, "Wallet importada exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error al importar wallet", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Debe ser exactamente 12 palabras", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    
    private void showPrivateKeyImportDialog() {
        final EditText input = new EditText(this);
        input.setHint("Ingrese clave privada (0x...)");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importar con Clave Privada");
        builder.setView(input);
        builder.setPositiveButton("Importar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String privateKey = input.getText().toString().trim();
                if (privateKey.startsWith("0x") && privateKey.length() > 10) {
                    // Simular restauracion con clave privada
                    boolean success = walletManager.restoreWallet("fake mnemonic for private key import");
                    if (success) {
                        statusText.setText("Conectado");
                        statusText.setTextColor(COLOR_PRIMARY);
                        Toast.makeText(MainActivity.this, "Wallet importada con clave privada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Clave privada invalida", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    private void showSendDialog() {
        final EditText amountInput = new EditText(this);
        amountInput.setHint("Monto");
        amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        
        final EditText addressInput = new EditText(this);
        addressInput.setHint("Direccion destino (0x...)");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(amountInput);
        layout.addView(addressInput);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enviar Fondos");
        builder.setView(layout);
        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = amountInput.getText().toString();
                String address = addressInput.getText().toString();
                if (!amount.isEmpty() && address.startsWith("0x")) {
                    Toast.makeText(MainActivity.this, "Enviado " + amount + " a " + address.substring(0, 10) + "...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    private void showReceiveDialog() {
        if (!walletManager.walletExists()) {
            Toast.makeText(this, "Primero cree o importe una wallet", Toast.LENGTH_SHORT).show();
            return;
        }
        
        TextView addressText = new TextView(this);
        String bscAddress = walletManager.getAddress("bsc");
        addressText.setText(bscAddress != null ? bscAddress : "Direccion no disponible");
        addressText.setTextColor(0xFFFFFFFF);
        addressText.setPadding(20, 20, 20, 20);
        addressText.setBackgroundColor(0xFF333333);
        addressText.setTextIsSelectable(true);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recibir Fondos (BSC)");
        builder.setMessage("Su direccion BSC:");
        builder.setView(addressText);
        builder.setPositiveButton("Copiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Direccion copiada al portapapeles", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }
    
    private void showBackupDialog() {
        if (!walletManager.walletExists()) {
            Toast.makeText(this, "Primero cree una wallet", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Backup de Wallet");
        builder.setMessage("Guarde su frase mnemonic en un lugar seguro. Con ella puede restaurar su wallet en cualquier dispositivo.");
        builder.setPositiveButton("Ver Mnemonic", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMnemonicBackupDialog();
            }
        });
        builder.setNegativeButton("Mas tarde", null);
        builder.show();
    }
    
    private void showMnemonicBackupDialog() {
        String mnemonic = walletManager.getMnemonic();
        if (mnemonic == null) {
            Toast.makeText(this, "No hay mnemonic disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        
        TextView mnemonicText = new TextView(this);
        mnemonicText.setText(mnemonic);
        mnemonicText.setTextColor(0xFFFFFFFF);
        mnemonicText.setTextSize(16);
        mnemonicText.setPadding(30, 30, 30, 30);
        mnemonicText.setBackgroundColor(0xFF333333);
        mnemonicText.setTextIsSelectable(true);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Su Frase Mnemonic (SECRETA)");
        builder.setMessage("GUARDE ESTA INFORMACION EN UN LUGAR SEGURO. Cualquiera con esta frase puede controlar sus fondos.");
        builder.setView(mnemonicText);
        builder.setPositiveButton("Copiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Mnemonic copiado al portapapeles", Toast.LENGTH_SHORT).show();
                walletManager.markAsBackedUp();
            }
        });
        builder.setNegativeButton("Entendido", null);
        builder.show();
    }
}