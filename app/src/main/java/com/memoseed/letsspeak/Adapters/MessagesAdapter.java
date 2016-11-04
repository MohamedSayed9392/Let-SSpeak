package com.memoseed.letsspeak.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.roundview.RoundTextView;
import com.memoseed.letsspeak.Models.MessageItem;
import com.memoseed.letsspeak.R;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolderItem> {

    public List<MessageItem> listMessages = new ArrayList<>();
    Context context;
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy - hh:mm a");
    public static class ViewHolderItem extends RecyclerView.ViewHolder {

        LinearLayout linOther,linMe;
        TextView txtDateOther , txtDateMe ;
        RoundTextView txtMessageOther,txtMessageMe ;

        public ViewHolderItem(View itemView) {
            super(itemView);
            linOther = (LinearLayout) itemView.findViewById(R.id.linOther);
            linMe  = (LinearLayout) itemView.findViewById(R.id.linMe);
            txtDateOther  = (TextView) itemView.findViewById(R.id.txtDateOther);
            txtDateMe = (TextView) itemView.findViewById(R.id.txtDateMe);
            txtMessageOther = (RoundTextView) itemView.findViewById(R.id.txtMessageOther);
            txtMessageMe = (RoundTextView) itemView.findViewById(R.id.txtMessageMe);
        }


    }


    public MessagesAdapter(List<MessageItem> listMessages, Context context) {
        this.listMessages = listMessages;
        this.context = context;
    }

    @Override
    public ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolderItem(view);
    }

    public void addMessage(MessageItem messageItem){
        listMessages.add(0,messageItem);
        notifyDataSetChanged();
    }


    public void addMessages(List<MessageItem> posts){
        listMessages.addAll(posts);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolderItem holder, int position) {
        MessageItem messageItem = listMessages.get(position);
        if(ParseUser.getCurrentUser().getObjectId().matches(messageItem.getSenderId())){
            holder.linMe.setVisibility(View.VISIBLE);
            holder.linOther.setVisibility(View.GONE);
            holder.txtDateMe.setText(format.format(messageItem.getDate()));
            holder.txtMessageMe.setText(messageItem.getMessage());
        }else{
            holder.linOther.setVisibility(View.VISIBLE);
            holder.linMe.setVisibility(View.GONE);
            holder.txtDateOther.setText(format.format(messageItem.getDate()));
            holder.txtMessageOther.setText(messageItem.getMessage());
        }

        holder.txtMessageOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.txtDateOther.getVisibility()==View.VISIBLE){
                    holder.txtDateOther.setVisibility(View.GONE);
                }else{
                    holder.txtDateOther.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.txtMessageMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.txtDateMe.getVisibility()==View.VISIBLE){
                    holder.txtDateMe.setVisibility(View.GONE);
                }else{
                    holder.txtDateMe.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
