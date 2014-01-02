package com.example.androidtest1;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseFeedParser implements FeedParser{
	static final String MESSAGE = "message";
	static final String TIME = "time";
	static final String DRAW = "draw";
	static final String POOLS = "pools";
	static final String WINNERS = "winners";
	static final String WINNER = "winner";
	static final String OPENDAY = "openday";
	static final String BONUS = "bonus";
	static final String RECORDS = "records";
	static final String RECORD = "record";
	static final String REDBALL1 = "redball1";
	static final String REDBALL2 = "redball2";
	static final String REDBALL3 = "redball3";
	static final String REDBALL4 = "redball4";
	static final String REDBALL5 = "redball5";
	static final String REDBALL6 = "redball6";
	static final String BLUEBALL = "blueball";
	
	final URL feedUrl;

    protected BaseFeedParser(String feedUrl){
        try {
            this.feedUrl = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected InputStream getInputStream() {
        try {
            return feedUrl.openConnection().getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
		
}
