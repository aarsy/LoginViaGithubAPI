package com.github.aarsy.loginviagithubapi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class ReposListCustomAdapter extends RecyclerView.Adapter<View_Holder_Repo> {

    private List<RepoModel> list = Collections.emptyList();
    private Activity activity;
    private String userName;

    public ReposListCustomAdapter(List<RepoModel> list, Activity activity, String userName) {
        this.list = list;
        this.activity = activity;
        this.userName=userName;
    }

    public void reconstructAdapter(List<RepoModel> reposList) {
        list = reposList;
        notifyDataSetChanged();
    }

    @Override
    public View_Holder_Repo onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_repository_layout, parent, false);
        View_Holder_Repo holder = new View_Holder_Repo(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(final View_Holder_Repo holder, int position) {
        Log.d("bind", "1");
        final RepoModel listItem = list.get(position);
        holder.repo_name.setText(listItem.getReponame());
        holder.ll_upper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, RepoActivity.class);
                intent.putExtra("login", userName);
                intent.putExtra("repoid", listItem.getRepoId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}


