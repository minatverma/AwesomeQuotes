package com.miniptech.nerdapp.awesomequotes;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.miniptech.nerdapp.awesomequotes.quote.GeneratorListener;
import com.miniptech.nerdapp.awesomequotes.quote.Quote;
import com.miniptech.nerdapp.awesomequotes.quote.QuotesGenerator;

import java.util.ArrayList;
import java.util.List;


public class SplashActivity extends AppCompatActivity implements GeneratorListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        QuotesGenerator generator = new QuotesGenerator();
        generator.setGeneratorListener(this);
        try {
            generator.start(this);
        } catch (NetworkErrorException e) {
            Toast.makeText(this, "No network connection.", Toast.LENGTH_SHORT).show();
            finish();   // Close the application
        }
    }

    @Override
    public void onSucceed(List<Quote> quotes) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("QUOTES", (ArrayList) quotes);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailed(Exception ex) {
        Toast.makeText(this, "Failed to download the quotes. Check Internet Connectivity"
                , Toast.LENGTH_SHORT).show();
        finish();   // Close the application
    }
}
