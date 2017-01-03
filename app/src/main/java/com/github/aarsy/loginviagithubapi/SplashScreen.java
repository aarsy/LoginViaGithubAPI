package com.github.aarsy.loginviagithubapi;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.github.aarsy.loginviagithubapi.githublogin.ApplicationData;
import com.github.aarsy.loginviagithubapi.githublogin.GithubApp;
import com.github.aarsy.loginviagithubapi.githublogin.LoginActivity;


/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener {


    private TextView tv;
    private Animation animOvershoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        animOvershoot = AnimationUtils.loadAnimation(this, R.anim.overshoot_anim);
        tv = (TextView) findViewById(R.id.splash_logo);
        animOvershoot.setAnimationListener(this);
        tv.startAnimation(animOvershoot);

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animOvershoot) {
            GithubApp mApp = new GithubApp(this, ApplicationData.CLIENT_ID,
                    ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
            if(mApp.hasAccessToken()) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }else{
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }
        }
    }


    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

