package com.takhir.redditgallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.takhir.redditgallery.adapters.ImagesRecyclerAdapter;
import com.takhir.redditgallery.model.Feed;
import com.takhir.redditgallery.models.ImageCard;
import com.takhir.redditgallery.util.VerticalSpacingItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.takhir.redditgallery.RetrieveUrl.retrieveGifUrlsAndAddToArrayList;
import static com.takhir.redditgallery.RetrieveUrl.retrieveImageUrlsAndAddToArrayList;

public class MainActivity extends AppCompatActivity implements
        ImagesRecyclerAdapter.OnLongImageListener,
        View.OnClickListener {
    public static final String TAG = "MainActivity";

    public static final String BASE_URL = "https://www.reddit.com/r/";
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;

    public static ArrayList<String> urlsList = new ArrayList<>();

    // UI components
    private RecyclerView mRecyclerView;
    private EditText mSearchSubredditFeed;
    private Button mLoadSubredditButton;
    private String currentFeed;

    // vars
    private final ArrayList<ImageCard> mImageCards = new ArrayList<>();
    private ImagesRecyclerAdapter mImagesRecyclerAdapter;
    private int mImagePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchSubredditFeed = findViewById(R.id.toolbar_search_subreddit);
        mLoadSubredditButton = findViewById(R.id.toolbar_load_subreddit);

        currentFeed = "art";
        init();
        mRecyclerView = findViewById(R.id.recyclerView);
        initRecyclerView();

        mLoadSubredditButton.setOnClickListener(this);
    }

    private void init() {
        urlsList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call;
        call = feedAPI.getFeed(currentFeed);
        enqueue(call);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select the action");
        menu.add(0, v.getId(), 0, "Share");
        menu.add(0, v.getId(), 0, "Download");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle() == "Share") {
            ImageView image = mImageCards.get(mImagePosition).getImage();

            Uri bmpUri = getLocalBitmapUri(image);
            if (bmpUri != null) {
                // Construct a ShareIntent with link to image
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/*");
                // Launch sharing dialog for image
                String chooserTitle = getString(R.string.chooser_title);
                Intent chooser = Intent.createChooser(shareIntent, chooserTitle);
                startActivity(chooser);
            } else {
                // ...sharing failed, handle error
                Log.e(TAG, "onContextItemSelected: image did not send");
            }
            return true;
        } else if (item.getTitle() == "Download") {
            ImageCard imageCard = mImageCards.get(mImagePosition);

            Glide.with(this)
                    .asBitmap()
                    .load(imageCard.getUrl())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            saveImage(resource);
                        }
                    });
            return true;
        }
        return true;
    }
    // Returns the URI path to the Bitmap displayed in specified ImageView

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    // Method when launching drawable within Glide

    public Uri getBitmapFromDrawable(Bitmap bmp) {

        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();

            // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
            bmpUri = FileProvider.getUriForFile(this, "com.codepath.fileprovider", file);  // use this version for API >= 24

            // **Note:** For API < 24, you may use bmpUri = Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
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

                mRecyclerView = findViewById(R.id.recyclerView);
                initRecyclerView();

                System.out.println(urlsList);

                setImages(urlsList);
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
            ImageCard imageCard = new ImageCard(url);
            mImageCards.add(imageCard);
            mImagesRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecoration itemDecoration = new VerticalSpacingItemDecoration(0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mImagesRecyclerAdapter = new ImagesRecyclerAdapter(mImageCards, this);
        mRecyclerView.setAdapter(mImagesRecyclerAdapter);
        registerForContextMenu(mRecyclerView);
    }

    @Override
    public void onLongImageClick(int position) {
        mImagePosition = position;
    }

    private String saveImage(Bitmap image) {
        String savedImagePath = null;

        String imageFileName = "RG_" + System.currentTimeMillis() + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/Reddit Gallery");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            galleryAddPic(savedImagePath);
            Toast.makeText(this, "IMAGE SAVED", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onClick(View view) {
        String searchSubredditFeedName = mSearchSubredditFeed.getText().toString();
        if (!searchSubredditFeedName.equals("") && !searchSubredditFeedName.equals(currentFeed)) {
            currentFeed = searchSubredditFeedName;
            init();
        }
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        private String imageURL;

        public MyAsyncTask(String imageURL) {
            this.imageURL = imageURL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(imageURL);
                //create the new connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //set up some things on the connection
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                //and connect!
                urlConnection.connect();
                //set the path where we want to save the file in this case, going to save it on the root directory of the sd card.
                File SDCardRoot = Environment.getExternalStorageDirectory();
                //create a new file, specifying the path, and the filename which we want to save the file as.
                File file = new File(SDCardRoot, "image" + System.currentTimeMillis() + ".jpg");
                //this will be used to write the downloaded data into the file we created
                FileOutputStream fileOutput = new FileOutputStream(file);
                //this will be used in reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();
                //this is the total size of the file
                int totalSize = urlConnection.getContentLength();
                //variable to store total downloaded bytes
                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer
                //now, read through the input buffer and write the contents to the file
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    //this is where you would do something to report the progress, like this maybe
                    //updateProgress(downloadedSize, totalSize);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: image downloaded");
        }
    }

}



