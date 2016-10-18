package com.miniptech.nerdapp.awesomequotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        ListView listView = (ListView) findViewById(R.id.listView);
        registerForContextMenu(listView);
        receiveQuotes();

        ArrayAdapter<Quote> adapter = new CustomArrayAdapter(this, quotes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String quote = quotes.get(position).getQuote();
                String author = quotes.get(position).getAuthor();
//                Toast.makeText(MainActivity.this, quote, Toast.LENGTH_LONG).show();
//                shareQuote(quote + "\n\t\t--" + author);
            }
        });

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                return true;
//            }
//        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listView) {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.share_menu, menu);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo;
        menuInfo = item.getMenuInfo();
        int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        String quote = quotes.get(position).toString();
        switch (item.getItemId()){
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

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
