package com.example.androidtest1;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static com.example.androidtest1.BaseFeedParser.*;


public class OpenDataHandler extends DefaultHandler {
	private OpenData openData;
	private StringBuilder builder;
	private Message currentMessage;
	private Record currentRecord;
	

	public OpenData getOpenData() {
		return openData;
	}

	public void setOpenData(OpenData openData) {
		this.openData = openData;
	}
	
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        super.endElement(uri, localName, name);
        if (this.currentMessage != null){
            if (localName.equalsIgnoreCase(TIME)){
                currentMessage.setTime(Long.parseLong(builder.toString()));
             } else if (localName.equalsIgnoreCase(MESSAGE)){
                openData.setMessage(currentMessage);
                currentMessage = null;
           }
         }
        else if(this.currentRecord != null){
            if (localName.equalsIgnoreCase(DRAW)){
            	currentRecord.setDraw(Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(OPENDAY)){
            	try{
            		currentRecord.setOpenDay(Long.parseLong(builder.toString()));
            	} catch (NumberFormatException e){
            		currentRecord.setOpenDay(0);
            	}
            } else if (localName.equalsIgnoreCase(WINNERS)){
            	currentRecord.setWinners(Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(BONUS)){
            	currentRecord.setBonus(Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(POOLS)){
            	currentRecord.setPools(Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(REDBALL1)){
            	currentRecord.setRedBall(1, Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(REDBALL2)){
            	currentRecord.setRedBall(2, Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(REDBALL3)){
            	currentRecord.setRedBall(3, Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(REDBALL4)){
            	currentRecord.setRedBall(4, Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(REDBALL5)){
            	currentRecord.setRedBall(5, Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(REDBALL6)){
            	currentRecord.setRedBall(6, Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(BLUEBALL)){
            	currentRecord.setBlueBall(Long.parseLong(builder.toString()));
            } else if (localName.equalsIgnoreCase(RECORD)){
                openData.addRecord(currentRecord);
                currentRecord = null;
            }
        }
        builder.setLength(0);    
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
   //     messages = new ArrayList<Message>();
        builder = new StringBuilder();
        openData = new OpenData();
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        if (localName.equalsIgnoreCase(MESSAGE)){
            this.currentMessage = new Message();
        }
        else if (localName.equalsIgnoreCase(RECORD)){
        	this.currentRecord = new Record();
        }
        builder.setLength(0);     
    }
	
}
