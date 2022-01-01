package com.sustart.shdsystem.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sustart.shdsystem.R;

/**
 * 用于展示在主页home的每一个卡片的商品信息
 */
public class ProductCardViewHolder extends RecyclerView.ViewHolder {
    View itemView;
    public ImageView productImage;
    public TextView productName;
    public TextView productPrice;

    public ProductCardViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        productImage = itemView.findViewById(R.id.detail_image);
        productName = itemView.findViewById(R.id.product_name);
        productPrice = itemView.findViewById(R.id.product_price);
    }
}