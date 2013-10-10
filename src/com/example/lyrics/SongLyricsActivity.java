package com.example.lyrics;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SongLyricsActivity extends Activity {

	public static final String TITLE_KEY = "Title";
	public static final String URL_KEY = "Url";
	private TextView lyricsTextView;
	private TextView youtubeTextView;
	private TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_lyrics);
		lyricsTextView = (TextView) findViewById(R.id.SongLyricsTextView);
		youtubeTextView = (TextView) findViewById(R.id.SongYoutubeTextView);
		titleTextView = (TextView) findViewById(R.id.SongTitleTextView);
		Intent i = this.getIntent();
		String title = i.getStringExtra(TITLE_KEY);
		String url = i.getStringExtra(URL_KEY);
		titleTextView.setText(title);
		GetLyricsTask glt = new GetLyricsTask();
		glt.execute(url);
	}

	private class GetLyricsTask extends AsyncTask<String, Void, String> {

		String youtubeLink;
		@Override
		protected String doInBackground(String... urls) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(urls[0]);
				HttpResponse response = client.execute(request);
				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				String httpResponseVal = sb.toString();
				Document doc = Jsoup.parse(httpResponseVal);
				
				Elements youtube = doc.select("param[name=movie]");
				if(youtube.size()==1){
					Element youtubeDiv = youtube.get(0);
					String src =youtubeDiv.attr("value");
					youtubeLink=src;
					Log.d("SOURCE",src);
				}
				
				Elements lyrics = doc.select("#songlyrics");
				if(lyrics.size()==1){
					Element lyricsDiv = lyrics.get(0);
					return lyricsDiv.html().replaceAll("<br[ ]*/*>[ ]*", "");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			lyricsTextView.setText(result);
			if(youtubeLink!=null){
				youtubeTextView.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(youtubeLink)));
						
					}
					
				});
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.song_lyrics, menu);
		return true;
	}

}
