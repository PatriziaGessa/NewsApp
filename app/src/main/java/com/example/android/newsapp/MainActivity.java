package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.newsapp.utils.Constants;
import com.example.android.newsapp.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    /**
     * URL for earthquake data from The Guardian API
     */
    private static final String GUARDIAN_REQUEST_URL = Constants.NEWS_REQUEST_URL;
    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * Adapter for the list of news
     */
    private NewsAdapter adapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView emptyStateTextView;


    @Override
    protected void onResume() {
        super.onResume();
        // Listener for Shared Preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.OnSharedPreferenceChangeListener prefListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                    }
                };
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsListView = findViewById(R.id.list);

        emptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);

        adapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(adapter);


        // Start the AsyncTask to fetch the news data
        NewsAsyncTask task = new NewsAsyncTask();
        task.execute(GUARDIAN_REQUEST_URL);

        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // preferences.registerOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                News currentNews = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInto = connectivityManager.getActiveNetworkInfo();

        if (netInto != null && netInto.isConnected()) {
            LoaderManager manager = getLoaderManager();

            manager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
        }

        emptyStateTextView.setText(R.string.no_internet_connection);


    }


    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences
        // the second parameter is the default value for this preference.
        String numbOfItems = sharedPrefs.getString(
                getString(R.string.settings_number_of_items_key),
                getString(R.string.settings_number_of_items_default));


        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));


        // Parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=json`
        uriBuilder.appendQueryParameter(Constants.QUERY_PARAM, "");
        uriBuilder.appendQueryParameter(Constants.API_KEY_PARAM, Constants.API_KEY);
        uriBuilder.appendQueryParameter(Constants.ORDER_BY_PARAM, orderBy);
        uriBuilder.appendQueryParameter(Constants.PAGE_SIZE_PARAM, numbOfItems);
        uriBuilder.appendQueryParameter(Constants.FORMAT_PARAM, Constants.FORMAT);
        // uriBuilder.appendQueryParameter(Constants.QUERY_PARAM, keyword);
        // uriBuilder.appendQueryParameter("page", "1");
        //uriBuilder.appendQueryParameter(Constants.PAGE_SIZE_PARAM, "20");
        //uriBuilder.appendQueryParameter(Constants.ORDER_BY_PARAM, "newest");
        //uriBuilder.appendQueryParameter("show-tags", "contributor");

        // Return the completed uri
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Set empty state text to display "No news found."
            emptyStateTextView.setText(R.string.no_news);
            emptyStateTextView.setGravity(Gravity.CENTER);
        } else {
            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Clear the adapter of previous news data
        adapter.clear();

        // If there is a valid list of {@link News}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            adapter.addAll(news);
        }
    }


    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class NewsAsyncTask extends AsyncTask<String, Void, List<News>> {

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link News} as the result.
         */
        @Override
        protected List<News> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            return QueryUtils.fetchNewsData(urls[0]);
        }

        @Override
        protected void onPostExecute(List<News> data) {
            // Clear the adapter of previous news data
            adapter.clear();

            // If there is a valid list of {@link News}, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                adapter.addAll(data);
            }
        }
    }

}