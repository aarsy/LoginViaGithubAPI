package com.github.aarsy.loginviagithubapi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class View_Holder_Repo extends RecyclerView.ViewHolder {
    TextView repo_name;
    LinearLayout ll_upper;

    View_Holder_Repo(View itemView) {
        super(itemView);
        repo_name= (TextView) itemView.findViewById(R.id.reponame);
        ll_upper= (LinearLayout) itemView.findViewById(R.id.ll_upper);
    }
}