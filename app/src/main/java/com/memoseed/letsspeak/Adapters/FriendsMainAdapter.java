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
import com.memoseed.letsspeak.Models.FriendItem;
import com.memoseed.letsspeak.R;

import java.util.ArrayList;
import java.util.List;

public class FriendsMainAdapter extends RecyclerView.Adapter<FriendsMainAdapter.ViewHolderItem> {

    public List<FriendItem> listFriend = new ArrayList<>();
    Context context;

    public static class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imLine ;
        RoundedImageView image;
        LinearLayout linFriend;


        public ViewHolderItem(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            imLine = (ImageView) itemView.findViewById(R.id.imLine);
            image = (RoundedImageView) itemView.findViewById(R.id.image);
            linFriend = (LinearLayout) itemView.findViewById(R.id.linFriend);
        }


    }


    public FriendsMainAdapter(List<FriendItem> listFriend, Context context) {
        this.listFriend = listFriend;
        this.context = context;
    }

    @Override
    public ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new ViewHolderItem(view);
    }

    public void addFriend(FriendItem post){
        listFriend.add(post);
        notifyDataSetChanged();
    }


    public void addFriends(List<FriendItem> posts){
        listFriend.addAll(posts);
        notifyDataSetChanged();
    }

    public void removeFriends(){
        listFriend.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolderItem holder, int position) {
        FriendItem friendItem = listFriend.get(position);
        if(position==0){
            holder.imLine.setVisibility(View.GONE);
        }else{
            holder.imLine.setVisibility(View.VISIBLE);
        }
        holder.linFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MessagesActiviry_.class)
                        .putExtra("senderId",friendItem.getObjectId()));
            }
        });
        holder.txtName.setText(friendItem.getName());
      //  Glide.with(context).load(friendItem.getImage()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return listFriend.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
