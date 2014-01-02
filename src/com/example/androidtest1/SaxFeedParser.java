package com.example.androidtest1;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class SaxFeedParser extends BaseFeedParser {
    protected SaxFeedParser(String feedUrl){
        super(feedUrl);
    }
		
	@Override
	public OpenData parse() {
		// TODO Auto-generated method stub
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            OpenDataHandler handler = new OpenDataHandler();
            parser.parse(this.getInputStream(), handler);
            return handler.getOpenData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
	}

}
