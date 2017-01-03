package com.github.aarsy.loginviagithubapi.githublogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aarsy.loginviagithubapi.MainActivity;
import com.github.aarsy.loginviagithubapi.R;
import com.github.aarsy.loginviagithubapi.common.CommonGlobalVariable;


/**
 * Created by abhay yadav on 05-Dec-16.
 */

public class LoginActivity extends AppCompatActivity {

    private GithubApp mApp;
    private TextView btnSkip;
    private TextView tvSummary;
    private RelativeLayout btnConnect;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mApp = new GithubApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mApp.setListener(listener);
        context = this;

        btnSkip= (TextView) findViewById(R.id.btnskip);
        btnConnect = (RelativeLayout) findViewById(R.id.rl_connect);
        btnConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mApp.hasAccessToken()) {
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                } else {
                    if(CommonGlobalVariable.checkInternetConnection(context))
                        mApp.authorize();
                    else{
                        CommonGlobalVariable.alertDailog(context);
                    }
                }
            }
        });

        btnSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });

    }

    GithubApp.OAuthAuthenticationListener listener = new GithubApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }

        @Override
        public void onFail(String error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        }
    };
}