package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    ImageView profilePictureDetails;
    TextView tvUserNameDetails;
    TextView tvTimeStampDetails;
    TextView tvUserHandleDetails;
    TextView tvTweetBodyDetails;

    ImageButton btnFavorite;
    ImageButton btnRetweet;

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

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));


        int radius = 30; // corner radius, higher value = more rounded
        Glide.with(getApplicationContext()).load(tweet.user.profileImageUrl).transform(new RoundedCorners(radius)).into(profilePictureDetails);

        tvUserNameDetails.setText(tweet.user.name);
        tvTimeStampDetails.setText(tweet.createdAt);
        tvTweetBodyDetails.setText(tweet.body);
        tvUserHandleDetails.setText("@"+tweet.user.screenName);
    }
}