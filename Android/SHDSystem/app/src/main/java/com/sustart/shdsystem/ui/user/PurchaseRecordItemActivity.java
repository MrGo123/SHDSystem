package com.sustart.shdsystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sustart.shdsystem.R;
import com.sustart.shdsystem.common.BaseResponse;
import com.sustart.shdsystem.common.Constant;
import com.sustart.shdsystem.entity.Product;
import com.sustart.shdsystem.entity.User;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PurchaseRecordItemActivity extends AppCompatActivity {
    private String TAG = "PurchaseRecordItemActivity.class";
    private TextView productSellerName;
    private TextView productSellerAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_record_item);

        Intent intent = getIntent();
        Product recordProduct = (Product) intent.getSerializableExtra("recordProduct");
        ImageView productImage = findViewById(R.id.purchase_record_item_product_image);
        TextView productName = findViewById(R.id.purchase_record_item_product_name);
        TextView productPrice = findViewById(R.id.purchase_record_item_product_price);
        TextView productDesc = findViewById(R.id.purchase_record_item_product_desc);
        TextView productType = findViewById(R.id.purchase_record_item_product_type);
//        TextView productPublishTime = findViewById(R.id.detail_product_publish_time);
        productSellerName = findViewById(R.id.purchase_record_item_seller_name);
        productSellerAddress = findViewById(R.id.purchase_record_item_seller_address);

        String imageUri = Constant.HOST_URL + "static/" + recordProduct.getImageUrl();
        Glide.with(PurchaseRecordItemActivity.this).load(imageUri)
                .into(productImage);
        productName.setText(recordProduct.getName());
        productPrice.setText("￥" + recordProduct.getPrice());
        productDesc.setText(recordProduct.getDescription());
        productType.setText(recordProduct.getType());
//        productPublishTime.setText(detailProduct.getPublishTime() + "");

        getUserInfoByHttp(recordProduct.getSellerId());
    }

    /**
     * 通过后台获取该用户对象
     *
     * @param userId
     */
    private void getUserInfoByHttp(String userId) {
        String requestUrl = Constant.HOST_URL + "user/queryById/" + userId;
        Request request = new Request.Builder().url(requestUrl).get().build();
        OkHttpClient client = new OkHttpClient();
        try {
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private okhttp3.Callback callback = new okhttp3.Callback() {

        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
//            响应成功
            if (response.isSuccessful()) {
//                获取响应数据体
                String body = response.body().string();
                Log.e(TAG, "用户信息接口服务器返回数据：" + body);

                if (body != null) {
                    Gson gson = new Gson();
                    Type jsonType = new TypeToken<BaseResponse<User>>() {
                    }.getType();
                    BaseResponse<User> userResponse = gson.fromJson(body, jsonType);
                    User user = userResponse.getData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            productSellerName.setText(user.getName());
                            productSellerAddress.setText(user.getAddress());
                        }
                    });
                }

            }
        }

        @Override
        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
            Log.e(TAG, "连接服务器失败! ");
            e.printStackTrace();
        }
    };

}