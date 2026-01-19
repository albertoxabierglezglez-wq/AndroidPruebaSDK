package com.tuapp.wallet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiveDialog extends Dialog {
    
    private TextView addressTextView;
    private Button copyButton;
    private Button shareButton;
    private String walletAddress;
    
    public ReceiveDialog(Context context, String address) {
        super(context);
        this.walletAddress = address;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_receive);
        
        // Configurar título
        setTitle("Receive Funds");
        
        // Inicializar vistas
        addressTextView = findViewById(R.id.address_text);
        copyButton = findViewById(R.id.copy_button);
        shareButton = findViewById(R.id.share_button);
        
        // Mostrar dirección
        if (walletAddress != null && !walletAddress.isEmpty()) {
            addressTextView.setText(walletAddress);
        } else {
            addressTextView.setText("No address available");
        }
        
        // Botón Copy
        copyButton.setOnClickListener(v -> {
            if (walletAddress != null && !walletAddress.isEmpty()) {
                android.content.ClipboardManager clipboard = 
                    (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Wallet Address", walletAddress);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Address copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Botón Share
        shareButton.setOnClickListener(v -> {
            if (walletAddress != null && !walletAddress.isEmpty()) {
                android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
                    "My wallet address: " + walletAddress);
                getContext().startActivity(android.content.Intent.createChooser(shareIntent, "Share address"));
            }
        });
    }
}