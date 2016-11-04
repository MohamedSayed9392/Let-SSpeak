package com.memoseed.letsspeak.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.memoseed.letsspeak.R;
import com.memoseed.letsspeak.UTils.TempValues;
import com.memoseed.letsspeak.UTils.UTils;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import info.hoang8f.widget.FButton;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    @ViewById
    LinearLayout linLogin;
    @ViewById
    EditText editMailL;
    @ViewById
    EditText editPassL;
    @ViewById
    FButton btnLogin;

    @Click
    void btnLogin() {
        if(UTils.isOnline(this)) {
            if (editMailL.getText().toString().isEmpty()) {
                editMailL.setError(getResources().getString(R.string.email));
            } else if (editPassL.getText().toString().isEmpty()) {
                editPassL.setError(getResources().getString(R.string.password));
            } else {
                UTils.showProgressDialog(getResources().getString(R.string.loggingIn), getResources().getString(R.string.pleaseWait), pD);
                ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
                query.whereEqualTo("username", editMailL.getText().toString());
                query.countInBackground(new CountCallback() {
                    @Override
                    public void done(int count, ParseException e) {
                        if (count != 0) {
                            ParseUser.logInInBackground(editMailL.getText().toString(),
                                    editPassL.getText().toString(),
                                    new LogInCallback() {
                                        @Override
                                        public void done(ParseUser user, ParseException e) {
                                            if (e == null) {
                                                UTils.hideProgressDialog(pD);
                                                startActivity(new Intent(LoginActivity.this, MainActivity_.class));
                                                finish();
                                            } else {
                                                UTils.hideProgressDialog(pD);
                                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        } else {
                            UTils.hideProgressDialog(pD);
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.create_account_first), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }else{
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @ViewById
    EditText editNameS;
    @ViewById
    EditText editMailS;
    @ViewById
    EditText editPassS;
    @ViewById
    EditText editPassS2;
    @ViewById
    FButton btnSignUp;

    @Click
    void btnSignUp() {
        if(UTils.isOnline(this)) {
            if (editNameS.getText().toString().isEmpty()) {
                editNameS.setError(getResources().getString(R.string.full_name));
            } else if (editMailS.getText().toString().isEmpty()) {
                editPassS.setError(getResources().getString(R.string.email));
            } else if (editPassS.getText().toString().isEmpty()) {
                editPassS.setError(getResources().getString(R.string.password));
            } else if (editPassS2.getText().toString().isEmpty()) {
                editPassS2.setError(getResources().getString(R.string.password));
            } else if (!editPassS.getText().toString().matches(editPassS2.getText().toString())) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
            } else {
                UTils.showProgressDialog(getResources().getString(R.string.sign_up), getResources().getString(R.string.pleaseWait), pD);
                ParseUser user = new ParseUser();
                user.put("username", editMailS.getText().toString());
                user.put("password", editPassS.getText().toString());
                user.put("email", editMailS.getText().toString());
                user.put("full_name", editNameS.getText().toString());
                user.put("image","");
                user.put("device_id", TempValues.regId);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            UTils.hideProgressDialog(pD);
                            startActivity(new Intent(LoginActivity.this, MainActivity_.class));
                            finish();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }else{
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @ViewById
    LinearLayout linSignUp;
    @ViewById
    LinearLayout linCreateAccount;

    @Click
    void linCreateAccount() {
        linLogin.setVisibility(View.GONE);
        linSignUp.setVisibility(View.VISIBLE);
    }

    ProgressDialog pD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pD = new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        if(linSignUp.getVisibility()==View.VISIBLE){
            linSignUp.setVisibility(View.GONE);
            linLogin.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }

    }
}
