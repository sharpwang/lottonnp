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
	
	/**
	 * @return the outputSet
	 */
	public List<BasicMLData> getOutputSet() {
		return outputSet;
	}
	/**
	 * @param outputSet the outputSet to set
	 */
	public void setOutputSet(List<BasicMLData> outputSet) {
		this.outputSet = outputSet;
	}
	/**
	 * @return the evalutingSet
	 */
	public List<BasicMLData> getEvalutingSet() {
		return evalutingSet;
	}
	/**
	 * @param evalutingSet the evalutingSet to set
	 */
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
		for(int i = 0; i < this.getOutputSet().size() - 1; i++){
			OutputItem item = new OutputItem(this.getOutputSet().get(i).getData());
			item.makeOutputReadable();	
			outputItems.add(item);
		}
	}
}
