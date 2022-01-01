package com.sustart.shdsystem.ui.home.detail;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sustart.shdsystem.MainActivity;
import com.sustart.shdsystem.R;
import com.sustart.shdsystem.SHDSystemApplication;
import com.sustart.shdsystem.common.Constant;
import com.sustart.shdsystem.entity.Product;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 购买支付页
 */
public class PurchaseActivity extends AppCompatActivity {
    private String TAG = "PurchaseActivity.class";
    private Button paidBtn;
    private SHDSystemApplication application;
    private Product product;

    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.e(TAG, "商品支付成功，接口返回数据：" + body);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PurchaseActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
                        Intent intentToMainActivity = new Intent(PurchaseActivity.this, MainActivity.class);
                        startActivity(intentToMainActivity);
                    }
                });
            }
        }

        @Override
        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
            Log.e(TAG, "连接服务器失败! ");
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PurchaseActivity.this, "购买失败，服务器异常", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        application = (SHDSystemApplication) getApplicationContext();

        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("buyProduct");

        paidBtn = findViewById(R.id.purchase_paid_btn);
        paidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setBuyerId(application.loginUser.getId() + "");
                updateByHttp();
            }
        });
    }

    private void updateByHttp() {

        String requestUrl = Constant.HOST_URL + "product/edit";
//        todo                 .add("dealTime", currentTimestamp + "")
        RequestBody requestBody = new FormBody.Builder()
                .add("id", product.getId()+"")
                .add("name", product.getName())
                .add("price", String.valueOf(product.getPrice()))
                .add("imageUrl", product.getImageUrl())
                .add("type", product.getType())
                .add("description", product.getDescription())
                .add("sellerId", product.getSellerId())
                .add("buyerId", product.getBuyerId())
                .build();
        Request request = new Request.Builder().url(requestUrl).post(requestBody).build();

        OkHttpClient client = new OkHttpClient();
        try {
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
            return;
        }
    }
}