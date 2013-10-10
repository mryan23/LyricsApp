package com.example.lyrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.lyrics.model.SearchResultsRow;

public class MainSearchActivity extends Activity {

	private Spinner typeSpinner;
	private EditText queryEditText;
	private Button searchButton;
	private TableLayout searchResultsTableLayout;

	public static final int TITLE = 1;
	public static final int ARTIST = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_search);
		typeSpinner = (Spinner) findViewById(R.id.searchTypeSpinner);
		queryEditText = (EditText) findViewById(R.id.searchQueryEditText);
		searchButton = (Button) findViewById(R.id.searchButton);
		searchResultsTableLayout = (TableLayout) findViewById(R.id.searchResultsTable);

		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String query = queryEditText.getText().toString();
				int type = typeSpinner.getSelectedItemPosition() + 1;
				Log.d("ONCLICK", query + " "
						+ ((type == 1) ? "Title" : "Artist"));
				SearchTask st = new SearchTask(type);

				st.execute("http://www.seeklyrics.com/search.php?q="
						+ query.replace(' ', '+') + "&t=" + type);

			}

		});
	}

	private class SearchTask extends AsyncTask<String, Void, String> {

		int type;

		public SearchTask(int type) {
			this.type = type;
		}

		@Override
		protected void onPreExecute() {
			searchResultsTableLayout.removeAllViews();
		}

		ArrayList<SearchResultsRow> resultRows = new ArrayList<SearchResultsRow>();

		@Override
		protected String doInBackground(String... urls) {
			Document doc;
			try {
				Log.d("Name", urls[0]);
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
				doc = Jsoup.parse(httpResponseVal);
				Elements rows;
				if (type == TITLE) {
					rows = doc.select("table[title=Search Results] tbody tr");
				}else
				{
					rows = doc.select("table[summary=Search Results] tbody tr");
				}
				Log.d("SIZE", rows.size() + "");
				for (int i = 0; i < rows.size(); i++) {
					Element row = rows.get(i);
					Elements links = row.select("td a");
					if (type == TITLE && links.size() == 2) {
						String title = links.get(0).text();
						String titleLink = links.get(0).attr("href");
						String artist = links.get(1).text();
						String artistLink = links.get(1).attr("href");
						resultRows.add(new SearchResultsRow(title, artist,
								titleLink, artistLink));
					} else if (links.size() == 1) {
						String artist = links.get(0).text();
						String artistLink = links.get(0).attr("href");
						resultRows.add(new SearchResultsRow("", artist, "",
								artistLink));
						Log.d("ARTIST", artist);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			for (SearchResultsRow s : resultRows) {
				TableRow row = new TableRow(getApplicationContext());
				TextView title = new TextView(getApplicationContext());
				if (s.getTitle().length() < 25)
					title.setText(s.getTitle());
				else
					title.setText(s.getTitle().substring(0, 22) + "...");
				title.setOnClickListener(new TextViewListener(TITLE,
						"http://www.seeklyrics.com" + s.getTitleLink()));
				title.setTextColor(getResources().getColor(R.color.link_color));
				row.addView(title);
				TextView artist = new TextView(getApplicationContext());
				artist.setText(s.getArtist());
				artist.setTextColor(getResources().getColor(R.color.link_color));
				artist.setOnClickListener(new TextViewListener(ARTIST,
						"http://www.seeklyrics.com" + s.getArtistLink()));
				row.addView(artist);
				searchResultsTableLayout.addView(row);
			}

		}

	}

	private class TextViewListener implements OnClickListener {

		String link;
		int type;

		public TextViewListener(int type, String link) {
			this.link = link;
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			Log.d("CLICK", link);
			TextView tv = (TextView) v;
			Intent i;
			if (type == TITLE) {
				i = new Intent(getApplicationContext(),
						SongLyricsActivity.class);
			} else {
				i = new Intent(getApplicationContext(), ArtistTabActivity.class);
			}
			i.putExtra(SongLyricsActivity.TITLE_KEY, tv.getText().toString());
			i.putExtra(SongLyricsActivity.URL_KEY, link);
			startActivity(i);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_search, menu);
		return true;
	}

}
