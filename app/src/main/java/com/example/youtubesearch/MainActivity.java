package com.example.youtubesearch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

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
    public final static String API_KEY = "&key=" + Config.YOUTUBE_API_KEY;
    public final static String YOUTUBE_NEXT_PAGE_BASE = "&pageToken=";
    public final static String EXTRA_MESSAGE = "com.example.youtubesearch.MESSAGE";

    private String LAST_SEARCH = "";
    private String NEXT_PAGE = "";
    private String SEARCH_URL = "";

    private Boolean FLAG_LOADING = false;

    private SearchResultsAdapter mSearchResultAdapter;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchResultAdapter = new SearchResultsAdapter(this);

        spinner = (ProgressBar) findViewById(R.id.search_loader);

        ListView listView = (ListView) findViewById(R.id.result_list);
        listView.setAdapter(mSearchResultAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount >= totalItemCount - 5 && totalItemCount != 0) {

                    if (!FLAG_LOADING) {
                        FLAG_LOADING = true;
                        try {
                            searchYoutube(SEARCH_URL, NEXT_PAGE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
    }

    public void searchClicked(View view) {
        EditText searchText = (EditText) findViewById(R.id.search_text);
        String searchTextText = searchText.getText().toString();

        hideSoftKeyBoard();

        if (!LAST_SEARCH.equals(searchTextText)) {
            try {
                LAST_SEARCH = searchTextText;
                NEXT_PAGE = "";
                SEARCH_URL = "";
                mSearchResultAdapter.clear();
                FLAG_LOADING = true;
                spinner.setVisibility(View.VISIBLE);
                searchYoutube(searchTextText, "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void searchYoutube(String searchText, String nextPage) throws JSONException {
        String requestUrl;
        if (SEARCH_URL.equals("")) {
            requestUrl = YOUTUBE_SEARCH_BASE +
                    YOUTUBE_SEARCH_ORDER +
                    YOUTUBE_SEARCH_Q +
                    searchText +
                    YOUTUBE_SEARCH_TYPE +
                    YOUTUBE_MAX_RESULTS +
                    API_KEY;

            SEARCH_URL = requestUrl;
        } else {
            requestUrl = SEARCH_URL;
        }

        if (!nextPage.equals("")) {
            requestUrl += nextPage;
        }

        Log.v(TAG, "searchYoutube: " + requestUrl);

        YoutubeRestClient.get(requestUrl, null, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                JsonParser parser = new JsonParser();

                JsonObject rootResponse = parser.parse(response).getAsJsonObject();
                JsonArray items = rootResponse.getAsJsonArray("items");
                JsonElement nextPageTokenElement = rootResponse.get("nextPageToken");
                if (nextPageTokenElement != null) {
                    String nextPageToken = nextPageTokenElement.getAsString();

                    NEXT_PAGE = YOUTUBE_NEXT_PAGE_BASE + nextPageToken;
                } else {
                    NEXT_PAGE = "";
                }

                if (items.size() != 0) {
                    displayResults(items);
                } else {
                    FLAG_LOADING = false;
                    spinner.setVisibility(View.GONE);
                }

                Log.d(TAG, "onSuccess: object: " + items.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorMessage, Throwable throwable) {
                FLAG_LOADING = false;
                spinner.setVisibility(View.GONE);
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
            String videoId = result.getAsJsonObject("id").get("videoId").getAsString();
            list.add(new SearchResult(thumbnailUrl, title, subtitle, videoId));
        }

        mSearchResultAdapter.addAll(list);
        FLAG_LOADING = false;
        spinner.setVisibility(View.GONE);
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void videoClicked(View view) {
        Log.i(TAG, "videoClicked");
        String videoObject = view.getTag().toString();

        Intent videoPageIntent = new Intent(this, VideoPageActivity.class);
        videoPageIntent.putExtra(EXTRA_MESSAGE, videoObject);
        startActivity(videoPageIntent);
    }
}
