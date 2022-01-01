package com.sustart.shdsystem.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sustart.shdsystem.R;
import com.sustart.shdsystem.common.Constant;
import com.sustart.shdsystem.entity.Product;

import java.util.List;

/**
 * 展示商品栏的数据适配器
 */
public class ProductCardRecyclerViewAdapter extends RecyclerView.Adapter<ProductCardViewHolder> {

    private List<Product> productList;
    private Context context;
    //每一个item的点击事件
    private OnItemClickLitener mOnItemClickLitener;

    ProductCardRecyclerViewAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    //设置回调接口
    public interface OnItemClickLitener {
        void onItemClick(Product product, View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new ProductCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardViewHolder holder, final int position) {
// todo position爆红但可用
        if (productList != null && position < productList.size()) {
//             绑定数据
            Product product = productList.get(position);
            holder.productName.setText(product.getName());
            holder.productPrice.setText("￥" + product.getPrice());

            String imageUri = Constant.HOST_URL + "static/" + product.getImageUrl();
            Glide.with(context).load(imageUri)
                    .into(holder.productImage);

//设置item的监听事件，该Item被点击后，将该Item对应的Product传送到HomeFragment中，供详情页使用
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickLitener != null) {
                        mOnItemClickLitener.onItemClick(product, v, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}