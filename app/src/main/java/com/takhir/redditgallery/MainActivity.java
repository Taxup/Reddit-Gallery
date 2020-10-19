package com.takhir.redditgallery;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.takhir.redditgallery.adapters.ImagesRecyclerAdapter;
import com.takhir.redditgallery.model.Feed;
import com.takhir.redditgallery.models.ImageCard;
import com.takhir.redditgallery.util.VerticalSpacingItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import java.util.ArrayList;
import java.util.Collections;

import static com.takhir.redditgallery.RetrieveUrl.retrieveGifUrlsAndAddToArrayList;
import static com.takhir.redditgallery.RetrieveUrl.retrieveImageUrlsAndAddToArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public static final String BASE_URL = "https://www.reddit.com/r/";

    public static ArrayList<String> urlsList = new ArrayList<>();
    public static int indexWhenShowImages = 0;

    // UI components
    private RecyclerView mRecyclerView;

    // vars
    private final ArrayList<ImageCard> mImageCards = new ArrayList<>();
    private ImagesRecyclerAdapter mImagesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call, call2;
        call = feedAPI.getFeed();
        enqueue(call);
        call2 = feedAPI.getFeed2();
        enqueue(call2);
    }

    private void enqueue(Call<Feed> call) {

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                assert response.body() != null;
                Log.e(TAG, "onResponse: feed: " + response.body().toString());
                Log.e(TAG, "onResponse: Server Response: " + response.toString());

                String[] entries = response.body().toString().split("splitter");

                retrieveImageUrlsAndAddToArrayList(entries);
                retrieveGifUrlsAndAddToArrayList(entries);

                indexWhenShowImages++;

                if (indexWhenShowImages == 2) {

                    mRecyclerView = findViewById(R.id.recyclerView);
                    initRecyclerView();

                    Collections.shuffle(urlsList);
                    System.out.println(urlsList);

                    setImages(urlsList);
                }
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: unable to retrieve rss: " + t.getMessage());
                Toast.makeText(MainActivity.this, "An error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setImages(ArrayList<String> urls) {

        for (String url : urls) {
            ImageView imageView = new ImageView(this);

            ImageCard imageCard = new ImageCard(url, imageView);
            mImageCards.add(imageCard);
            mImagesRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecoration itemDecoration = new VerticalSpacingItemDecoration(0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mImagesRecyclerAdapter = new ImagesRecyclerAdapter(mImageCards);
        mRecyclerView.setAdapter(mImagesRecyclerAdapter);
    }

}



