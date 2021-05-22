package com.example.android.newsapp.utils;

public class Constants {

    /**
     * URL for news data from the guardian data set
     */
    public static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search";
    /**
     * Query Uri builder
     */
    public static final String QUERY_PARAM = "q";
    public static final String ORDER_BY_PARAM = "order-by";
    public static final String PAGE_SIZE_PARAM = "page-size";
    public static final String ORDER_DATE_PARAM = "order-date";
    public static final String FROM_DATE_PARAM = "from-date";
    public static final String SHOW_FIELDS_PARAM = "show-fields";
    public static final String FORMAT_PARAM = "format";
    public static final String SHOW_TAGS_PARAM = "show-tags";
    public static final String API_KEY_PARAM = "api-key";
    public static final String SECTION_PARAM = "section";
    /**
     * The show fields we want our API to return
     */
    public static final String SHOW_FIELDS = "trailText";
    /**
     * The format we want our API to return
     */
    public static final String FORMAT = "json";
    /**
     * The show tags we want our API to return
     */
    public static final String SHOW_TAGS = "contributor";
    /**
     * API Key
     */
    public static final String API_KEY = "abf8fdd5-a152-4471-935d-ea03cbc2a30b";
    /**
     * Default number to set the image on the top of the textView
     */
    public static final int DEFAULT_NUMBER = 0;
    /**
     * HTTP request parameters
     */
    static final int READ_TIMEOUT = 10000; /* milliseconds */
    static final int CONNECT_TIMEOUT = 15000; /* milliseconds */
    static final int SUCCESS_RESPONSE_CODE = 200;
    static final String REQUEST_METHOD_GET = "GET";
    /**
     * Extract the key associated with the JSONObject
     */
    static final String JSON_KEY_RESPONSE = "response";
    static final String JSON_KEY_RESULTS = "results";
    static final String JSON_KEY_WEB_TITLE = "webTitle";
    static final String JSON_KEY_AUTHOR = "webTitle";
    static final String JSON_KEY_SECTION_NAME = "sectionName";
    static final String JSON_KEY_WEB_PUBLICATION_DATE = "webPublicationDate";
    static final String JSON_KEY_WEB_URL = "webUrl";
    static final String JSON_KEY_TAGS = "tags";
    static final String JSON_KEY_FIELDS = "fields";

    /**
     * Private constructor because no one should ever create a {@link Constants} object.
     */
    private Constants() {
    }


}
