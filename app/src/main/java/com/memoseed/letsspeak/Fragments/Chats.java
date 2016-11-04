package com.memoseed.letsspeak.Fragments;

/**
 * Created by MemoSeed on 16/02/2016.
 */

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.memoseed.letsspeak.Activities.MainActivity;
import com.memoseed.letsspeak.Adapters.ChatsMainAdapter;
import com.memoseed.letsspeak.Models.ChatItem;
import com.memoseed.letsspeak.R;
import com.memoseed.letsspeak.UTils.UTils;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


@EFragment(R.layout.fragment_chats)
public class Chats extends Fragment {
   

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return null;
    }

    @ViewById
    RecyclerView rView;
    public ChatsMainAdapter chatsMainAdapter;
    List<ChatItem> list = new ArrayList<>();

    @AfterViews
    void afterViews(){
        chatsMainAdapter = new ChatsMainAdapter(list,getActivity());
        rView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rView.setAdapter(chatsMainAdapter);
    }

    public void getChats(){
        if(UTils.isOnline(getActivity())) {
            UTils.showProgressDialog(getString(R.string.loading),getResources().getString(R.string.pleaseWait), MainActivity.pD);
            ParseQuery query = ParseQuery.getQuery("Chats");
            List<String> list = new ArrayList<>();
            list.add(ParseUser.getCurrentUser().getObjectId());
            query.whereContainedIn("users", list);
            query.include("from");
            query.include("to");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        chatsMainAdapter.removeChats();
                        for (ParseObject object : objects) {
                            Log.d("Chats CUrrent",ParseUser.getCurrentUser().getObjectId());
                            Log.d("Chats from",object.getParseObject("from").getObjectId());
                            if( object.getParseObject("from").getObjectId().matches(ParseUser.getCurrentUser().getObjectId())){
                                chatsMainAdapter.addChat(new ChatItem("",
                                        object.getString("last_message"),
                                        object.getParseObject("to").getString("full_name"),
                                        object.getParseObject("to").getObjectId(),
                                        object.getObjectId()));
                            }else{
                                chatsMainAdapter.addChat(new ChatItem("",
                                        object.getString("last_message"),
                                        object.getParseObject("from").getString("full_name"),
                                        object.getParseObject("from").getObjectId(),
                                        object.getObjectId()));
                            }

                        }
                        MainActivity.fbRefresh.setVisibility(View.GONE);
                        UTils.hideProgressDialog(MainActivity.pD);
                    } else {
                        UTils.hideProgressDialog(MainActivity.pD);
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            MainActivity.fbRefresh.setVisibility(View.VISIBLE);
            UTils.hideProgressDialog(MainActivity.pD);
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
}
