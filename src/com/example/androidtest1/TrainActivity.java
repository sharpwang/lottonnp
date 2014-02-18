package com.example.androidtest1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class TrainActivity extends Activity {
	private final static int SIZE_OF_HISTORY = MyApp.getSizeOfHistory();
	private final static int SIZE_OF_EVALUE = MyApp.getSizeOfEvalute();
	private final static int SIZE_OF_TRAINING = MyApp.getSizeOfTraining();
	private GraphicalView mChart;
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries;
	private XYSeriesRenderer mCurrentRenderer;
	List<Double> errors;

    private void initChart() {
        mCurrentSeries = new XYSeries("Sample Data");
        mDataset.addSeries(mCurrentSeries);
        mCurrentRenderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(mCurrentRenderer);
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        LinearLayout layout = (LinearLayout) findViewById(R.id.chart1);
        if (mChart == null) {
            initChart();
         //   addSampleData();
            mChart = ChartFactory.getCubeLineChartView(this, mDataset, mRenderer, 0.3f);
            layout.addView(mChart);
        } else {
            mChart.repaint();
        }
	}

	
    
	Handler handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.obj instanceof Double){
				Double error = (Double)msg.obj;
				String msgText = "error:" + error;
				mCurrentSeries.add(mCurrentSeries.getItemCount() + 1, error);
            	            	
                TextView textView = (TextView) findViewById(R.id.textView1);  
                textView.setText(msgText);
                mChart.repaint();
			}
			else if(msg.obj instanceof List<?>){
				((MyApp)getApplication()).setOutputSet((List<BasicMLData>)msg.obj);
				((MyApp)getApplication()).makeOutputReadable();
			}
		}
   	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);

	       Button button = (Button) findViewById(R.id.button1);  
	        button.setOnClickListener(new OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//数据存放在records中
	            	
	            	initChart();
	            	
	        		Thread background = new Thread(new Runnable() {
	        			@Override
	        			public void run(){
	        				try{
	        					BasicNetwork network = new BasicNetwork();
	        					network.addLayer(new BasicLayer(null, true, SIZE_OF_HISTORY * 49));
	        					network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (int)Math.sqrt(SIZE_OF_HISTORY* 49 + 49) + 9));
	        					network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 49));
	        					network.getStructure().finalizeStructure();
	        					network.reset();
	        					NeuralDataSet trainingSet =	new BasicNeuralDataSet(((MyApp)getApplication()).getTrainingSet());
	        					// train the neural network
	        					final Train train =	new ResilientPropagation(network, trainingSet);
	        					// reset if improve is less than 1% over 5 cycles
	        					train.addStrategy(new RequiredImprovementStrategy(10));
	        					int epoch = 1;
	        					//List<Double> errors = new ArrayList<Double>();
	        					do {
		        					train.iteration();
		        					Message msg = handler.obtainMessage();
		        					msg.obj = train.getError();
		        					handler.sendMessage(msg);

		        					epoch++;
	        					} while(train.getError() > 0.01);		
	        					
	        					System.out.println("Saving network");
	        					try{
	        					String path= getApplicationContext().getFilesDir().getAbsolutePath();	
	        					String file = path + "/" + MyApp.getModelFileName();
	        					EncogDirectoryPersistence.saveObject(new File(file), network);
	        					//BasicNetwork network = (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(FILENAME));
	        					System.out.println(file);
	        					}catch(Exception e){
	        						System.out.println(e.getMessage());
	        					}
	        					
	        					System.out.println("Evaluting network");
	        					
	        					List<BasicMLData> outputSet = new ArrayList<BasicMLData>();
	        					List<BasicMLData> evalutingSet  = ((MyApp)getApplication()).getEvalutingSet();
	        					for(int i = 0; i < SIZE_OF_EVALUE; i++){
	        						outputSet.add((BasicMLData)network.compute(evalutingSet.get(i)));
	        					}

	        					Message msg = handler.obtainMessage();
	        					msg.obj = outputSet;
	        					handler.sendMessage(msg);
	        					
	        					
	        				}
	        				catch(Throwable t){
	        					
	        				}
	        			}
	        		});
	        		
	        		background.start();

	            }  
	        });  
	}

}
