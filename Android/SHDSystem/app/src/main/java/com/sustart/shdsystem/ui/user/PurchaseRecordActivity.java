package com.sustart.shdsystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sustart.shdsystem.R;
import com.sustart.shdsystem.SHDSystemApplication;
import com.sustart.shdsystem.common.Constant;
import com.sustart.shdsystem.entity.Product;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用户的购买记录页
 */
public class PurchaseRecordActivity extends AppCompatActivity {
    private String TAG = "PurchaseRecordActivity.class";

    private ListView productsListView;
    private PurchaseRecordProductAdapter adapter;

    private SHDSystemApplication application;

    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.e(TAG, "商品购买记录接口请求到数据：" + body);
                Gson gson = new Gson();
                Type jsonType = new TypeToken<List<Product>>() {
                }.getType();
                List<Product> productDataList = gson.fromJson(body, jsonType);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new PurchaseRecordProductAdapter(PurchaseRecordActivity.this, R.layout.purchase_record_list_item, productDataList);
                        productsListView.setAdapter(adapter);
                    }
                });
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
        setContentView(R.layout.activity_purchase_record);

        application = (SHDSystemApplication) getApplicationContext();
        Log.e(TAG, "商品购买记录页监听到用户：" + application.loginUser.toString());

        initView();
        initData();

    }
    private void initData() {

        String requestParam = "product/queryByBuyerId/" + application.loginUser.getId();
//                发送用户id到后台，后台在Product数据库中根据手机号查找该sellerId字段，返回符合的所有Product。安卓端根据需要动态渲染
        String requestUrl = Constant.HOST_URL + requestParam;
        Request request = new Request.Builder().url(requestUrl).get().build();
        OkHttpClient client = new OkHttpClient();
        try {
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取列表对象并绑定事件监听器、Intent
     */
    private void initView() {
        productsListView = findViewById(R.id.my_purchase_record_list);
        productsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int i, long l) {
                        Intent intent = new Intent(PurchaseRecordActivity.this, PurchaseRecordItemActivity.class);
                        Product product = adapter.getItem(i);
                        intent.putExtra("recordProduct", product);
                        startActivity(intent);
                    }
                });
    }
}