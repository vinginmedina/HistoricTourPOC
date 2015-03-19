package com.ving.historictourpoc;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private MyApplication myApp = null;
	private Context mContext = null;
	private ImageView pictureLoc;
	private ImageButton rewindBtn;
	private ImageButton playPauseBtn;
	private ImageButton restartBtn;
	private ImageButton stopBtn;
	private ImageButton fastForwardBtn;
	private int stanzaDownloading;
	private int stanzaPlaying;
	private ReportStatusHandler mHandler = null;
	private TourPresentation presentation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pictureLoc = (ImageView)findViewById(R.id.pic);
		rewindBtn = (ImageButton)findViewById(R.id.previous);
		playPauseBtn = (ImageButton)findViewById(R.id.playpause);
		restartBtn = (ImageButton)findViewById(R.id.restart);
		stopBtn = (ImageButton)findViewById(R.id.stop);
		fastForwardBtn = (ImageButton)findViewById(R.id.next);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		Log.i("MainActivity","Display is Width: "+width+" Height: "+height);
		myApp = (MyApplication) getApplication();
		mContext = this;
		myApp.set(mContext, display, width, height, pictureLoc, rewindBtn, playPauseBtn, restartBtn, stopBtn, fastForwardBtn);
		myApp.setAllDisabled();
		mHandler = new ReportStatusHandler(this);
		getTourStanzaListFromDB(1);
	}
	
	public void mainOnClick(View target) {
		if (presentation != null) {
	    	switch(target.getId()) {
	    	case R.id.previous:
	    		if (stanzaPlaying == 0) {
	    			presentation.restart();
	    		} else {
	    			stanzaPlaying = -1;
	    			presentation.stop();
	    		}
	    		break;
	    	case R.id.playpause:
	    		presentation.switchState();
	    		break;
	    	case R.id.restart:
	    		presentation.restart();
	    		break;
	    	case R.id.stop:
	    		stanzaPlaying = myApp.lengthStanzaList();
	    		presentation.stop();
	    		break;
	    	case R.id.next:
	    		if (stanzaPlaying < myApp.lengthStanzaList()-1) {
	    			presentation.stop();
	    		}
	    		break;
	    	}
		} else {
			if (target.getId() == R.id.restart) {
				getTourStanzaListFromDB(1);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    Log.i("onConfigurationChanged","Switching state");
//	    presentation.switchState();

	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	Log.i("onConfigurationChanged","Orientation: Landscape");
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); 
//	        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
	    	Log.i("onConfigurationChanged","Orientation: Portrait");
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	    } else {
	    	Log.i("onConfigurationChanged","Orientation unknown: "+newConfig.orientation);
	    }
	    if ((presentation != null) && (presentation.isPlaying() == false)) {
	    	presentation.play();
	    }
	  }
	
    @Override
	public void onPause() {
		Log.i("MainActivity","pausing...");
		super.onPause();
		if (presentation != null) {
//			stanzaPlaying = myApp.lengthStanzaList();
//		    presentation.stop();
		    presentation.pause();
		}
	}
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    public void getTourStanzaListFromDB(int id) {
    	myApp.startProcessDialog("Getting Tour Data...");
    	presentation = null;
    	stanzaDownloading = -1;
		stanzaPlaying = -1;
    	Thread stanzaThread = new Thread(new getTourStanzaList(id, myApp, mHandler));
    	stanzaThread.start();
    }
    
    public void getTourMetaDataFromDB(int id) {
    	Thread metaThread = new Thread(new GetTourMetaData(id, myApp, mHandler));
    	metaThread.start();
    }
    
    public void getTourObjects(int stanza) {
    	ArrayList<TourMetaData> tourMetaData = myApp.getTourMetaDataList(stanza);
    	for (TourMetaData tmd : tourMetaData) {
    		Log.i("getTourObjects","Stanza: "+stanza+" URL: "+tmd.url());
    		Thread objectThread = new Thread(new GetTourObject(tmd, mContext, myApp, mHandler));
    		objectThread.start();
    	}
    }
    
    public void messageReceived(String message) {
    	Log.i("MainActivity","Got Message: "+message);
    	if (message.equals("StanzaListComplete")) {
    		getTourMetaDataFromDB(1);
        } else if (message.equals("MetaComplete")) {
        	stanzaDownloading = 0;
    		getTourObjects(stanzaDownloading);
    	} else if (message.equals("ObjectLoaded")) {
    		if (myApp.allDataLoaded(stanzaDownloading)) {
    			if (stanzaPlaying == -1) {
		    		myApp.cancelProcessDialog();
    			}
	    		if (presentation == null) {
	    			stanzaPlaying = stanzaDownloading;
		    		presentation = new TourPresentation(stanzaPlaying, myApp, mHandler);
		    		stanzaDownloading++;
		    		if (stanzaDownloading < myApp.lengthStanzaList()) {
		    			getTourObjects(stanzaDownloading);
		    		}
		    		presentation.displayData();
	    		}
    		}
    	} else if (message.equals("PresentationComplete")) {
    		stanzaPlaying++;
    		Log.i("MainActivity","Stanza: "+stanzaPlaying);
    		if (stanzaPlaying < myApp.lengthStanzaList()) {
    			if (myApp.allDataLoaded(stanzaPlaying)) {
    				presentation = new TourPresentation(stanzaPlaying, myApp, mHandler);
    				stanzaDownloading++;
    				if (stanzaDownloading < myApp.lengthStanzaList()) {
		    			getTourObjects(stanzaDownloading);
		    		}
		    		presentation.displayData();
    			} else {
    				myApp.startProcessDialog("Getting Tour Data...");
    				stanzaPlaying = -1;
    			}
    		} else {
    			myApp.faidOut();
    			presentation = null;
    			myApp.setEnabled(MyApplication.RESTART);
    		}
    	} else {
    		myApp.cancelProcessDialog();
    		myApp.setAllDisabled();
    		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    		builder.setTitle("No Data Retrieved");
    		builder.setMessage("Sorry, there was an error trying to get the data.\n" + message);
    		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                }});
    		builder.setCancelable(false);
    		AlertDialog myAlertDialog = builder.create();
    		myAlertDialog.show();
    	}
    }

}
