package com.miniptech.nerdapp.awesomequotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miniptech.nerdapp.awesomequotes.quote.Quote;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<Quote> quotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar app_default_toolbar = (Toolbar) findViewById(R.id.app_default_toolbar);
        setSupportActionBar(app_default_toolbar);

        ListView listView = (ListView) findViewById(R.id.quoteListView);

        registerForContextMenu(listView);

        receiveQuotes();

        ArrayAdapter<Quote> adapter = new CustomArrayAdapter(this, quotes);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.quoteListView) {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.share_menu, menu);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo;
        menuInfo = item.getMenuInfo();
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        String quote = quotes.get(position).toString();
        switch (item.getItemId()) {
            case R.id.action_copy_quote:
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Quote Text", quote);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Quote copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            case R.id.action_share_quote:
                shareQuote(quote);
        }
        return super.onContextItemSelected(item);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    public void refresh_quotes(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                get_new_quotes();
        }
    }


    private void receiveQuotes() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            this.quotes = (ArrayList) bundle.get("QUOTES");
        } else {
            this.quotes = new ArrayList<>();
        }
    }


    public void shareQuote(String quote) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Awesome quote");
        shareIntent.putExtra(Intent.EXTRA_TEXT, quote);
        shareIntent.setType("text/plain");
        try {
            startActivity(Intent.createChooser(shareIntent, "Share quote via"));
        } catch (android.content.ActivityNotFoundException ex) {
            // (handle error)
        }
    }

    public void get_new_quotes() {

        final String QUOTES_URL = "http://quotes4all.net/rss/040110110/quotes.xml";

        ListView currLV = (ListView) findViewById(R.id.quoteListView);

        ArrayList<Quote> values = new ArrayList<>();
        values.clear();

        ArrayAdapter<Quote> empty_adapter = new CustomArrayAdapter(this, values);
        empty_adapter.clear();
        currLV.setAdapter(empty_adapter);
        empty_adapter.notifyDataSetChanged();
        Log.d("QUOTE DATA", quotes.get(0).toString());

        this.quotes = null;

        //TODO Quotes data is not populated in below line, check  why ?
        new DownloadManager().execute(QUOTES_URL);

        Log.d("QUOTE DATA", quotes.get(0).toString());

        ArrayAdapter<Quote> adapter = new CustomArrayAdapter(this, quotes);
        registerForContextMenu(currLV);
        currLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "New Quotes Received" + quotes.size(), Toast.LENGTH_LONG).show();

    }

    private List<Quote> downloadUrl(String urlParam) throws IOException, XmlPullParserException {
        InputStream is = null;
        try {
            URL url = new URL(urlParam);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Quotes_Generator", "The response is: " + response);
            if (response == 200) {
                // OK
                is = conn.getInputStream();
                return readXML(is);
            }
            // Makes sure that the InputStream is closed after the app is finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return null;
    }

    private List<Quote> readXML(InputStream stream) throws XmlPullParserException, IOException {
        List<Quote> quotes = new ArrayList<>();
        boolean isInsideItem = false;
        boolean isInsideAuthor = false;
        boolean isInsideQuote = false;
        Quote quote = null;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(stream, "UTF-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tag = parser.getName();
                if ("item".equals(tag)) {
                    isInsideItem = true;
                    quote = new Quote();    // Create a new quote
                } else if (isInsideItem && "title".equals(tag)) {
                    isInsideAuthor = true;
                } else if (isInsideItem && "description".equals(tag)) {
                    isInsideQuote = true;
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                String tag = parser.getName();
                if ("item".equals(tag)) {
                    isInsideItem = false;
                    quotes.add(quote);  // Add the quote to the list
                } else if (isInsideItem && "title".equals(tag)) {
                    isInsideAuthor = false;
                } else if (isInsideItem && "description".equals(tag)) {
                    isInsideQuote = false;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                String text = parser.getText();
                if (isInsideAuthor) {
                    quote.setAuthor(text);
                } else if (isInsideQuote) {
                    quote.setQuote(text);
                }
            }
            eventType = parser.next();
        }
        return quotes;
    }

    private class CustomArrayAdapter extends ArrayAdapter<Quote> {

        CustomArrayAdapter(Context context, List<Quote> quotes) {
            super(context, 0, quotes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_layout, parent, false);
            }
            TextView quoteText = (TextView) convertView.findViewById(R.id.text_quote);
            TextView quoteAuthor = (TextView) convertView.findViewById(R.id.text_author);

            quoteText.setText(quotes.get(position).getQuote());
            quoteAuthor.setText(quotes.get(position).getAuthor());

            return convertView;
        }
    }

    private class DownloadManager extends AsyncTask<String, Void, List<Quote>> {
        @Override
        protected List<Quote> doInBackground(String... urls) {
            String url = urls[0];
            List<Quote> result = null;
            try {
                result = downloadUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

}
