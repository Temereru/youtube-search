package com.example.youtubesearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.*;
import org.json.*;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";
    public final static String YOUTUBE_SEARCH_BASE = "search?part=snippet";
    public final static String YOUTUBE_SEARCH_ORDER = "&order=rating";
    public final static String YOUTUBE_SEARCH_Q = "&q=";
    public final static String YOUTUBE_SEARCH_TYPE = "&type=video";
    public final static String YOUTUBE_MAX_RESULTS = "&maxResults=10";
    public final static String API_KEY = "&key=AIzaSyCWWvMYqP3iLAH5mV_efCBRU4y0FVIYapQ";
    public final static String YOUTUBE_NEXT_PAGE_BASE = "&pageToken=";

    private String NEXT_PAGE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void searchClicked(View view) {
        EditText searchText = (EditText) findViewById(R.id.search_text);

        try {
            searchYoutube(searchText.getText().toString());
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

    }

    public void searchYoutube(String searchText) throws JSONException {
        String requestUrl = YOUTUBE_SEARCH_BASE +
                            YOUTUBE_SEARCH_ORDER +
                            YOUTUBE_SEARCH_Q +
                            searchText +
                            YOUTUBE_SEARCH_TYPE +
                            YOUTUBE_MAX_RESULTS +
                            API_KEY;

        Log.v(TAG, "searchYoutube: " + requestUrl);

        YoutubeRestClient.get(requestUrl, null, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                JsonParser parser = new JsonParser();

                JsonObject rootResponse = parser.parse(response).getAsJsonObject();
                JsonArray items = rootResponse.getAsJsonArray("items");

                if (items.size() != 0) {
                    displayResults(items);
                }

                Log.d(TAG, "onSuccess: object: " + items.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorMessage, Throwable throwable) {
                Log.e(TAG, "onFailure " + statusCode + " " + errorMessage, throwable);
            }
        });
    }

    private void displayResults(JsonArray results) {
        ArrayList<SearchResult> list = new ArrayList<SearchResult>();

        int len = results.size();
        for (int i = 0; i < len; i++) {
            JsonObject result = results.get(i).getAsJsonObject();
            JsonObject snippet = result.getAsJsonObject("snippet");
            String title = snippet.get("title").getAsString();
            String subtitle = snippet.get("description").getAsString();
            String thumbnailUrl = snippet.getAsJsonObject("thumbnails").getAsJsonObject("default").get("url").getAsString();
            list.add(new SearchResult(thumbnailUrl, title, subtitle));
        }

        SearchResultsAdapter adapter = new SearchResultsAdapter(this, list);

        ListView listView = (ListView) findViewById(R.id.result_list);
        listView.setAdapter(adapter);
    }

}
