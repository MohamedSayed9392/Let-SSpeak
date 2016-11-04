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
import com.makeramen.roundedimageview.RoundedImageView;
import com.memoseed.letsspeak.Activities.MessagesActiviry_;
import com.memoseed.letsspeak.Models.ChatItem;
import com.memoseed.letsspeak.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ChatsMainAdapter extends RecyclerView.Adapter<ChatsMainAdapter.ViewHolderItem> {

    public List<ChatItem> listChat = new ArrayList<>();
    Context context;
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy - hh:mm aaa");
    public static class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView txtName,txtLMessage,txtDate;
        ImageView imLine ;
        RoundedImageView image;
        LinearLayout linChat;


        public ViewHolderItem(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtLMessage = (TextView) itemView.findViewById(R.id.txtLMessage);
            imLine = (ImageView) itemView.findViewById(R.id.imLine);
            image = (RoundedImageView) itemView.findViewById(R.id.image);
            linChat = (LinearLayout) itemView.findViewById(R.id.linChat);
        }


    }


    public ChatsMainAdapter(List<ChatItem> listChat, Context context) {
        format.setTimeZone(TimeZone.getDefault());
        this.listChat = listChat;
        this.context = context;
    }

    @Override
    public ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolderItem(view);
    }

    public void addChat(ChatItem post){
        listChat.add(post);
        notifyDataSetChanged();
    }


    public void addChats(List<ChatItem> posts){
        listChat.addAll(posts);
        notifyDataSetChanged();
    }

    public void removeChats(){
        listChat.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolderItem holder, int position) {
        ChatItem chatItem = listChat.get(position);
        if(position==0){
            holder.imLine.setVisibility(View.GONE);
        }else{
            holder.imLine.setVisibility(View.VISIBLE);
        }
        holder.txtName.setText(chatItem.getName());
        holder.txtLMessage.setText(chatItem.getLastMessage());
        holder.linChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MessagesActiviry_.class)
                        .putExtra("senderId",chatItem.getSenderId())
                        .putExtra("chatId",chatItem.getObjectId()));
            }
        });
        holder.txtDate.setText(format.format(chatItem.getDate()));
      //  Glide.with(context).load(chatItem.getImage()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
