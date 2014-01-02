package com.example.androidtest1;

import java.util.ArrayList;
import java.util.List;

public class OpenData {

	private Message message;
	private List<Record> records;

	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public List<Record> getRecords() {
		return records;
	}
	public void setRecords(List<Record> records) {
		this.records = records;
	}	
	
	public void addRecord(Record record){
		records.add(record);
	}
	
	OpenData()
	{
		message = new Message();
		records = new ArrayList<Record>();	
	}
	
	
}
