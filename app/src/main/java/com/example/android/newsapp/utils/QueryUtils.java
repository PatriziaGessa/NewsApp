package com.example.android.newsapp.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.newsapp.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Private constructor because no one should ever create a {@link QueryUtils} object.
     */
    private QueryUtils() {
    }


    /**
     * Query the USGS dataset and return a list of {@link com.example.android.newsapp.News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }


        // Return the list of {@link News}
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * @param requestUrl is the given string url.
     * @return new URL object from the given request URL.
     */
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL.", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(Constants.READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(Constants.CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod(Constants.REQUEST_METHOD_GET);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == Constants.SUCCESS_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String json) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(json)) {
            return Collections.emptyList();
        }
        try {
            return doParse(json);
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
            return Collections.emptyList();

        }


    }

    private static List<News> doParse(String json) throws JSONException {
        JSONObject baseJSON = new JSONObject(json);
        JSONObject responseJSON = baseJSON.getJSONObject(Constants.JSON_KEY_RESPONSE);
        return parseNews(responseJSON);

    }

    private static List<News> parseNews(JSONObject responseJSON) throws JSONException {
        JSONArray resultArray = responseJSON.getJSONArray(Constants.JSON_KEY_RESULTS);
        List<News> myNews = new ArrayList<>();
        for (int index = 0; index < resultArray.length(); index++) {
            JSONObject currentNews = resultArray.getJSONObject(index);
            // Extract the value for the key called "sectionName"
            String sectionName = currentNews.getString(Constants.JSON_KEY_SECTION_NAME);

            // Extract the value for the author name
            //  JSONArray tagsArray = currentNews.getJSONArray("tags");
            // JSONObject currentTagsObject = tagsArray.getJSONObject(0);
            // String authorName = currentTagsObject.getString("webTitle");

            // Extract the value for the key called "webTitle"
            String webTitle = currentNews.getString(Constants.JSON_KEY_WEB_TITLE);

            // Extract the value for the key called "webUrl"
            String webUrl = currentNews.getString(Constants.JSON_KEY_WEB_URL);


            myNews.add(new News(sectionName, webTitle, webUrl));


        }
        return myNews;

    }
}