package com.tuapp.wallet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BackupDialog extends Dialog {
    
    private TextView mnemonicTextView;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button backupButton;
    private Button cancelButton;
    
    private String mnemonicPhrase;
    
    public BackupDialog(Context context, String mnemonic) {
        super(context);
        this.mnemonicPhrase = mnemonic;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_backup);
        
        setTitle("Backup Wallet");
        
        // Inicializar vistas
        mnemonicTextView = findViewById(R.id.mnemonic_text);
        passwordEditText = findViewById(R.id.password_edit);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit);
        backupButton = findViewById(R.id.backup_button);
        cancelButton = findViewById(R.id.cancel_button);
        
        // Mostrar frase mnemónica
        if (mnemonicPhrase != null && !mnemonicPhrase.isEmpty()) {
            mnemonicTextView.setText(mnemonicPhrase);
        } else {
            mnemonicTextView.setText("No mnemonic available");
        }
        
        // Botón Backup
        backupButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.length() < 6) {
                Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Aquí iría la lógica real de backup
            Toast.makeText(getContext(), "Backup completed successfully", Toast.LENGTH_SHORT).show();
            dismiss();
        });
        
        // Botón Cancel
        cancelButton.setOnClickListener(v -> {
            dismiss();
        });
    }
}