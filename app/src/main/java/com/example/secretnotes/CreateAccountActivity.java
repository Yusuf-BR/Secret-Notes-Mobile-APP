package com.example.secretnotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountButton;
    ProgressBar progressBar;
    TextView LoginTextView;
    ImageView hide, show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        createAccountButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.progress_bar);
        LoginTextView = findViewById(R.id.login_here);
        hide = findViewById(R.id.hide_password);
        show = findViewById(R.id.show_password);

        hide.setOnClickListener(v -> {
            passwordEditText.setInputType(129);
            confirmPasswordEditText.setInputType(129);
            hide.setVisibility(View.GONE);
            show.setVisibility(View.VISIBLE);
        });
        show.setOnClickListener(v -> {
            passwordEditText.setInputType(1);
            confirmPasswordEditText.setInputType(1);
            show.setVisibility(View.GONE);
            hide.setVisibility(View.VISIBLE);
        });

        createAccountButton.setOnClickListener(v -> createAccount());
        LoginTextView.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }
    private void createAccount() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        boolean isValid = isEmailValid(email, password, confirmPassword);
        if(!isValid) {
            return;
        }
        createAccountInFirebase(email,password);
    }
    boolean isEmailValid(String email, String password, String confirmPassword) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid");
            emailEditText.requestFocus();
            return false;
        }
        if (password.length()<6) {
            passwordEditText.setError("Password length is invalid");
            passwordEditText.requestFocus();
            return false;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Confirm Password is required");
            confirmPasswordEditText.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password does not match");
            confirmPasswordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void createAccountInFirebase(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateAccountActivity.this, "Account created successfully, check email to verify", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification(); // Send verification email
                        FirebaseAuth.getInstance().signOut(); // Sign out the user
                        startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        if(task.getException() != null)
                            Toast.makeText(CreateAccountActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}