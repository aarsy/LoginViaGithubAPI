package com.github.aarsy.loginviagithubapi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aarsy.loginviagithubapi.common.CommonGlobalVariable;
import com.github.aarsy.loginviagithubapi.database.DatabaseHandler;
import com.github.aarsy.loginviagithubapi.githublogin.ApplicationData;
import com.github.aarsy.loginviagithubapi.githublogin.GithubApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class RepoActivity extends AppCompatActivity {

    private Context context;
    private Button starRepo;
    private Button watchRepo;
    private Button followUser;
    private GithubApp mApp;
    private RepoModel repo;
    private ProgressDialog progressDialog;
    private final int STARRING = 0;
    private final int WATCHING = 1;
    private final int FOLLOWING = 2;
    private final int ALL_CHECK = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repository_details_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        final String userName = getIntent().getStringExtra("login");
        String userRepoId = getIntent().getStringExtra("repoid");
        context = this;

        DatabaseHandler dbHandler = new DatabaseHandler(context);
        try {
            String repoData = dbHandler.getUserRepo(userName, userRepoId);
            JSONObject repoJson = new JSONObject(repoData);
            repo = new RepoModel(repoJson, context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dbHandler.close();
        TextView reponame = (TextView) findViewById(R.id.tvName);
        TextView description = (TextView) findViewById(R.id.tvdes);
        TextView clone_url = (TextView) findViewById(R.id.tvclone);
        TextView stars = (TextView) findViewById(R.id.tvstars);
        TextView forks = (TextView) findViewById(R.id.tvforks);
        TextView openissues = (TextView) findViewById(R.id.tvopenissues);
        starRepo = (Button) findViewById(R.id.btnstar);
        watchRepo = (Button) findViewById(R.id.btnwatch);
        followUser= (Button) findViewById(R.id.btnfollow);
        reponame.setText(repo.getReponame());
        description.setText(repo.getDescription());
        clone_url.setText(repo.getCloneUrl());
        stars.setText(repo.getStargazers_count());
        forks.setText(repo.getForks_count());
        openissues.setText(repo.getOpen_issues_count());
        followUser.setText("Follow "+userName);
        mApp = new GithubApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);

        mApp.setListener(listener);

        starRepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("starslistener", "true");
                if (mApp.hasAccessToken()) {
                    if(CommonGlobalVariable.checkInternetConnection(context))
                        new Get_Starred_Status().execute(userName, repo.getReponame(), true, STARRING);
                    else{
                        CommonGlobalVariable.alertDailog(context);
                    }
                } else {
                    if(CommonGlobalVariable.checkInternetConnection(context))
                        mApp.authorize();
                    else{
                        CommonGlobalVariable.alertDailog(context);
                    }
                }
            }
        });
        watchRepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("starslistener", "true");
                if (mApp.hasAccessToken()) {
                    if(CommonGlobalVariable.checkInternetConnection(context))
                        new Get_Starred_Status().execute(userName, repo.getReponame(), true, WATCHING);
                    else{
                        CommonGlobalVariable.alertDailog(context);
                    }
                } else {
                    if(CommonGlobalVariable.checkInternetConnection(context))
                        mApp.authorize();
                    else{
                        CommonGlobalVariable.alertDailog(context);
                    }
                }
            }
        });
        followUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("starslistener", "true");
                if (mApp.hasAccessToken()) {
                    if(CommonGlobalVariable.checkInternetConnection(context))
                        new Get_Starred_Status().execute(userName, repo.getReponame(), true, FOLLOWING);
                    else{
                        CommonGlobalVariable.alertDailog(context);
                    }
                } else {
                    if(CommonGlobalVariable.checkInternetConnection(context))
                        mApp.authorize();
                    else{
                        CommonGlobalVariable.alertDailog(context);
                    }
                }
            }
        });


