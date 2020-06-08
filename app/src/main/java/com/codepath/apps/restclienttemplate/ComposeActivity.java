package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ComposeActivity extends AppCompatActivity {

    EditText etCompose;
    Button btnTweet;

    public static final int MAX_TWEET_LENGTH = 240;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent =  etCompose.getText().toString();
                if (tweetContent.isEmpty())
                {
                    Toast.makeText(ComposeActivity.this, "Tweet can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (tweetContent.length() > MAX_TWEET_LENGTH)
                {
                    Toast.makeText(ComposeActivity.this, "Tweet can not be over 240 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
