package com.axgg.walletmultired.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class SendDialog extends DialogFragment {
    
    public interface SendListener {
        void onSendConfirmed(String network, String token, String toAddress, BigDecimal amount);
    }
    
    private SendListener listener;
    private Context context;
    private String selectedNetwork = "BSC";
    private String selectedToken = "BNB";
    private BigDecimal maxAmount = BigDecimal.ZERO;
    
    // Views
    private Spinner spinnerNetwork;
    private Spinner spinnerToken;
    private EditText etAddress;
    private EditText etAmount;
    private TextView tvMaxAmount;
    private TextView tvFee;
    private TextView tvTotal;
    private Button btnSend;
    
    // Datos
    private List<String> networks = new ArrayList<>();
    private List<String> tokens = new ArrayList<>();
    
    public SendDialog(Context context) {
        this.context = context;
    }
    
    public void setSendListener(SendListener listener) {
        this.listener = listener;
    }
    
    public void setAvailableNetworks(List<String> networks) {
        this.networks = networks;
    }
    
    public void setTokenBalance(String network, String token, BigDecimal balance) {
        selectedNetwork = network;
        selectedToken = token;
        maxAmount = balance != null ? balance : BigDecimal.ZERO;
        
        if (tvMaxAmount != null) {
            tvMaxAmount.setText("Disponible: " + maxAmount.setScale(6, RoundingMode.HALF_UP).toString() + " " + token);
        }
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Crear layout principal
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);
        
        // Título
        TextView tvTitle = new TextView(context);
        tvTitle.setText("✈️ Enviar Fondos");
        tvTitle.setTextColor(Color.parseColor("#D4AF37"));
        tvTitle.setTextSize(20);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setPadding(0, 0, 0, 24);
        
        // Selector de red
        TextView tvNetworkLabel = new TextView(context);
        tvNetworkLabel.setText("Red:");
        tvNetworkLabel.setTextColor(Color.parseColor("#F5F5F5"));
        tvNetworkLabel.setPadding(0, 0, 0, 8);
        
        spinnerNetwork = new Spinner(context);
        if (networks.isEmpty()) {
            networks.add("BSC");
            networks.add("ETH");
        }
        ArrayAdapter<String> networkAdapter = new ArrayAdapter<>(context, 
            android.R.layout.simple_spinner_item, networks);
        networkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNetwork.setAdapter(networkAdapter);
        
        // Selector de token
        TextView tvTokenLabel = new TextView(context);
        tvTokenLabel.setText("Token:");
        tvTokenLabel.setTextColor(Color.parseColor("#F5F5F5"));
        tvTokenLabel.setPadding(0, 16, 0, 8);
        
        spinnerToken = new Spinner(context);
        tokens.add("BNB");
        tokens.add("USDT");
        ArrayAdapter<String> tokenAdapter = new ArrayAdapter<>(context,
            android.R.layout.simple_spinner_item, tokens);
        tokenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerToken.setAdapter(tokenAdapter);
        
        // Dirección destino
        TextView tvAddressLabel = new TextView(context);
        tvAddressLabel.setText("Dirección destino:");
        tvAddressLabel.setTextColor(Color.parseColor("#F5F5F5"));
        tvAddressLabel.setPadding(0, 16, 0, 8);
        
        etAddress = new EditText(context);
        etAddress.setHint("0x...");
        etAddress.setTextColor(Color.parseColor("#F5F5F5"));
        etAddress.setHintTextColor(Color.parseColor("#A0A0A0"));
        etAddress.setBackground(createEditTextBackground());
        
        // Cantidad
        TextView tvAmountLabel = new TextView(context);
        tvAmountLabel.setText("Cantidad:");
        tvAmountLabel.setTextColor(Color.parseColor("#F5F5F5"));
        tvAmountLabel.setPadding(0, 16, 0, 8);
        
        LinearLayout amountLayout = new LinearLayout(context);
        amountLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        etAmount = new EditText(context);
        etAmount.setHint("0.0");
        etAmount.setTextColor(Color.parseColor("#F5F5F5"));
        etAmount.setHintTextColor(Color.parseColor("#A0A0A0"));
        etAmount.setBackground(createEditTextBackground());
        etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | 
                             android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        
        Button btnMax = new Button(context);
        btnMax.setText("MAX");
        btnMax.setTextColor(Color.parseColor("#D4AF37"));
        btnMax.setBackgroundColor(Color.TRANSPARENT);
        btnMax.setOnClickListener(v -> {
            etAmount.setText(maxAmount.setScale(6, RoundingMode.HALF_UP).toString());
            calculateTotal();
        });
        
        LinearLayout.LayoutParams amountParams = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
        );
        etAmount.setLayoutParams(amountParams);
        
        LinearLayout.LayoutParams maxParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        maxParams.setMargins(8, 0, 0, 0);
        btnMax.setLayoutParams(maxParams);
        
        amountLayout.addView(etAmount);
        amountLayout.addView(btnMax);
        
        // Máximo disponible
        tvMaxAmount = new TextView(context);
        tvMaxAmount.setText("Disponible: 0.000000 " + selectedToken);
        tvMaxAmount.setTextColor(Color.parseColor("#A0A0A0"));
        tvMaxAmount.setTextSize(12);
        tvMaxAmount.setPadding(0, 8, 0, 16);
        
        // Información de tarifas
        LinearLayout feeLayout = new LinearLayout(context);
        feeLayout.setOrientation(LinearLayout.VERTICAL);
        feeLayout.setPadding(16, 16, 16, 16);
        feeLayout.setBackgroundColor(Color.parseColor("#1A1A1A"));
        feeLayout.setBackground(createCardBackground());
        
        TextView tvFeeLabel = new TextView(context);
        tvFeeLabel.setText("Resumen:");
        tvFeeLabel.setTextColor(Color.parseColor("#D4AF37"));
        tvFeeLabel.setTextSize(14);
        
        tvFee = new TextView(context);
        tvFee.setText("Tarifa de red: 0.0001 BNB");
        tvFee.setTextColor(Color.parseColor("#A0A0A0"));
        tvFee.setTextSize(12);
        
        tvTotal = new TextView(context);
        tvTotal.setText("Total a enviar: 0.000000 BNB");
        tvTotal.setTextColor(Color.parseColor("#F5F5F5"));
        tvTotal.setTextSize(14);
        tvTotal.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTotal.setPadding(0, 8, 0, 0);
        
        feeLayout.addView(tvFeeLabel);
        feeLayout.addView(tvFee);
        feeLayout.addView(tvTotal);
        
        // Botón enviar
        btnSend = new Button(context);
        btnSend.setText("ENVIAR");
        btnSend.setTextColor(Color.BLACK);
        btnSend.setBackgroundColor(Color.parseColor("#D4AF37"));
        btnSend.setPadding(16, 16, 16, 16);
        btnSend.setEnabled(false);
        btnSend.setOnClickListener(v -> {
            String address = etAddress.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            
            if (address.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!address.startsWith("0x") || address.length() != 42) {
                Toast.makeText(context, "Dirección inválida", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    Toast.makeText(context, "Cantidad inválida", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (amount.compareTo(maxAmount) > 0) {
                    Toast.makeText(context, "Saldo insuficiente", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (listener != null) {
                    String network = (String) spinnerNetwork.getSelectedItem();
                    String token = (String) spinnerToken.getSelectedItem();
                    listener.onSendConfirmed(network, token, address, amount);
                }
                
                dismiss();
                
            } catch (Exception e) {
                Toast.makeText(context, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Agregar elementos al layout principal
        mainLayout.addView(tvTitle);
        mainLayout.addView(tvNetworkLabel);
        mainLayout.addView(spinnerNetwork);
        mainLayout.addView(tvTokenLabel);
        mainLayout.addView(spinnerToken);
        mainLayout.addView(tvAddressLabel);
        mainLayout.addView(etAddress);
        mainLayout.addView(tvAmountLabel);
        mainLayout.addView(amountLayout);
        mainLayout.addView(tvMaxAmount);
        mainLayout.addView(feeLayout);
        mainLayout.addView(btnSend);
        
        // Configurar listeners
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                validateForm();
                calculateTotal();
            }
        });
        
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                validateForm();
                calculateTotal();
            }
        });
        
        spinnerNetwork.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedNetwork = (String) parent.getItemAtPosition(position);
                updateTokensForNetwork();
                calculateTotal();
            }
            
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        spinnerToken.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedToken = (String) parent.getItemAtPosition(position);
                tvMaxAmount.setText("Disponible: " + maxAmount.setScale(6, RoundingMode.HALF_UP).toString() + " " + selectedToken);
                calculateTotal();
            }
            
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        builder.setView(mainLayout);
        builder.setNegativeButton("Cancelar", (dialog, which) -> dismiss());
        
        return builder.create();
    }
    
    private void validateForm() {
        String address = etAddress.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        
        boolean isValid = !address.isEmpty() && !amountStr.isEmpty();
        
        if (isValid) {
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                isValid = amount.compareTo(BigDecimal.ZERO) > 0 && 
                         amount.compareTo(maxAmount) <= 0;
            } catch (Exception e) {
                isValid = false;
            }
        }
        
        btnSend.setEnabled(isValid);
    }
    
    private void calculateTotal() {
        try {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                tvFee.setText("Tarifa de red: 0.0001 " + (selectedNetwork.equals("BSC") ? "BNB" : "ETH"));
                tvTotal.setText("Total a enviar: 0.000000 " + selectedToken);
                return;
            }
            
            BigDecimal amount = new BigDecimal(amountStr);
            
            // Calcular tarifa (simulada)
            BigDecimal fee = new BigDecimal("0.0001");
            String feeToken = selectedNetwork.equals("BSC") ? "BNB" : "ETH";
            
            // Calcular total
            BigDecimal total = amount;
            if (selectedToken.equals(feeToken)) {
                total = total.add(fee);
            }
            
            tvFee.setText("Tarifa de red: " + fee.setScale(6, RoundingMode.HALF_UP).toString() + " " + feeToken);
            tvTotal.setText("Total a enviar: " + total.setScale(6, RoundingMode.HALF_UP).toString() + " " + selectedToken);
            
        } catch (Exception e) {
            tvFee.setText("Tarifa de red: 0.0001 " + (selectedNetwork.equals("BSC") ? "BNB" : "ETH"));
            tvTotal.setText("Total a enviar: 0.000000 " + selectedToken);
        }
    }
    
    private void updateTokensForNetwork() {
        tokens.clear();
        if (selectedNetwork.equals("BSC")) {
            tokens.add("BNB");
            tokens.add("USDT");
        } else {
            tokens.add("ETH");
            tokens.add("USDT");
            tokens.add("USDC");
        }
        
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerToken.getAdapter();
        adapter.notifyDataSetChanged();
        spinnerToken.setSelection(0);
    }
    
    private android.graphics.drawable.Drawable createEditTextBackground() {
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        shape.setCornerRadius(8);
        shape.setColor(Color.parseColor("#1A1A1A"));
        shape.setStroke(2, Color.parseColor("#D4AF37"));
        return shape;
    }
    
    private android.graphics.drawable.Drawable createCardBackground() {
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        shape.setCornerRadius(8);
        shape.setColor(Color.parseColor("#1A1A1A"));
        return shape;
    }
}