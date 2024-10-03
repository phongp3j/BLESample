package com.example.mypets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.SQLite.UserDao;

public class LoginActivity extends AppCompatActivity {
    private EditText edUsername, edPassword;
    private Button loginBtn, registerBtn;

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initview();

        userDao = new UserDao(this);

        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v -> {
            String username = edUsername.getText().toString();
            String password = edPassword.getText().toString();
            if (username.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập tài khoản", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            } else {
                int userId = userDao.login(username, password);
                if (userId > 0) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initview() {
        edUsername = findViewById(R.id.username);
        edPassword = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);
        registerBtn = findViewById(R.id.register);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDao.close();
    }
}