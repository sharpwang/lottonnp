package com.example.androidtest1;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;


public class MainActivity extends Activity {
	private final static int SIZE_OF_HISTORY = 30; 
	private final static int SIZE_OF_EVALUTE = 30;
	private final static int SIZE_OF_TRAINING = 200;
	
	Button redBallOnePredict;
	BasicNetwork netRedBallOne;
	List<Record> records;
	
	Handler handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.obj instanceof OpenData){
				OpenData openData = (OpenData)msg.obj;
				TextView textView1 = (TextView)findViewById(R.id.textView1);
				String lastDraw = String.format("第%d期开奖号码\n%d %d %d %d %d %d + %d", 
						openData.getRecords().get(0).getDraw(),
						openData.getRecords().get(0).getRedBall(1),
						openData.getRecords().get(0).getRedBall(2),
						openData.getRecords().get(0).getRedBall(3),
						openData.getRecords().get(0).getRedBall(4),
						openData.getRecords().get(0).getRedBall(5),
						openData.getRecords().get(0).getRedBall(6),
						openData.getRecords().get(0).getBlueBall()
						); 
				textView1.setText(lastDraw);
				records = openData.getRecords();
				
				
				String recentDraw = "";
				for(int i=0; i<5; i++){
					recentDraw = recentDraw + String.format("第%d期 共%d注一等奖 奖金%d元\n%d %d %d %d %d %d + %d\n",
							openData.getRecords().get(i).getDraw(),
							openData.getRecords().get(i).getWinners(),
							openData.getRecords().get(i).getBonus(),
							openData.getRecords().get(i).getRedBall(1),
							openData.getRecords().get(i).getRedBall(2),
							openData.getRecords().get(i).getRedBall(3),
							openData.getRecords().get(i).getRedBall(4),
							openData.getRecords().get(i).getRedBall(5),
							openData.getRecords().get(i).getRedBall(6),
							openData.getRecords().get(i).getBlueBall()
							);
					}		
				
				TextView textView2 = (TextView)findViewById(R.id.textView2);
				textView2.setText(recentDraw);
				
		
				syncLocalDb(openData);
				
		
				
			}
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);

		Thread background = new Thread(new Runnable() {
			@Override
			public void run(){
				try{
					SaxFeedParser parser = new SaxFeedParser("http://unionlotto.sinaapp.com/Index/open_data");
					OpenData openData = parser.parse();
					Message msg = handler.obtainMessage();
					msg.obj = openData;
					handler.sendMessage(msg);
				}
				catch(Throwable t){
					
				}
			}
		});
		
		background.start();
		
		redBallOnePredict = (Button)findViewById(R.id.radio_button1);
		redBallOnePredict.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub			
				netRedBallOne = new BasicNetwork();
				netRedBallOne.addLayer(new BasicLayer(SIZE_OF_HISTORY));
				netRedBallOne.addLayer(new BasicLayer(19));
				netRedBallOne.addLayer(new BasicLayer(1));
				netRedBallOne.getStructure().finalizeStructure();
				netRedBallOne.reset();
				
				BasicMLDataSet trainingSet = new BasicMLDataSet();
				for(int i = SIZE_OF_HISTORY + SIZE_OF_EVALUTE + SIZE_OF_TRAINING; i > SIZE_OF_HISTORY * 2 + SIZE_OF_EVALUTE; i--){
					double[] inputArray = new double[SIZE_OF_HISTORY];
					for(int j = 0; j < SIZE_OF_HISTORY; j++){
						inputArray[j] = records.get(i - j).getRedBall(1) % 2;	
					}
					double[] idealArray = new double[1];
					idealArray[0] = records.get(i - SIZE_OF_HISTORY).getRedBall(1) % 2;

					BasicMLDataPair pair = new BasicMLDataPair(new BasicMLData(inputArray), new BasicMLData(idealArray));
					trainingSet.add(pair);					
				}

			}
			
			
		});
		
	}
	public void onResume() {
		super.onResume();
	}
	
	public void onPause() {
		super.onPause();
	}
	
	public void syncLocalDb(OpenData o) {
	    DatabaseHelper dbHelper = new DatabaseHelper(this, "localdb"); 
	    for(int i = 0; i < o.getRecords().size(); i++){
	    	dbHelper.getWritableDatabase().execSQL("replace into draws(draw, redball1, redball2, redball3, " +
	    			"redball4, redball5, redball6, blueball, openday, winners, " +
	    			"bonus, pools) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
	    			new String[]{Long.toString(o.getRecords().get(i).getDraw()),
	    			Long.toString(o.getRecords().get(i).getRedBall(1)),
	    			Long.toString(o.getRecords().get(i).getRedBall(2)),
	    			Long.toString(o.getRecords().get(i).getRedBall(3)),
	    			Long.toString(o.getRecords().get(i).getRedBall(4)),
	    			Long.toString(o.getRecords().get(i).getRedBall(5)),
	    			Long.toString(o.getRecords().get(i).getRedBall(6)),
	    			Long.toString(o.getRecords().get(i).getBlueBall()),
	    			Long.toString(o.getRecords().get(i).getOpenDay()),
	    			Long.toString(o.getRecords().get(i).getWinners()),
	    			Long.toString(o.getRecords().get(i).getBonus()),
	    			Long.toString(o.getRecords().get(i).getPools())}
	    	);
	    			
	    }
	}
}