package com.miniptech.nerdapp.awesomequotes.quote;

import java.util.List;


public interface GeneratorListener {
    void onSucceed(List<Quote> quotes);

    void onFailed(Exception ex);
}
