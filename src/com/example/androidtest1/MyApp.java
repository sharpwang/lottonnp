package com.example.androidtest1;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.data.NeuralDataSet;
import android.app.Application;

public class MyApp extends Application {
	private static final int  SIZE_OF_HISTORY = 5; 
	private static final int SIZE_OF_EVALUTE = 5;
	private static final int SIZE_OF_TRAINING = 50;
	public static final String MODEL_FILE_NAME = "trainmodel.eg";

	/**
	 * @return the filename
	 */
	public static String getModelFileName() {
		return MODEL_FILE_NAME;
	}
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
	private List<BasicMLData> evalutingSet;
	private List<BasicMLData> outputSet;
	private List<OutputItem> outputItems; //经过转化的可读的预测结果

	public List<OutputItem> getOutputItems() {
		return outputItems;
	}
	

	public List<BasicMLData> getOutputSet() {
		return outputSet;
	}
	public void setOutputSet(List<BasicMLData> outputSet) {
		this.outputSet = outputSet;
	}
	public List<BasicMLData> getEvalutingSet() {
		return evalutingSet;
	}
	public void setEvalutingSet(List<BasicMLData> evalutingSet) {
		this.evalutingSet = evalutingSet;
	}
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

	public void makeOutputReadable()
	{
		outputItems = new ArrayList<OutputItem>();
		for(int i = 0; i < this.getOutputSet().size(); i++){
			OutputItem item = new OutputItem(this.getOutputSet().get(i).getData());
			if(i > 0)
			item.setRecord(this.getRecords().get(i - 1));
			item.makeOutputReadable();	
			outputItems.add(item);
		}
	}
}
