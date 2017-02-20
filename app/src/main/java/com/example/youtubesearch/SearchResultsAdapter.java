package com.example.youtubesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.ArrayList;

public class SearchResultsAdapter extends ArrayAdapter<SearchResult> {

    private Context mContext;

    public SearchResultsAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    public SearchResultsAdapter(Context context, ArrayList<SearchResult> searchResults) {
        super(context, 0, searchResults);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchResult searchResult = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_item, parent, false);
        }

        convertView.setTag(searchResult.videoId);

        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView subTitle = (TextView) convertView.findViewById(R.id.subtitle);

        title.setText(searchResult.title);
        subTitle.setText(searchResult.subTitle);

        Glide.with(mContext).load(searchResult.thumbnailUrl).into(thumbnail);

        return convertView;
    }
}
