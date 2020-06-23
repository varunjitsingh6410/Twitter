package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
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
    ImageButton btnReply;

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
        btnReply = findViewById(R.id.btnReply);

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

        btnReply.setBackgroundResource(R.drawable.vector_compose_dm_fab);

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.reply_window, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                Button btnTweetReply = (Button) popupView.findViewById(R.id.btnTweetReply);
                final EditText etComposeReply = popupView.findViewById(R.id.etComposeReply);
                final TextView tvCharacterCountReply = (TextView) popupView.findViewById(R.id.tvCharactercountReply);

                etComposeReply.setText("@"+tweet.user.screenName);
                int tweetLength = etComposeReply.getText().toString().length();
                int charsLeft = 280 - tweetLength;
                String charsLeftMessage = Integer.toString(charsLeft) + " characters left";
                tvCharacterCountReply.setText(charsLeftMessage);


                etComposeReply.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int tweetLength = etComposeReply.getText().toString().length();
                        int charsLeft = 280 - tweetLength;
                        String charsLeftMessage = Integer.toString(charsLeft) + " characters left";
                        tvCharacterCountReply.setText(charsLeftMessage);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                btnTweetReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tweetContent =  etComposeReply.getText().toString();
                        if (tweetContent.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "Tweet can not be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (tweetContent.length() > 280)
                        {
                            Toast.makeText(getApplicationContext(), "Tweet can not be over 280 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), tweetContent, Toast.LENGTH_SHORT).show();

                        client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("POPUP", "onSuccess to publish tweet");
                                try {
                                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                                    Log.i("POPUP", "Tweet published says: " + tweet);

                                    Intent intent = new Intent();
                                    intent.putExtra("tweet", Parcels.wrap(tweet));
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("POPUP", "onFailure to publish tweet", throwable);
                            }
                        });
                    }
                });
            }
        });

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