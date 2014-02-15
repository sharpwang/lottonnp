package com.example.androidtest1;

import java.util.List;
import org.encog.neural.data.NeuralDataSet;
import android.app.Application;

public class MyApp extends Application {
	private final static int SIZE_OF_HISTORY = 5; 
	private final static int SIZE_OF_EVALUTE = 5;
	private final static int SIZE_OF_TRAINING = 50;

	public static int getSizeOfHistory() {
		return SIZE_OF_HISTORY;
	}
	public static int getSizeOfEvalute() {
		return SIZE_OF_EVALUTE;
	}
	public static int getSizeOfTraining() {
		return SIZE_OF_TRAINING;
	}
	
	private List<Record> records;
	private NeuralDataSet trainingSet;
	
	public List<Record> getRecords() {
		return records;
	}
	public void setRecords(List<Record> records) {
		this.records = records;
	}
	public NeuralDataSet getTrainingSet() {
		return trainingSet;
	}
	public void setTrainingSet(NeuralDataSet trainingSet) {
		this.trainingSet = trainingSet;
	}

}
