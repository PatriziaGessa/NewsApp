package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * New {@link NewsAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param object  is the list of {@link News} to be displayed.
     */
    public NewsAdapter(Context context, ArrayList<News> object) {
        super(context, 0, object);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // ViewHolder object.
        ViewHolder holder;

        // Check if the existing view is being reused, otherwise inflate the view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.sectionTextView = convertView.findViewById(R.id.sectionTextView);
            holder.titleTextView = convertView.findViewById(R.id.titleTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the News object located at this position in the list.
        News currentNews = getItem(position);


        // Set proper data in list_item by using ViewHolder.
        holder.sectionTextView.setText(currentNews.getSectionName());
        holder.titleTextView.setText(currentNews.getWebTitle());


        return convertView;
    }

    /**
     * This ViewHolder class is help to represent data in ListView by recycling.
     */
    static class ViewHolder {

        private TextView sectionTextView;
        private TextView titleTextView;

    }


}
