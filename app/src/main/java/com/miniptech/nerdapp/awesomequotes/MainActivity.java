package com.miniptech.nerdapp.awesomequotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miniptech.nerdapp.awesomequotes.quote.Quote;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<Quote> quotes;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.listView = (ListView) findViewById(R.id.listView);
        receiveQuotes();

        ArrayAdapter<Quote> adapter = new CustomArrayAdapter(this, quotes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String quote = quotes.get(position).getQuote();
                String author = quotes.get(position).getAuthor();
//                Toast.makeText(MainActivity.this, quote, Toast.LENGTH_LONG).show();
                shareQuote(quote + "\n\t\t--" + author);
            }
        });
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


    private class CustomArrayAdapter extends ArrayAdapter<Quote> {

        public CustomArrayAdapter(Context context, List<Quote> quotes) {
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
}
