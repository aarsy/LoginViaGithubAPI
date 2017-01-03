package com.github.aarsy.loginviagithubapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aarsy.loginviagithubapi.common.CommonGlobalVariable;
import com.github.aarsy.loginviagithubapi.common.PicassoCircularImageView;
import com.github.aarsy.loginviagithubapi.database.DatabaseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class ProfileActivity extends AppCompatActivity {

    private Context context;
    private UserModel user;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView userPhoto = (ImageView) findViewById(R.id.user_photo);
        String user_name = getIntent().getStringExtra("login");
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        String userdata = dbHandler.getUserProfile(user_name);
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        TextView bio = (TextView) nestedScrollView.findViewById(R.id.tvBio);
        recyclerView = (RecyclerView) nestedScrollView.findViewById(R.id.recyclerView);
        TextView name = (TextView) nestedScrollView.findViewById(R.id.tvName);
        TextView location = (TextView) nestedScrollView.findViewById(R.id.tvLocation);
        TextView followers=(TextView) nestedScrollView.findViewById(R.id.tvFollowers);
        TextView following=(TextView) nestedScrollView.findViewById(R.id.tvFollowing);
        recyclerView.setNestedScrollingEnabled(false);
        JSONObject json = null;
        try {
            json = new JSONObject(userdata);
            user = new UserModel(json, context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        toolbar.setTitle(user.getUsername());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bio.setText(user.getBio());
        name.setText(user.getName());
        followers.setText(user.getFollowers());
        following.setText(user.getFollowing());
        location.setText(user.getLocation());
        Picasso.with(context).load(user.getAvatar_url()).transform(new PicassoCircularImageView()).into(userPhoto);
        String userRepos = dbHandler.getUserAllRepos(user_name);
        if (userRepos == null) {
            if (!CommonGlobalVariable.checkInternetConnection(context)) {
                alertDailog(context);
            }else {
                new Get_Github_User_Repos().execute(user_name, user.getRepos_url());
            }
        } else {
            setAdapterData(userRepos, user_name);
        }
    }

    private void setAdapterData(String data, String userName) {
        List<RepoModel> reposList = new ArrayList<>();
        JSONArray res = null;
        try {
            res = new JSONArray(data);
            for (int i = 0; i < res.length(); i++) {
                JSONObject jsonObject = res.getJSONObject(i);
                reposList.add(new RepoModel(jsonObject, context));
            }
            ReposListCustomAdapter adapter = new ReposListCustomAdapter(reposList, (Activity) context, userName);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class Get_Github_User_Repos extends AsyncTask<Object, Void, String> {
        ProgressDialog progressDialog;
        String userName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("onPre", "true");
            progressDialog = ProgressDialog.show(context, "", "Fetching Repos...");
        }

        @Override
        protected String doInBackground(Object... params) {
            userName = params[0].toString();
            return confirm(params[1].toString());
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response11 ", " " + result);
            Log.d("onPost", "true");
            JSONArray res = null;
            if (result != null) {
                try {
                    res = new JSONArray(result);
                    if (res.length() > 0) {
                        DatabaseHandler dbHandler = new DatabaseHandler(context);
                        dbHandler.updateUserRepos(userName, result);
                        dbHandler.close();
                        setAdapterData(result, userName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Something went wrong...\n Please try again", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    private static String confirm(String url) {
        try {
            Log.d("Urllll", url);
            String json = CommonGlobalVariable.get(url);
            return json;
        } catch (Exception e) {
            return null;
        }
    }


    public void alertDailog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle(getResources().getString(R.string.nointernet));

        alertDialogBuilder
                .setMessage(getResources().getString(R.string.checkconnection))
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}