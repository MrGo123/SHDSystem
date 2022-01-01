package com.sustart.shdsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sustart.shdsystem.common.BaseResponse;
import com.sustart.shdsystem.common.Constant;
import com.sustart.shdsystem.entity.User;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity.class";

    private Button registerButton;
    private Button loginButton;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private CheckBox cbRememberPwd;

    private User legalUser;
    private SHDSystemApplication application;
    private boolean requestStatus = false;

    private EditText phoneEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneTextInputLayout = findViewById(R.id.login_user_phone);
        passwordTextInputLayout = findViewById(R.id.login_user_password);
        registerButton = findViewById(R.id.register_btn);
        loginButton = findViewById(R.id.login_btn);
        cbRememberPwd = findViewById(R.id.login_remember_pwd);

        phoneEditText = phoneTextInputLayout.getEditText();
        passwordEditText = passwordTextInputLayout.getEditText();
        rememberMe();

        application = (SHDSystemApplication) getApplication();

        registerBind();
        loginBind();
    }

    /**
     * 如果sp文件中保存有该用户的信息，则直接获取并填入输入框中。
     */
    private void rememberMe() {
        String spFileName = getResources().getString(R.string.shared_preference_file_name);
        String userPhoneKey = getResources().getString(R.string.login_user_phone);
        String userPwdKey = getResources().getString(R.string.login_user_password);
        String rememberPasswordKey = getResources().getString(R.string.login_remember_password);
// 如果文件中保存有，则获取对应的数据并绑定
        SharedPreferences spFile = getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        String recordPhone = spFile.getString(userPhoneKey, null);
        String recordPwd = spFile.getString(userPwdKey, null);
        Boolean rememberPassword = spFile.getBoolean(rememberPasswordKey, false);
// 有就设置，没有不设置
        if (recordPhone != null && !TextUtils.isEmpty(recordPhone)) {
            phoneEditText.setText(recordPhone);
        }

        if (recordPwd != null && !TextUtils.isEmpty(recordPwd)) {
            passwordEditText.setText(recordPwd);
        }
        cbRememberPwd.setChecked(rememberPassword);
    }

    /**
     * 登录按钮及其相关逻辑、网络请求
     */
    private void loginBind() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                获取输入框的内容
                String tempPhoneNumber = phoneEditText.getText().toString();
                String tempPassword = passwordEditText.getText().toString();
                // 输入合法性判断，暂时只判断是否填写
                if (tempPhoneNumber.length() == 0) {
//                    显示错误提示
                    Toast.makeText(LoginActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tempPassword.length() == 0) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isLegalUser(tempPhoneNumber, tempPassword)) {
//                    记住密码
                    String spFileName = getResources().getString(R.string.shared_preference_file_name);
                    String phoneKey = getResources().getString(R.string.login_user_phone);
                    String passwordKey = getResources().getString(R.string.login_user_password);
                    String rememberPasswordKey = getResources().getString(R.string.login_remember_password);

                    SharedPreferences spFile = getSharedPreferences(spFileName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = spFile.edit();

                    if (cbRememberPwd.isChecked()) {
                        editor.putString(phoneKey, application.loginUser.getPhone());
                        editor.putString(passwordKey, application.loginUser.getPassword());
                        editor.putBoolean(rememberPasswordKey, true);
                        editor.apply();
                    } else {
                        editor.remove(phoneKey);
                        editor.remove(passwordKey);
                        editor.remove(rememberPasswordKey);
                        editor.apply();
                    }

                    Toast.makeText(LoginActivity.this, "欢迎，" + legalUser.getName(), Toast.LENGTH_SHORT).show();
//                 进入主页
                    Intent intentToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intentToMainActivity);
                }
            }
        });
    }

    /**
     * 注册按钮事件绑定
     */
    private void registerBind() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // todo 这里okhttp为异步执行：存在线程冲突的问题，因为使用了共享变量 legalUser 和 requestStatus
    private okhttp3.Callback callback = new okhttp3.Callback() {

        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
//            响应成功
            if (response.isSuccessful()) {
//                获取响应数据体
                String body = response.body().string();
                Log.e(TAG, "登录服务器返回数据：" + body);

                if (body != null) {
                    Gson gson = new Gson();
                    Type jsonType = new TypeToken<BaseResponse<User>>() {
                    }.getType();
                    BaseResponse<User> userResponse = gson.fromJson(body, jsonType);
                    User user = userResponse.getData();
                    if (user != null) {
                        legalUser = user;
                    }
                }
                requestStatus = true;
            }
        }

        @Override
        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
            Log.e(TAG, "连接服务器失败! ");
            e.printStackTrace();
            requestStatus = true;
        }
    };

    /**
     * 通过后台获取该用户对象
     *
     * @param userPhone
     */
    private void getUserInfoByHttp(String userPhone) {
        String requestUrl = Constant.HOST_URL + "user/queryByPhone/" + userPhone;
        Request request = new Request.Builder().url(requestUrl).get().build();
        OkHttpClient client = new OkHttpClient();
        try {
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 网络请求校验
     *
     * @param userPhone
     * @param password
     * @return
     */
    private boolean isLegalUser(String userPhone, String password) {

//        网络请求返回该用户的所有信息
        getUserInfoByHttp(userPhone);
//        todo 存在线程问题：如果当前用户没请求回来，则该线程空转
        while (!requestStatus) {
        }
//        没有返回User说明没有该用户
        if (this.legalUser == null) {
            Log.e(TAG, "不存在该用户或手机号码错误");
            Toast.makeText(LoginActivity.this, "不存在该用户或手机号码错误", Toast.LENGTH_SHORT).show();
            return false;
        }

//        根据网络返回的信息判断是否合法用户
        if (!userPhone.equals(this.legalUser.getPhone()) || !password.equals(this.legalUser.getPassword())) {
            Log.e(TAG, "用户手机号或密码错误");
            Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
            return false;
        }

//        保存当前用户到Application中
        application.loginUser = this.legalUser;
        Log.e(TAG, "当前登录用户信息："+application.loginUser.toString());
        return true;

    }
}