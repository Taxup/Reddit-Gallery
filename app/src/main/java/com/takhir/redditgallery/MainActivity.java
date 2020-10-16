package com.takhir.redditgallery;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.takhir.redditgallery.model.Feed;
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
    public LinearLayout myLayout;

    public static ArrayList<String> urlsList = new ArrayList<>();
    public static int indexWhenShowImages = 0;

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

    public void enqueue(Call<Feed> call) {

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.e(TAG, "onResponse: feed: " + response.body().toString());
                Log.e(TAG, "onResponse: Server Response: " + response.toString());

                String[] entries = response.body().toString().split("splitter");

                retrieveImageUrlsAndAddToArrayList(entries);
                retrieveGifUrlsAndAddToArrayList(entries);

                System.out.println("ffffffffffffffff    " + call.request());

                indexWhenShowImages++;

                if (indexWhenShowImages == 2) {

                    Toast.makeText(MainActivity.this, String.valueOf(urlsList.size()), Toast.LENGTH_LONG).show();
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

    void setImages(ArrayList<String> urls) {
        myLayout = findViewById(R.id.linearLayout);

        for (String url : urls) {
            ImageView imageView = new ImageView(MainActivity.this);

            myLayout.addView(imageView);

            Picasso.get().load(url)
                    .resize(1080, 0)
                    .into(imageView);

        }
    }
}



