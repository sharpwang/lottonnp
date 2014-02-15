package com.example.androidtest1;

import java.io.File;
import org.encog.engine.network.activation.ActivationSigmoid;
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
import android.widget.TextView;
import android.widget.Toast;

public class TrainActivity extends Activity {
	private final static int SIZE_OF_HISTORY = MyApp.getSizeOfHistory();
	private final static int SIZE_OF_EVALUE = MyApp.getSizeOfEvalute();
	private final static int SIZE_OF_TRAINING = MyApp.getSizeOfTraining();

	
    
	Handler handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.obj instanceof String){
            	            	
                TextView textView = (TextView) findViewById(R.id.textView1);  
                textView.setText((String)msg.obj);
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
	        					//train.addStrategy(new RequiredImprovementStrategy(5));
	        					int epoch = 1;

	        					do {
		        					train.iteration();
		        					String messageText = "Epoch #" + epoch + " Error:" + train.getError();
		        					Message msg = handler.obtainMessage();
		        					msg.obj = messageText;
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
