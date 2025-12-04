package com.example.restoranaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restoranaapp.database.dao.UserDao;
import com.example.restoranaapp.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvError;
    private TextView tvForgotPassword;
    private TextView tvSignUp;
    private ImageView ivTogglePassword;
    private UserDao userDao;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if already logged in
        // SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        // String savedRole = prefs.getString("role", null);
        // if (savedRole != null) {
        //    navigateBasedOnRole(savedRole);
        //    return;
        // }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        userDao = new UserDao(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // Password visibility toggle
        ivTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        // Forgot Password click (placeholder)
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Şifre sıfırlama özelliği yakında eklenecek", Toast.LENGTH_SHORT).show();
            }
        });

        // Sign Up click (placeholder)
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Kayıt olma özelliği yakında eklenecek", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            isPasswordVisible = false;
        } else {
            // Show password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            isPasswordVisible = true;
        }
        // Move cursor to end
        etPassword.setSelection(etPassword.getText().length());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // G3: Empty check
        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Kullanıcı adı ve şifre boş bırakılamaz.");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        // G2, G4: Database check
        User user = userDao.login(username, password);

        if (user != null) {
            // Login successful
            tvError.setVisibility(View.INVISIBLE);
            
            // Save session
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("userId", user.getId());
            editor.putString("username", user.getUsername());
            editor.putString("role", user.getRole());
            editor.apply();

            // G5: Role based navigation
            navigateBasedOnRole(user.getRole());
        } else {
            // G4: Login failed
            tvError.setText("Geçersiz kullanıcı adı veya şifre.");
            tvError.setVisibility(View.VISIBLE);
        }
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;
        if ("ADMIN".equals(role)) {
            intent = new Intent(LoginActivity.this, AdminMainActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, WaiterMainActivity.class);
        }
        startActivity(intent);
        finish(); // Close LoginActivity
    }
}

