package com.example.mypets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Model.User;
import com.example.mypets.SQLite.UserDao;

public class RegisterActivity extends AppCompatActivity {
    private EditText edFullname, edUsername, edPassword, edCpassword, edEmail, edPhone;
    private Button registerBtn, backBtn;

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initview();
        userDao = new UserDao(this);

        backBtn.setOnClickListener(v -> onBackPressed());

        registerBtn.setOnClickListener(v -> {
            String username = edUsername.getText().toString();
            String password = edPassword.getText().toString();
            String confirmPassword = edCpassword.getText().toString();

            if (username.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Điền tài khoản", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Điền mật khẩu", Toast.LENGTH_SHORT).show();
            } else if (confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Vui lòng kiểm tra lại mật khẩu", Toast.LENGTH_SHORT).show();
            } else {
                User user = new User();
                user.setUsername(username);
                user.setHashedPassword(password);
                if (userDao.register(user) == -1) {
                    Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại trên hệ thống!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    public void initview() {
        edUsername = findViewById(R.id.username);
        edPassword = findViewById(R.id.password);
        edCpassword = findViewById(R.id.cpassword);
        registerBtn = findViewById(R.id.register);
        backBtn = findViewById(R.id.back);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDao.close();
    }
}