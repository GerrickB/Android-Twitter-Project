package com.allmycode.a13_08;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {
  TextView textViewCountChars, textViewTimeline;
  EditText editTextTweet, editTextUsername;
  Twitter twitter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    editTextTweet = (EditText) findViewById(R.id.editTextTweet);
    editTextTweet.addTextChangedListener(new MyTextWatcher());
    textViewCountChars = (TextView) findViewById(R.id.textViewCountChars);
    editTextUsername = (EditText) findViewById(R.id.editTextUsername);
    textViewTimeline = (TextView) findViewById(R.id.textViewTimeline);
    textViewTimeline.setMovementMethod(new ScrollingMovementMethod());
    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder
        .setOAuthConsumerKey("0000000000000000000000000")
        .setOAuthConsumerSecret("111111111111111111111111111111111111111111")
        .setOAuthAccessToken("222222222-33333333333333333333333333333333")
        .setOAuthAccessTokenSecret("4444444444444444444444444444444444444");
    TwitterFactory factory = new TwitterFactory(builder.build());
    twitter = factory.getInstance();
  }

  // Button click listeners

  public void onTweetButtonClick(View view) {
    new MyAsyncTaskTweet().execute(editTextTweet.getText().toString());
  }

  public void onTimelineButtonClick(View view) {
    new MyAsyncTaskTimeline().execute(editTextUsername.getText().toString());
  }

  // Count characters in the Tweet field

  class MyTextWatcher implements TextWatcher {
    @Override
    public void afterTextChanged(Editable s) {
      textViewCountChars.setText("" + editTextTweet.getText().length());
    }

    @Override
    public void beforeTextChanged
        (CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged
        (CharSequence s, int start, int before, int count) {
    }
  }

  // The AsyncTask classes

  public class MyAsyncTaskTweet extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... tweet) {
      String result = "";
      try {
        twitter.updateStatus(tweet[0]);
        result = getResources().getString(R.string.success);
      } catch (TwitterException twitterException) {
        result = getResources().getString(R.string.twitter_failure);
      } catch (Exception e) {
        result = getResources().getString(R.string.general_failure);
      }
      return result;
    }

    @Override
    protected void onPostExecute(String result) {
      editTextTweet.setHint(result);
      editTextTweet.setText("");
    }
  }

  public class MyAsyncTaskTimeline extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... username) {
      String result = new String("");
      try {
        result = getResult(username);
      } catch (TwitterException twitterException) {
        result = getResources().getString(R.string.twitter_failure);
      }
      return result;
    }

    String getResult(String... username) throws TwitterException {
      String result = new String("");
      List<twitter4j.Status> statuses = null;

      statuses = getStatuses(username);

      for (twitter4j.Status status : statuses) {
        result += status.getText();
        result += "\n";
      }
      return result;
    }

    List<twitter4j.Status> getStatuses(String[] username)
                                                   throws TwitterException {
      List<twitter4j.Status> statuses;
      statuses = twitter.getUserTimeline(username[0]);
      return statuses;
    }

    @Override
    protected void onPostExecute(String result) {
      editTextUsername.setText("");
      textViewTimeline.setText(result);
    }
  }
}
