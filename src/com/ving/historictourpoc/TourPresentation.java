package com.ving.historictourpoc;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

public class TourPresentation {
	
	private int stanza;
	private MyApplication myApp = null;
	private int sizeTourMetaData;
	private ArrayList<TourMetaData> tourMetaData;
	private Boolean restartCurrent;
	private int current;
	private MediaPlayer mp;
	private Boolean mpPlaying;
	private Runnable timerRunnable;
	private ReportStatusHandler reportHandler;
	private ReportSlideShowHandler timerHandler;
	
	public TourPresentation (int s, MyApplication app, ReportStatusHandler h) {
		stanza = s;
		myApp = app;
		reportHandler = h;
		sizeTourMetaData = myApp.lengthMetaData(stanza);
		tourMetaData = myApp.getTourMetaDataList(stanza);
		restartCurrent = false;
		current = 0;
		mp = null;
		mpPlaying = false;
		myApp.setMessage(MyApplication.UNDEF);
		timerRunnable = null;
		timerHandler = new ReportSlideShowHandler(this);
	}
	
	public void displayData() {
		Log.i("TourPresentation","Display all data");
		Log.i("TourPresentation","Size of list: "+sizeTourMetaData);
		restartCurrent = false;
		current = 0;
		while (tourMetaData.get(current).type().equals("audio")) {
			mp = new MediaPlayer();
			try {
				mp.setDataSource((String)tourMetaData.get(current).object());
				mp.prepare();
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
		            @Override
		            public void onCompletion(MediaPlayer mp) {
		            	mpPlaying = false;
		                finished();
		            }
		        });
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			current++;
		}
		myApp.setMessage(MyApplication.UNDEF);
		timerRunnable = new SlideShow(stanza, current, myApp, timerHandler);
		myApp.setAllEnabled();
		myApp.setButtonPause();
		if (stanza == myApp.lengthStanzaList()-1) {
			Log.i("TourPresentation","Disable fast forward");
			myApp.setDisabled(MyApplication.FASTFORWARD);
		}
		timerHandler.postDelayed(timerRunnable, 0);
		mp.start();
		mpPlaying = true;
	}
	
	public Boolean isPlaying() {
		return mpPlaying;
	}
	
	public void pause() {
		myApp.setMessage(MyApplication.PAUSE);
		if (mp != null) {
			mp.pause();
			mpPlaying = false;
		}
		myApp.setButtonPlay();
	}
	
	public void play() {
		myApp.setMessage(MyApplication.RUN);
		if (mp != null) {
			mp.start();
			mpPlaying = true;
		}
		myApp.setButtonPause();
	}
	
	public void switchState() {
		if (mpPlaying) {
			pause();
		} else {
			play();
		}
	}
	
	public void restart() {
		Log.i("Restart","mpPlaying: "+mpPlaying+" slideShowActive: "+myApp.slideShowActive());
		if ((mpPlaying) && (myApp.slideShowActive())) {
			mp.pause();
			mp.seekTo(0);
			myApp.setMessage(MyApplication.RESTART);
			mp.start();
		} else {
			restartCurrent = true;
			stop();
		}
	}
	
	public void stop() {
		myApp.setMessage(MyApplication.STOP);
		if (mp != null) {
			mpPlaying = false;
		    mp.stop();
		}
		finished();
	}
	
	public void messageReceived(String message) {
		Log.i("TourPresentation","Got Message: "+message);
		if (message.equals("SlideShowComplete")) {
			finished();
		}
	}
	
	public void finished() {
		Log.i("finished","mpPlaying: "+mpPlaying+" slideShowActive: "+myApp.slideShowActive());
		if ((! mpPlaying) && (! myApp.slideShowActive())) {
			if (restartCurrent) {
				displayData();
			} else {
				myApp.setAllDisabled();
	        	mp = null;
	        	timerRunnable = null;
	        	Message m = reportHandler.obtainMessage();
				m.setData(myApp.setStringAsBundle("PresentationComplete"));
				reportHandler.sendMessage(m);
			}
        }
	}

}
