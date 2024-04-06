package com.example.secretnotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class setting_lock extends AppCompatActivity {
    EditText pin, confirmPin;
    Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_lock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pin = findViewById(R.id.pin);
        confirmPin = findViewById(R.id.confirm_pin);
        confirm = findViewById(R.id.confirm_button);
        confirm.setOnClickListener(v -> confirm());

    }
    private void confirm() {
        String pin = this.pin.getText().toString();
        String confirmPin = this.confirmPin.getText().toString();
        boolean isValid = isPinValid(pin, confirmPin);
        if(!isValid) {
            return;
        }
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("registered", true);
        editor.putString("pin", pin);
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    boolean isPinValid(String pin, String confirmPin) {
        if (pin.isEmpty()) {
            this.pin.setError("Pin is required");
            this.pin.requestFocus();
            return false;
        }
        if (confirmPin.isEmpty()) {
            this.confirmPin.setError("Confirm Pin is required");
            this.confirmPin.requestFocus();
            return false;
        }
        if (!pin.equals(confirmPin)) {
            this.confirmPin.setError("Pin does not match");
            this.confirmPin.requestFocus();
            return false;
        }
        return true;
    }
}