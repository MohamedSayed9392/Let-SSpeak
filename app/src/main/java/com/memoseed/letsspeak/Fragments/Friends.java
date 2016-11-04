package com.memoseed.letsspeak.Fragments;

/**
 * Created by MemoSeed on 16/02/2016.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.memoseed.letsspeak.Activities.MainActivity;
import com.memoseed.letsspeak.Adapters.FriendsMainAdapter;
import com.memoseed.letsspeak.Models.FriendItem;
import com.memoseed.letsspeak.R;
import com.memoseed.letsspeak.UTils.UTils;
import com.parse.CountCallback;
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
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


@EFragment(R.layout.fragment_friends)
public class Friends extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @ViewById
    RecyclerView rView;
    @ViewById
    FloatingActionButton fbAdd;
    @Click
    void fbAdd(){
        addDialog();
    }

    public void addDialog(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(R.string.add_friend);
        LinearLayout linear=new LinearLayout(getActivity());
        linear.setOrientation(LinearLayout.VERTICAL);

        final EditText editText = new EditText(getActivity());
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setGravity(Gravity.CENTER);
        editText.setTextSize(15);
        editText.setHint(R.string.enter_friend_id);

        linear.addView(editText);
        alert.setView(linear);

        alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                dialog.cancel();
                UTils.showProgressDialog(getString(R.string.adding),getResources().getString(R.string.pleaseWait), MainActivity.pD);
                ParseQuery query = ParseQuery.getQuery("_User");
                query.whereEqualTo("objectId",editText.getText().toString());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if(e==null) {
                            if (object == null) {
                                Log.d("Friends","1");
                                UTils.hideProgressDialog(MainActivity.pD);
                                Toast.makeText(getActivity(), "User Not Found", Toast.LENGTH_SHORT).show();
                            }else{
                                ParseQuery query1 = ParseQuery.getQuery("Friend");
                                query1.whereEqualTo("from",ParseUser.getCurrentUser());
                                query1.whereEqualTo("to",object);
                                query1.countInBackground(new CountCallback() {
                                    @Override
                                    public void done(int count, ParseException e) {
                                        if(e==null){
                                            if(count==0){

                                                ParseObject parseObject = new ParseObject("Friend");
                                                parseObject.put("from",ParseUser.getCurrentUser());
                                                parseObject.put("to",object);
                                                parseObject.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if(e==null){
                                                            ParseObject parseObject1 = new ParseObject("Friend");
                                                            parseObject1.put("to",ParseUser.getCurrentUser());
                                                            parseObject1.put("from",object);
                                                            parseObject1.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if(e==null){
                                                                        Log.d("Friends","4");
                                                                        Toast.makeText(getActivity(), "Adding done", Toast.LENGTH_SHORT).show();
                                                                        getFriends();
                                                                        JSONObject messagee = new JSONObject();
                                                                        try {
                                                                            messagee.put("image",object.getString("image"));
                                                                            messagee.put("name",object.getString("full_name"));
                                                                            messagee.put("objectId",object.getString(object.getObjectId()));
                                                                        } catch (JSONException e1) {
                                                                            e1.printStackTrace();
                                                                        }
                                                                        UTils.sendPush("newFriend",messagee.toString(),object.getString("device_id"));
                                                                    }else{
                                                                        Log.d("Friends","5");
                                                                        UTils.hideProgressDialog(MainActivity.pD);
                                                                        e.printStackTrace();
                                                                        Toast.makeText(getActivity(), getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }else{
                                                            Log.d("Friends","5");
                                                            UTils.hideProgressDialog(MainActivity.pD);
                                                            e.printStackTrace();
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }else{
                                                UTils.hideProgressDialog(MainActivity.pD);
                                                Toast.makeText(getActivity(), R.string.already_friend, Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            e.printStackTrace();
                                        }
                                    }
                                });


                            }
                        }else{
                            Log.d("Friends","7");
                            UTils.hideProgressDialog(MainActivity.pD);
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                        }
                    }

                });


            }
        });

        alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                dialog.cancel();
            }
        });


        alert.show();

    }

    public FriendsMainAdapter friendsMainAdapter;
    List<FriendItem> list = new ArrayList<>();

    @AfterViews
    void afterViews(){
        friendsMainAdapter = new FriendsMainAdapter(list,getActivity());
        rView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rView.setAdapter(friendsMainAdapter);
    }

    public void getFriends(){
        if(UTils.isOnline(getActivity())) {
            UTils.showProgressDialog(getString(R.string.loading),getResources().getString(R.string.pleaseWait), MainActivity.pD);

                ParseQuery query = ParseQuery.getQuery("Friend");
                query.include("from");
                query.include("to");
                query.whereEqualTo("from",ParseUser.getCurrentUser());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if(objects==null || objects.size()==0){
                                UTils.hideProgressDialog(MainActivity.pD);
                                Toast.makeText(getActivity(),"No Friends", Toast.LENGTH_SHORT).show();
                            }else{
                                friendsMainAdapter.removeFriends();
                                for (ParseObject object : objects) {
                                    friendsMainAdapter.addFriend(new FriendItem("",
                                            object.getParseObject("to").getString("full_name"),
                                            object.getParseObject("to").getObjectId()));
                                }
                                MainActivity.fbRefresh.setVisibility(View.GONE);
                                UTils.hideProgressDialog(MainActivity.pD);
                            }

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
