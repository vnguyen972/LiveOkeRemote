package com.vnguyen.liveokeremote.youtube;

import android.content.Context;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.helper.LogHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vu Nguyen on 12/7/2015.
 */
public class YTConnector {
    private YouTube youtube;
    public YouTube.Search.List query;
    public int maxResults = 50;
    public String nextPageToken = null;
    public String prevPageToken = null;
    // youtube API only allows 500 videos to return in all cases
    public int totalResults = 500;

    // Your developer key goes here
    public static final String KEY
            = "AIzaSyC2zsBjsEg3pTcE9Ltjqi1mXm-qOYA3d_c";

    public YTConnector(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {}
        }).setApplicationName(context.getResources().getString(R.string.app_name)).build();

        try{
            query = youtube.search().list("id,snippet");
            query.setKey(KEY);
            query.setType("video");
            //query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            query.setMaxResults(new Long(maxResults));
        }catch(Exception e){
            LogHelper.e("YTConnector: ",e);
        }
    }

    public List<YTVideoItem> search(String keywords){
        query.setQ(keywords);
        try{
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();
            nextPageToken = response.getNextPageToken();
            prevPageToken = response.getPrevPageToken();
            LogHelper.i("Next Page Token = " + nextPageToken);
            LogHelper.i("Previous Page Token = " + prevPageToken);
            //totalResults = response.getPageInfo().getTotalResults().intValue();
            LogHelper.i("Total Results: " + totalResults);
            List<YTVideoItem> items = new ArrayList<YTVideoItem>();
            if (results != null) {
                for (SearchResult result : results) {
                    YTVideoItem item = new YTVideoItem();
                    if (result.getSnippet() != null) {
                        item.setTitle(result.getSnippet().getTitle());
                        item.setDescription(result.getSnippet().getDescription());
                        item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                    }
                    item.setId(result.getId().getVideoId());
                    items.add(item);
                }
            }
            return items;
        }catch(Exception e){
            LogHelper.e("YTConnector: Could not search!",e);
            return null;
        }
    }
}
