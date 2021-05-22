package com.example.android.newsapp;


import java.util.Objects;

public class News {

    private String sectionName;
    private String webTitle;
    private String url;

    public News(String sectionName, String webTitle, String url) {

        this.sectionName = sectionName;
        this.webTitle = webTitle;
        this.url = url;
    }


    public String getSectionName() {
        return sectionName;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getUrl() {
        return url;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return Objects.equals(sectionName, news.sectionName) &&
                Objects.equals(webTitle, news.webTitle) &&
                Objects.equals(url, news.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionName, webTitle, url);
    }

    @Override
    public String toString() {
        return "News{" +
                "sectionName='" + sectionName + '\'' +
                ", webTitle='" + webTitle + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}