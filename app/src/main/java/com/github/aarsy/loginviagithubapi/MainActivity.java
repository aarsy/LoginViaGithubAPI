package com.github.aarsy.loginviagithubapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.aarsy.loginviagithubapi.common.CommonGlobalVariable;
import com.github.aarsy.loginviagithubapi.database.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class MainActivity extends AppCompatActivity {

    private ProfilesHistoryCustomAdapter adapter;
    private RecyclerView recyclerView = null;
    private Activity activity;
    private long lastPress = 0L;
    private SearchView searchView;
    private RelativeLayout rl_no_history;
    private List<UserModel> usersList;
    private MenuItem mSearchMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        rl_no_history = (RelativeLayout) findViewById(R.id.rl_nohistory);
        usersList = new ArrayList<>();
        getAllEvents();
        handleIntent(getIntent());
    }


    void getAllEvents() {
        DatabaseHandler dbHandler = new DatabaseHandler(activity);
        List<String> allProfilesUsername = dbHandler.getAllProfiles();
        Log.d("allprousername", ""+allProfilesUsername.size());
        if (allProfilesUsername.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            rl_no_history.setVisibility(View.GONE);
            Iterator<String> it = allProfilesUsername.listIterator();
            while (it.hasNext()) {
                try {
                    String userdata = it.next();
                    Log.d("Usersnamee", userdata);
                    UserModel user = new UserModel(new JSONObject(userdata), activity);
                    usersList.add(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter = new ProfilesHistoryCustomAdapter(usersList, activity);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        }
        dbHandler.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        mSearchMenu = menu.findItem(R.id.menu_search);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                searchView.onActionViewCollapsed();
                mSearchMenu.collapseActionView();
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private class Get_Github_User extends AsyncTask<Object, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(activity, "", "Loading...");
        }

        @Override
        protected String doInBackground(Object... params) {

            return confirm(params[0].toString());
        }


        @Override
        protected void onPostExecute(String result) {
            Log.d("response ", " " + result);
            JSONObject res = null;
            if (result != null) {
                try {
                    res = new JSONObject(result);
                    if (res.getString("message").equalsIgnoreCase("Not Found")) {
                        alertDailog(activity, "Not Found", "Username not available on github!!! Please try another");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DatabaseHandler dbHandler = new DatabaseHandler(activity);
                    try {
                        dbHandler.addUserProfile(res.getString("login"), result);
                        dbHandler.close();
                        for (int i = 0; i < usersList.size(); i++) {
                            if (res.getString("login").equalsIgnoreCase(usersList.get(i).getUsername())) {
                                usersList.remove(i);
                                break;
                            }
                        }
                        UserModel userModel = new UserModel(res, activity);
                        usersList.add(0, userModel);
                        if (recyclerView.getAdapter() == null) {
                            Log.d("reyclerrrrview ", ""+usersList.size());
                            recyclerView.setVisibility(View.VISIBLE);
                            rl_no_history.setVisibility(View.GONE);
                            adapter = new ProfilesHistoryCustomAdapter(usersList, activity);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(adapter);

                        } else
                            adapter.reconstructAdapter(usersList);
                        Intent intent = new Intent(activity, ProfileActivity.class);
                        intent.putExtra("login", res.getString("login"));
                        startActivity(intent);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(activity, "Something went wrong...\n Please try again", Toast.LENGTH_SHORT).show();
            }

            progressDialog.dismiss();
        }
    }

    public void alertDailog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);


        alertDialogBuilder.setTitle(title);

        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String username = intent.getStringExtra(SearchManager.QUERY);
            Log.d("query", "" + username);
            if (!CommonGlobalVariable.checkInternetConnection(activity)) {
                alertDailog(activity, getResources().getString(R.string.nointernet), getResources().getString(R.string.checkconnection));
            } else {
                new Get_Github_User().execute(username);
            }
        }

    }


    private static String confirm(String username) {
        try {
            Log.d("Urll", CommonGlobalVariable.HOST + CommonGlobalVariable.API_USERS + username);
            String json = CommonGlobalVariable.get(CommonGlobalVariable.HOST + CommonGlobalVariable.API_USERS + username);
            return json;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
            mSearchMenu.collapseActionView();
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 3000) {
                Toast.makeText(activity, "Press back again to exit", Toast.LENGTH_SHORT).show();
                lastPress = currentTime;
            } else {
                super.onBackPressed();
            }
        }
    }


}
