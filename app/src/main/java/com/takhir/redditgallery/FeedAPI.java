package com.takhir.redditgallery;

import com.takhir.redditgallery.model.Feed;
import retrofit2.Call;
import retrofit2.http.GET;

public interface FeedAPI {

    @GET("art/.rss")
    Call<Feed> getFeed();

    @GET("abstract/.rss")
    Call<Feed> getFeed2();
}
