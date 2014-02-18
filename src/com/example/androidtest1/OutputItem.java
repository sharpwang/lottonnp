package com.example.androidtest1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.encog.ml.data.basic.BasicMLData;

public class OutputItem {
	private Record record;
	private List<Integer> redBalls;
	private List<Integer> blueBalls;
	private Integer score;
	private double[] output;
	
	OutputItem(double[] output){
		this.output = output;
	}
	
	
	class Output{
		private Integer number;
		private Double output;
		public Integer getNumber() {
			return number;
		}
		public void setNumber(Integer number) {
			this.number = number;
		}
		public Double getOutput() {
			return output;
		}
		public void setOutput(Double output) {
			this.output = output;
		}
		
		Output(Integer number, Double output){
			this.number = number;
			this.output = output;
		}		
	}
	
	public Record getRecord() {
		return record;
	}
	public void setRecord(Record record) {
		this.record = record;
	}
	public List<Integer> getRedBalls() {
		return redBalls;
	}
	public void setRedBalls(List<Integer> redBalls) {
		this.redBalls = redBalls;
	}
	public List<Integer> getBlueBalls() {
		return blueBalls;
	}
	public void setBlueBalls(List<Integer> blueBalls) {
		this.blueBalls = blueBalls;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public double[] getOutput() {
		return output;
	}
	public void setOutput(double[] output) {
		this.output = output;
	}
	
	public void makeOutputReadable(){
		List<Output> redList1 = new ArrayList<Output>();
		List<Output> blueList1 = new ArrayList<Output>();
		
		double[] data = this.output;
		for(int i = 0; i < 33; i++){
			redList1.add(new Output(i + 1, data[i]));
		}
		
		for(int i= 33; i < 49; i++){
			blueList1.add(new Output(i - 33 + 1, data[i]));
		}
		
		//首先，按照预测结果出现概率最大的排序，降序
		Collections.sort(redList1, new Comparator<Output>(){   
	           public int compare(Output arg0, Output arg1) {   
	               return arg1.getOutput().compareTo(arg0.getOutput());   
	            }   
	        });   
		Collections.sort(blueList1, new Comparator<Output>(){   
	           public int compare(Output arg0, Output arg1) {   
	               return arg1.getOutput().compareTo(arg0.getOutput());   
	            }   
	        }); 
		
		List<Output> redList2 = new ArrayList<Output>();
		List<Output> blueList2 = new ArrayList<Output>();		
		for(int i = 0; i < 9; i++){
			redList2.add(redList1.get(i));
		}
		for(int i = 0; i < 2; i++){
			blueList2.add(blueList1.get(i));
		}
		//其次，按照号码排序，升序
		Collections.sort(redList1, new Comparator<Output>(){   
	           public int compare(Output arg0, Output arg1) {   
	               return arg0.getNumber().compareTo(arg1.getNumber());
	            }   
	        });   
		Collections.sort(blueList1, new Comparator<Output>(){   
	           public int compare(Output arg0, Output arg1) {   
	        	   return arg0.getNumber().compareTo(arg1.getNumber());
	            }   
	        }); 
		
		redBalls = new ArrayList<Integer>();
		for(int i = 0; i < redList2.size() - 1; i++){
			redBalls.add(redList2.get(i).getNumber());
		}
		for(int i = 0; i < blueList2.size() - 1; i++){
			blueBalls.add(blueList2.get(i).getNumber());
		}	
	}
}
