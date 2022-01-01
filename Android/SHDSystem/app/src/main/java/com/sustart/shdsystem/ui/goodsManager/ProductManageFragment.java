package com.sustart.shdsystem.ui.goodsManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sustart.shdsystem.R;
import com.sustart.shdsystem.SHDSystemApplication;
import com.sustart.shdsystem.common.Constant;
import com.sustart.shdsystem.databinding.FragmentProductManageBinding;
import com.sustart.shdsystem.entity.Product;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 该用的发布的商品的管理页
 */
public class ProductManageFragment extends Fragment {
    private String TAG = "ProductManageFragment.class";

    private ListView productsListView;
    private ProductManageAdapter adapter;
    private View view;
    private FloatingActionButton floatingActionButton;

    private SHDSystemApplication application;


    private FragmentProductManageBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        // 通过application获取当前登录的的用户信息
        application = (SHDSystemApplication) getContext().getApplicationContext();
        Log.e(TAG, "商品管理页监听到用户：" + application.loginUser.toString());
        initView();
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.e(TAG, "用户商品管理接口请求到数据：" + body);

                Gson gson = new Gson();
                Type jsonType = new TypeToken<List<Product>>() {
                }.getType();
                List<Product> productDataList = gson.fromJson(body, jsonType);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ProductManageAdapter(getContext(), R.layout.product_list_item, productDataList);
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

    private void initData() {

        String requestParam = "product/queryBySellerId/" + application.loginUser.getId();
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
        floatingActionButton = view.findViewById(R.id.floating_action_button);
        floatingActionButtonSetListener();

        productsListView = view.findViewById(R.id.lv_product_manage_list);
        productsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int i, long l) {
                        Intent intent = new Intent(getContext(), ProductManageDetailActivity.class);
                        Product product = adapter.getItem(i);
                        intent.putExtra("manageItemProduct", product);
                        startActivity(intent);
                    }
                });
    }

    /**
     * 浮动按钮点击后打开发布商品页PostProductActivity
     */
    private void floatingActionButtonSetListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToProductActivity = new Intent(getActivity(), PostProductActivity.class);
                startActivity(intentToProductActivity);
            }
        });
    }

}