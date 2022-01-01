package com.sustart.shdsystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.sustart.shdsystem.common.Constant;
import com.sustart.shdsystem.entity.User;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity.class";

    private Button registerButton;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout addressTextInputLayout;

    private User user;


    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
//            响应成功
            if (response.isSuccessful()) {
//                获取响应数据体
                String body = response.body().string();
                Log.e(TAG, "注册服务器返回数据：" + body);
            }
        }

        @Override
        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
            Log.e(TAG, "连接服务器失败! ");
            e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phoneTextInputLayout = findViewById(R.id.register_user_phone_layout);
        nameTextInputLayout = findViewById(R.id.register_user_name_layout);
        passwordTextInputLayout = findViewById(R.id.register_user_password_layout);
        addressTextInputLayout = findViewById(R.id.register_user_address_layout);
        registerButton = findViewById(R.id.register_submit_btn);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phoneEditText = phoneTextInputLayout.getEditText();
                String phone = phoneEditText.getText().toString();
                EditText nameEditText = nameTextInputLayout.getEditText();
                String name = nameEditText.getText().toString();
                EditText passwordEditText = passwordTextInputLayout.getEditText();
                String password = passwordEditText.getText().toString();
                EditText addressEditText = addressTextInputLayout.getEditText();
                String address = addressEditText.getText().toString();

//              todo  参数合法性校验。内容不为空且合法才执行注册方法

                user = new User(phone, name, password, address);
                registerUser();
            }
        });
    }

    private void registerUser() {
        String requestUrl = Constant.HOST_URL + "user";
        RequestBody requestBody = new FormBody.Builder()
                .add("phone", user.getPhone())
                .add("name", user.getName())
                .add("password", user.getPassword())
                .add("address", user.getAddress())
                .build();
        Request request = new Request.Builder().url(requestUrl).post(requestBody).build();
        OkHttpClient client = new OkHttpClient();
        try {
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
            return;
        }
//        设定一定注册成功
        Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}