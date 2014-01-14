package com.example.androidtest1;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.persist.EncogDirectoryPersistence;


public class MainActivity extends Activity {
	private final static int SIZE_OF_HISTORY = 30; 
	private final static int SIZE_OF_EVALUTE = 30;
	private final static int SIZE_OF_TRAINING = 200;
	private final static String TABLE_DRAWS = "draws";
	public static final String FILENAME = "netredone.eg";

    DatabaseHelper dbHelper; 
	Button redBallOnePredict;
	BasicNetwork netRedBallOne;
	List<Record> records;
	
	ActionBar actionBar;
	
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
				
				loadDataFromLocalDb();
				
			}
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);
		
		actionBar = getActionBar();
	    actionBar.show();
		
	    dbHelper = new DatabaseHelper(this, "localdb"); 

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

				netRedBallOne.addLayer(new BasicLayer(null, true, SIZE_OF_HISTORY));
				netRedBallOne.addLayer(new BasicLayer(new ActivationSigmoid(), true, 19));
				netRedBallOne.addLayer(new BasicLayer(new ActivationSigmoid(), true, 1));
				netRedBallOne.getStructure().finalizeStructure();
				netRedBallOne.reset();
				
				BasicMLDataSet trainingSet = new BasicMLDataSet();
				for(int i = SIZE_OF_EVALUTE ; i <  SIZE_OF_EVALUTE + SIZE_OF_TRAINING; i++){
					double[] inputArray = new double[SIZE_OF_HISTORY];
					for(int j = 0; j < SIZE_OF_HISTORY; j++){
						inputArray[j] = records.get(i + j + 1 ).getRedBall(1) % 2;	
					}
					double[] idealArray = new double[1];
					idealArray[0] = records.get(i).getRedBall(1) % 2;
					BasicMLDataPair pair = new BasicMLDataPair(new BasicMLData(inputArray), 
							new BasicMLData(idealArray));
					trainingSet.add(pair);					
				}
				
				final Train train = new ResilientPropagation(netRedBallOne,
						trainingSet);
		
				int epoch = 1;
				do {
				train.iteration();
				System.out.println("Epoch #" + epoch + " Error:"
				+ train.getError());
				epoch++;
				} while(train.getError() > 0.4);
	
				System.out.println("Saving network");
				try{
				String path= getApplicationContext().getFilesDir().getAbsolutePath();	
				String file = path + "/" + FILENAME;
				EncogDirectoryPersistence.saveObject(new File(file), netRedBallOne);
				//BasicNetwork network = (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(FILENAME));

				}catch(Exception e){
					System.out.println(e.getMessage());
				}
				
				BasicMLDataSet evaluteSet = new BasicMLDataSet();				
				for(int i = 0; i < SIZE_OF_EVALUTE; i++ )
				{
					double[] inputArray = new double[SIZE_OF_HISTORY];
					for(int j = 0; j < SIZE_OF_HISTORY; j++){
						inputArray[j] = records.get(i + j + 1 ).getRedBall(1) % 2;	
					}
					double[] idealArray = new double[1];
					idealArray[0] = records.get(i).getRedBall(1) % 2;
					
					BasicMLDataPair pair = new BasicMLDataPair(new BasicNeuralData(inputArray), new BasicNeuralData(idealArray));
					evaluteSet.add(pair);									
				}
				
				System.out.println("Neural Network Results:");
				for(MLDataPair pair: evaluteSet){
				final BasicMLData output =
						(BasicMLData) netRedBallOne.compute(pair.getInput());
				System.out.println("actual=" + output.getData(0) + ",ideal=" +
				pair.getIdeal().getData(0));
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
		SQLiteDatabase db = dbHelper.getWritableDatabase();
	    for(int i = 0; i < o.getRecords().size(); i++){
	    	db.execSQL("replace into draws(draw, redball1, redball2, redball3, " +
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
	    db.close();
	}
	
	private void loadDataFromLocalDb(){
		records = new ArrayList<Record>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DRAWS, new String[]{"draw", "redball1", 
					"redball2", "redball3", "redball4", "redball5", "redball6", "blueball"}, null, null,
					null, null, "draw desc", String.valueOf(SIZE_OF_HISTORY + SIZE_OF_EVALUTE + SIZE_OF_TRAINING + 1));
		 for (cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {  
			 Record record = new Record();
			 long draw = cursor.getLong(cursor.getColumnIndex("draw"));
			 long redBall1 = cursor.getLong(cursor.getColumnIndex("redball1"));
			 long redBall2 = cursor.getLong(cursor.getColumnIndex("redball2"));
			 long redBall3 = cursor.getLong(cursor.getColumnIndex("redball3"));
			 long redBall4 = cursor.getLong(cursor.getColumnIndex("redball4"));
			 long redBall5 = cursor.getLong(cursor.getColumnIndex("redball5"));
			 long redBall6 = cursor.getLong(cursor.getColumnIndex("redball6"));
			 long blueBall = cursor.getLong(cursor.getColumnIndex("blueball"));
			 long redBalls[] = new long[]{redBall1, redBall2, redBall3, redBall4, redBall5, redBall6};

			 record.setDraw(draw);
			 record.setRedBalls(redBalls);
			 record.setBlueBall(blueBall);
			 records.add(record);
	        }  
	        cursor.close();//关闭结果集  
	        db.close();//关闭数据库对象  
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actions, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {     
		switch (item.getItemId()) {         
		case android.R.id.home: 
			// app icon in action bar clicked; go home   
			Intent intent = new Intent(this, MainActivity.class);             
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(intent);
			return true;        
		case R.id.action_refresh:
			
		default:              
			return super.onOptionsItemSelected(item);     
		} 
	}

		
}