//        if (mApp.hasAccessToken()) {
//            if (CommonGlobalVariable.checkInternetConnection(context))
//                new Get_Starred_Status().execute(userName, repo.getReponame(), false, ALL_CHECK);
//        }

    }


    private class Get_Starred_Status extends AsyncTask<Object, Void, Boolean> {

        String userName;
        String repoName;
        boolean starOrNot;
        int starFollowWatch;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", "Checking repo properties...");
        }

        @Override
        protected Boolean doInBackground(Object... params) {

            boolean status = false;
            userName = params[0].toString();
            repoName = params[1].toString();
            starOrNot = (Boolean) params[2];
            starFollowWatch = (Integer) params[3];
            try {
                if (starFollowWatch == STARRING) {
                    String endpoint = CommonGlobalVariable.HOST + CommonGlobalVariable.API_STARRED + userName + "/" + repoName + "?access_token=" + mApp.getAccessToken();
                    status = getIfStarredOrNot(endpoint, starFollowWatch);
                } else if (starFollowWatch == FOLLOWING) {
                    String endpoint = CommonGlobalVariable.HOST + CommonGlobalVariable.API_FOLLOWING + userName + "?access_token=" + mApp.getAccessToken();
                    status = getIfStarredOrNot(endpoint, starFollowWatch);
                } else if (starFollowWatch == WATCHING) {
                    String endpoint = CommonGlobalVariable.HOST + CommonGlobalVariable.API_WATCHING + userName + "/" + repoName +"?access_token=" + mApp.getAccessToken();
                    status = getIfStarredOrNot(endpoint, starFollowWatch);
                }

            } catch (IOException e) {
                return null;
            }
            return status;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("response ", " " + result);
            if (result != null) {
                if (result && starFollowWatch==STARRING) {
                    starRepo.setText("Already Starred");
                    starRepo.setClickable(false);
                    progressDialog.dismiss();
                    starRepo.setOnClickListener(null);
                } else if (starFollowWatch==STARRING) {
                    new Set_Starred_Status().execute(userName, repoName, starFollowWatch);
                }
                if (result && starFollowWatch==WATCHING) {
                    watchRepo.setText("Already Watching");
                    watchRepo.setClickable(false);
                    progressDialog.dismiss();
                    watchRepo.setOnClickListener(null);
                } else if (starFollowWatch==WATCHING) {
                    new Set_Starred_Status().execute(userName, repoName, starFollowWatch);
                }
                if (result && starFollowWatch==FOLLOWING) {
                    followUser.setText("Already Following");
                    followUser.setClickable(false);
                    progressDialog.dismiss();
                    followUser.setOnClickListener(null);
                } else if (starFollowWatch==FOLLOWING) {
                    new Set_Starred_Status().execute(userName, repoName, starFollowWatch);
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(context, "Something went wrong...\n Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean getIfStarredOrNot(String endpoint, int starFollowWatch)
            throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("Starred URl", endpoint + "");
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            String status = conn.getResponseMessage();
            int code = conn.getResponseCode();
            Log.v("status", status);

            if(code==200 && starFollowWatch==WATCHING)  //here 200 means repo is being watched
                return true;
            if (code == 204)        //here 204 means repo has been starred
                return true;
            if (code == 404)  //404 means repo is not starred
                return false;
            else
                throw new IOException("Post failed with error code " + status);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    private class Set_Starred_Status extends AsyncTask<Object, Void, Boolean> {
        String userName;
        String repoName;
        boolean starOrNot;
        int starFollowWatch;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("setstarredonpre", "true");
            progressDialog.setMessage("Starring this repo..");
        }

        @Override
        protected Boolean doInBackground(Object... params) {

            boolean status = false;
            userName = params[0].toString();
            repoName = params[1].toString();

            starFollowWatch = (Integer) params[2];
            try {
                if (starFollowWatch == STARRING) {
                    String endpoint = CommonGlobalVariable.HOST + CommonGlobalVariable.API_STARRED + userName + "/" + repoName + "?Content-Length=0" + "&access_token=" + mApp.getAccessToken();
                    status = setStarredStatus(endpoint, starFollowWatch);
                } else if (starFollowWatch == FOLLOWING) {
                    String endpoint = CommonGlobalVariable.HOST + CommonGlobalVariable.API_FOLLOWING + userName + "?Content-Length=0" + "&access_token=" + mApp.getAccessToken();
                    status = setStarredStatus(endpoint, starFollowWatch);
                } else if (starFollowWatch == WATCHING) {
                    String endpoint = CommonGlobalVariable.HOST + CommonGlobalVariable.API_WATCHING + userName + "/" + repoName +"?Content-Length=0" + "&access_token=" + mApp.getAccessToken();
                    status = setStarredStatus(endpoint, starFollowWatch);
                }

            } catch (IOException e) {
                return null;
            }

            return status;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("response Starred", " " + result);
            progressDialog.dismiss();
            if (result != null) {
                if (result && starFollowWatch==STARRING) {
                    starRepo.setText("Starred");
                    starRepo.setClickable(false);
                    starRepo.setOnClickListener(null);
                }
                if (result && starFollowWatch==WATCHING) {
                    watchRepo.setText("Watching");
                    watchRepo.setClickable(false);
                    watchRepo.setOnClickListener(null);
                }
                if (result && starFollowWatch==FOLLOWING) {
                    followUser.setText("Following");
                    followUser.setClickable(false);
                    followUser.setOnClickListener(null);
                }
            } else {
                Toast.makeText(context, "Something went wrong...\n Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean setStarredStatus(String endpoint, int starWatchFollow)
            throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.d("Starred URl", endpoint + "");
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);


            int code = conn.getResponseCode();
            Log.v("statusCode ", "" + code);

            if(code==200 && starWatchFollow==WATCHING)
                return true;
            if (code == 204) {
                return true;
            } else {
                throw new IOException("Post failed with error code " + code);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    private static String get(String endpoint)
            throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);


            String status = conn.getResponseMessage();
            int code = conn.getResponseCode();
            Log.v("status", status);

            if (status.equalsIgnoreCase("OK")) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            } else {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    GithubApp.OAuthAuthenticationListener listener = new GithubApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {
            Toast.makeText(context, "Successfully logged in..\nClick button again", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFail(String error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
