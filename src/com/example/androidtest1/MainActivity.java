package com.example.androidtest1;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;


public class MainActivity extends Activity {
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