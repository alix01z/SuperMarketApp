package com.AlixZDev01.supermarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.AlixZDev01.supermarket.R;
import com.AlixZDev01.supermarket.activity.ChosenProductActivity;
import com.AlixZDev01.supermarket.database.cart_db.ProductDatabase;
import com.AlixZDev01.supermarket.database.cart_db.ProductEntity;
import com.AlixZDev01.supermarket.fragment.CartFragment;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<ProductEntity> productList;
    private ProductDatabase productDB;
    private CartFragment cartFragment;
    private FragmentManager fragmentManager;

    public CartAdapter(Context context, List<ProductEntity> productList, ProductDatabase productDB , CartFragment cartFragment ,
                       FragmentManager fragmentManager) {
        this.context = context;
        this.productList = productList;
        this.productDB = productDB;
        this.cartFragment = cartFragment;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_recyclerv , parent
        ,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductEntity productE = productList.get(position);
        int totalCost = calculateTotalCost();
        cartFragment.updateTotalCost(totalCost);
        holder.setDataInCartAdapter(productE , context , position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoCPIntent = new Intent(context , ChosenProductActivity.class);
                gotoCPIntent.putExtra("idTransfer" , productE.getProduct_ID());
                context.startActivity(gotoCPIntent);
            }
        });
        holder.imgbtnCartDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDB.getProductDao().deleteProduct(productE);
                int adapterPositionAfterDelete = holder.getAdapterPosition();
                productList.remove(adapterPositionAfterDelete);
                notifyItemRemoved(adapterPositionAfterDelete);
                notifyItemRangeChanged(adapterPositionAfterDelete , productList.size());
                int newTotalCost = calculateTotalCost();
                cartFragment.updateTotalCost(newTotalCost);
                if (productList.size() == 0){
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.fragment_container , new CartFragment()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{
        CardView cardvCart;
        ImageView imgvCart;
        TextView txtvCartTitle;
        TextView txtvCartPrice;
        TextView txtCartAmount;
        ImageButton imgbtnCartDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cardvCart = itemView.findViewById(R.id.cardv_cart);
            imgvCart = itemView.findViewById(R.id.imgv_cart_product);
            txtvCartPrice = itemView.findViewById(R.id.txt_cart_product_price);
            txtvCartTitle = itemView.findViewById(R.id.txt_cart_product_title);
            txtCartAmount = itemView.findViewById(R.id.txt_cart_amount);
            imgbtnCartDelete = itemView.findViewById(R.id.imgbtn_cart_delete);

        }
        public void setDataInCartAdapter(ProductEntity productE , Context context , int position){
            txtvCartTitle.setText(productE.getTitle_fa());
            txtvCartPrice.setText(formatPrice(productE.getSelling_price() * productE.getAmount()));
            txtCartAmount.setText(String.valueOf(productE.getAmount()));
            Glide.with(context)
                    .load(productE.getWebp_url())
                    .into(imgvCart);
        }
        private String formatPrice(int number){
            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("fa"));
            return numberFormat.format(number / 10);
        }
    }
    public int calculateTotalCost() {
        int totalCost = 0;
        for (ProductEntity product : productList) {
            totalCost += (product.getSelling_price() * product.getAmount());
        }
        return totalCost;
    }
}
