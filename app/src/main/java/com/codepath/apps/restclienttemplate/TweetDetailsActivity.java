package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {

    ImageView profilePictureDetails;
    TextView tvUserNameDetails;
    TextView tvTimeStampDetails;
    TextView tvUserHandleDetails;
    TextView tvTweetBodyDetails;

    ImageButton btnFavorite;
    ImageButton btnRetweet;

    private static boolean isFavorite;

    private static boolean isRetweeted;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        profilePictureDetails = findViewById(R.id.profilePictureDetails);
        tvUserNameDetails = findViewById(R.id.tvUserNameDetails);
        tvTimeStampDetails = findViewById(R.id.tvTimeStampDetails);
        tvUserHandleDetails = findViewById(R.id.tvUserHandleDetails);
        tvTweetBodyDetails = findViewById(R.id.tvTweetBodyDetails);

        btnFavorite = findViewById(R.id.btnFavorite);
        btnRetweet = findViewById(R.id.btnRetweet);

        final Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));


        int radius = 30; // corner radius, higher value = more rounded
        Glide.with(getApplicationContext()).load(tweet.user.profileImageUrl).transform(new RoundedCorners(radius)).into(profilePictureDetails);

        tvUserNameDetails.setText(tweet.user.name);
        tvTimeStampDetails.setText(tweet.createdAt);
        tvTweetBodyDetails.setText(tweet.body);
        tvUserHandleDetails.setText("@"+tweet.user.screenName);

        isFavorite = tweet.isFavorite;
        isRetweeted = tweet.isRetweeted;

        client = TwitterApplication.getRestClient(this);

        if (isFavorite == true)
        {
            btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart);
        }
        else
        {
            btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
        }

        if (isRetweeted == true)
        {
            btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
        }
        else
        {
            btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
        }

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String createFav = "create";
                String destroyFav = "destroy";
                if (isFavorite == true)
                {
                    client.favoriteTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                            isFavorite = false;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        }
                    }, tweet.id, destroyFav);
                }
                else
                {
                    client.favoriteTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart);
                            isFavorite = true;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        }
                    }, tweet.id, createFav);
                }
            }
        });



        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRetweeted == false)
                {
                    client.retweetTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                            isRetweeted = true;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        }
                    }, tweet.id, "retweet");
                }
                else
                {
                    client.retweetTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                            isRetweeted = true;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        }
                    }, tweet.id, "unretweet");
                }
            }
        });


    }
}