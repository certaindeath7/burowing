package com.example.burowing2;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

// ViewHolder holds item view and data about its place within RecylerView
// this viewholder is used for products recycler view
public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView titleTV, descriptionTV ;
    public ImageView imageView;
    public ItemClickListener listener;

    public ViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.rowImage);
        titleTV = (TextView) itemView.findViewById(R.id.rowTitleTv);
        descriptionTV = (TextView) itemView.findViewById(R.id.rowDescritpionTv);
    }

    // constructor
    public void setItemCLickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onCLick(v, getAdapterPosition(),false);
    }
}
