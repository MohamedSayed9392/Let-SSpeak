package com.memoseed.letsspeak.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.memoseed.letsspeak.GCM.GcmRegistrationAsyncTask;
import com.memoseed.letsspeak.R;
import com.memoseed.letsspeak.UTils.UTils;
import com.parse.ParseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends Activity {

    @ViewById
    RelativeLayout rlLogo;
    Animation rotate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void afterViews(){
        rotate = AnimationUtils.loadAnimation(this,R.anim.rotate);
        rotate.setInterpolator(new OvershootInterpolator());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rlLogo.startAnimation(rotate);
            }
        },1000);
        new GcmRegistrationAsyncTask(this).execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        },5000);
    }

    private void checkUser(){
        if(UTils.isOnline(getApplicationContext())) {
            if (ParseUser.getCurrentUser() != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity_.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity_.class));
                finish();
            }
        }else {
            UTils.show2OptionsDialoge(SplashActivity.this, getResources().getString(R.string.no_internet),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            checkUser();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    },"Try Again","Exit");
        }
    }
}
