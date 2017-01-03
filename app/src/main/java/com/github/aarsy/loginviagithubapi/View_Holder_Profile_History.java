package com.github.aarsy.loginviagithubapi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by abhay yadav on 04-Dec-16.
 */
public class View_Holder_Profile_History extends RecyclerView.ViewHolder {
    ImageView user_image;
    TextView user_name;
    LinearLayout ll_upper;

    View_Holder_Profile_History(View itemView) {
        super(itemView);
        user_image = (ImageView) itemView.findViewById(R.id.image);
        user_name = (TextView) itemView.findViewById(R.id.txtname);
        ll_upper= (LinearLayout) itemView.findViewById(R.id.ll_upper);

    }
}