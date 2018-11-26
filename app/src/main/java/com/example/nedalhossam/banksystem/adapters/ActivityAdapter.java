package com.example.nedalhossam.banksystem.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nedalhossam.banksystem.R;
import com.example.nedalhossam.banksystem.model.UserActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Nedal Hossam on 10/04/2018.
 */

public class ActivityAdapter extends ArrayAdapter<UserActivity> {

    public ActivityAdapter(Activity context, List<UserActivity> activities) {
        super(context, 0, activities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_item, parent, false);
        }
        UserActivity currentUserActivity = getItem(position);
        Log.e("currentUs", currentUserActivity.getType());
        ImageView typeimageView = (ImageView) listItemView.findViewById(R.id.type_image);
        if (currentUserActivity.getType().contains("transfer")) {
            typeimageView.setImageResource(R.drawable.transfer);
        } else if (currentUserActivity.getType().contains("deposit")) {
            typeimageView.setImageResource(R.drawable.ic_input_add);
        } else {
            typeimageView.setImageResource(R.drawable.presence_busy);
        }

        TextView value = (TextView) listItemView.findViewById(R.id.value);
        value.setText("$"+currentUserActivity.getValue() + "");


        TextView descriptionTextView = (TextView) listItemView.findViewById(R.id.desc);
        String description = "";
        if (currentUserActivity.getType().contains("transfer") && TextUtils.isEmpty(currentUserActivity.getTo())) {
            description = "Transfer from " + currentUserActivity.getFrom();

        } else if (currentUserActivity.getType().contains("transfer") && TextUtils.isEmpty(currentUserActivity.getFrom())) {
            description = "Transfer to " + currentUserActivity.getTo();
        } else if (currentUserActivity.getType().contains("deposit")) {
            description = "Deposited to your balance";
        } else {
            description = "Withdraw from your balance";
        }
        descriptionTextView.setText(description);

        Date dateObject = new Date(Long.parseLong(currentUserActivity.getTimeInMilliseconds()));
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        String formattedDate = formatDate(dateObject);
        dateView.setText(formattedDate);
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        String formattedTime = formatTime(dateObject);
        timeView.setText(formattedTime);

        return listItemView;
    }


    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}

