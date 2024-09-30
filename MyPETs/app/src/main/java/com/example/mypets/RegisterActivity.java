package com.example.mypets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Model.User;
import com.example.mypets.SQLite.SQLiteHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText edFullname, edUsername, edPassword, edCpassword, edEmail, edPhone;
    private Button registerBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initview();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = edFullname.getText().toString();
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();
                String cpassword = edCpassword.getText().toString();
                String email = edEmail.getText().toString();
                String phone = edPhone.getText().toString();
                if (fullname.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Điền tên", Toast.LENGTH_SHORT).show();
                } else if (username.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Điền tài khoản", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Điền mật khẩu", Toast.LENGTH_SHORT).show();
                } else if (cpassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(cpassword)) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng kiểm tra lại mật khẩu", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
                } else if (phone.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
                } else {
                    SQLiteHelper db = new SQLiteHelper(RegisterActivity.this);
                    User user = new User(username, password, fullname, email, phone);
                    if (db.register(user) == -1) {
                        Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại trên hệ thống!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    public void initview() {
        edFullname = findViewById(R.id.fullname);
        edUsername = findViewById(R.id.username);
        edPassword = findViewById(R.id.password);
        edCpassword = findViewById(R.id.cpassword);
        registerBtn = findViewById(R.id.register);
        backBtn = findViewById(R.id.back);
        edEmail = findViewById(R.id.email);
        edPhone = findViewById(R.id.phone);
    }
}