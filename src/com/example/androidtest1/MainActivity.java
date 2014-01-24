package com.example.androidtest1;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;








public class MainActivity extends Activity {
	private final static int SIZE_OF_HISTORY = 30; 
	private final static int SIZE_OF_EVALUTE = 30;
	private final static int SIZE_OF_TRAINING = 200;
	private final static String TABLE_DRAWS = "draws";
	public static final String FILENAME = "netredone.eg";

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
  
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    
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
/*				TextView textView1 = (TextView)findViewById(R.id.textView1);
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
*/				
		
				syncLocalDb(openData);
				
				loadDataFromLocalDb();
				
			}
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);
		
	    mTitle = mDrawerTitle = getTitle();
		//actionBar = getActionBar();
	   // actionBar.show();
	
        mPlanetTitles = getResources().getStringArray(R.array.drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
     // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        
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
		
/*		redBallOnePredict = (Button)findViewById(R.id.radio_button1);
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
		*/
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
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_refresh:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.drawer_open, Toast.LENGTH_LONG).show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
    	Fragment fragment = new TrainFragment();
        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class TrainFragment extends Fragment {
    	/** The main dataset that includes all the series that go into a chart. */
    	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    	/** The main renderer that includes all the renderers customizing a chart. */
    	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    	/** The most recently added series. */
    	private XYSeries mCurrentSeries;
    	/** The most recently created renderer, customizing the current series. */
    	private XYSeriesRenderer mCurrentRenderer;
    	/** The chart view that displays the data. */
    	private GraphicalView mChartView;
    	
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public TrainFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_train, container, false);
            
            // set some properties on the main renderer
            mRenderer.setApplyBackgroundColor(true);
            mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
            mRenderer.setAxisTitleTextSize(16);
            mRenderer.setChartTitleTextSize(20);
            mRenderer.setLabelsTextSize(15);
            mRenderer.setLegendTextSize(15);
            mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
            mRenderer.setZoomButtonsVisible(true);
            mRenderer.setPointSize(5);

            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.chart);
            mChartView = ChartFactory.getLineChartView(getActivity(), mDataset, mRenderer);
            // enable the chart click events
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);
            layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT));
            
 /*           int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                            "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
   */         return rootView;
        }
    }
		
}