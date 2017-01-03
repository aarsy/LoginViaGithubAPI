package com.github.aarsy.loginviagithubapi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.aarsy.loginviagithubapi.common.PicassoCircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class ProfilesHistoryCustomAdapter extends RecyclerView.Adapter<View_Holder_Profile_History> {

    private List<UserModel> list = Collections.emptyList();
    private Activity activity;

    public ProfilesHistoryCustomAdapter(List<UserModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;

    }
    public void reconstructAdapter(List<UserModel> usersList) {
        list = usersList;
        notifyDataSetChanged();
    }

    @Override
    public View_Holder_Profile_History onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("createview", "1");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_history, parent, false);
        View_Holder_Profile_History holder = new View_Holder_Profile_History(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(final View_Holder_Profile_History holder, int position) {
        Log.d("bind", "1");
        final UserModel listItem = list.get(position);
        Picasso.with(activity).load(listItem.getAvatar_url()).transform(new PicassoCircularImageView()).into(holder.user_image);
        holder.user_name.setText(listItem.getName());
        holder.ll_upper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("login", listItem.getUsername());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("count", "1" + list.size());
        return list.size();

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}


