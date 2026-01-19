package com.axgg.walletmultired.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BalanceCard extends CardView {
    
    private Context context;
    private String network;
    private TextView tvBalance;
    private TextView tvUsdValue;
    private TextView tvToken;
    
    public BalanceCard(Context context, String network) {
        super(context);
        this.context = context;
        this.network = network;
        init();
    }
    
    private void init() {
        // Configurar card
        setCardElevation(8);
        setRadius(16);
        setUseCompatPadding(true);
        
        // Configurar layout
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        
        // Colores según red
        int backgroundColor, textColor;
        if (network.equals("BSC")) {
            backgroundColor = Color.parseColor("#F3BA2F");
            textColor = Color.BLACK;
        } else {
            backgroundColor = Color.parseColor("#6978FF");
            textColor = Color.BLACK;
        }
        
        setCardBackgroundColor(backgroundColor);
        
        // Network indicator
        TextView tvNetwork = new TextView(context);
        tvNetwork.setText(network);
        tvNetwork.setTextColor(textColor);
        tvNetwork.setTextSize(10);
        tvNetwork.setPadding(8, 4, 8, 4);
        tvNetwork.setBackgroundColor(Color.argb(64, 0, 0, 0));
        tvNetwork.setTypeface(null, android.graphics.Typeface.BOLD);
        
        LinearLayout.LayoutParams networkParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        networkParams.gravity = android.view.Gravity.END;
        tvNetwork.setLayoutParams(networkParams);
        
        // Balance info
        LinearLayout balanceLayout = new LinearLayout(context);
        balanceLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        
        TextView tvLabel = new TextView(context);
        tvLabel.setText("Saldo " + network);
        tvLabel.setTextColor(Color.argb(128, 0, 0, 0));
        tvLabel.setTextSize(12);
        
        tvBalance = new TextView(context);
        tvBalance.setText("0.000000");
        tvBalance.setTextColor(textColor);
        tvBalance.setTextSize(16);
        tvBalance.setTypeface(null, android.graphics.Typeface.BOLD);
        
        tvUsdValue = new TextView(context);
        tvUsdValue.setText("$0.00");
        tvUsdValue.setTextColor(Color.argb(128, 0, 0, 0));
        tvUsdValue.setTextSize(12);
        
        tvToken = new TextView(context);
        tvToken.setText(network.equals("BSC") ? "BNB" : "ETH");
        tvToken.setTextColor(Color.argb(128, 0, 0, 0));
        tvToken.setTextSize(12);
        
        textLayout.addView(tvLabel);
        textLayout.addView(tvBalance);
        textLayout.addView(tvUsdValue);
        textLayout.addView(tvToken);
        
        TextView tvIcon = new TextView(context);
        tvIcon.setText(network.equals("BSC") ? "B" : "Ξ");
        tvIcon.setTextSize(24);
        tvIcon.setTextColor(Color.argb(128, 0, 0, 0));
        tvIcon.setPadding(16, 0, 0, 0);
        
        balanceLayout.addView(textLayout);
        balanceLayout.addView(tvIcon);
        
        layout.addView(tvNetwork);
        layout.addView(balanceLayout);
        
        addView(layout);
    }
    
    public void setBalance(BigDecimal balance, BigDecimal usdValue) {
        if (balance == null) {
            tvBalance.setText("0.000000");
            tvUsdValue.setText("$0.00");
            return;
        }
        
        // Formatear balance
        String formattedBalance;
        if (network.equals("BSC")) {
            formattedBalance = balance.setScale(6, RoundingMode.HALF_UP).toString();
            tvToken.setText("BNB");
        } else {
            formattedBalance = balance.setScale(6, RoundingMode.HALF_UP).toString();
            tvToken.setText("ETH");
        }
        
        tvBalance.setText(formattedBalance);
        
        // Formatear valor USD
        if (usdValue != null) {
            tvUsdValue.setText("$" + usdValue.setScale(2, RoundingMode.HALF_UP).toString());
        }
    }
    
    public void setConnected(boolean isConnected) {
        if (!isConnected) {
            tvBalance.setText("No conectada");
            tvUsdValue.setText("");
            tvToken.setText("");
        }
    }
    
    public void setUsdtBalance(BigDecimal usdtBalance) {
        if (usdtBalance != null && usdtBalance.compareTo(BigDecimal.ZERO) > 0) {
            String text = tvUsdValue.getText().toString();
            if (!text.isEmpty()) {
                tvUsdValue.setText(text + " + " + usdtBalance.setScale(2, RoundingMode.HALF_UP) + " USDT");
            }
        }
    }
}