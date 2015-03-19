package com.ving.historictourpoc;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class SlideShow implements Runnable {
	private Object mRestart;
	private int message;
    private boolean mPaused;
    private boolean mFinished;
    private ReportSlideShowHandler reportStatusHandler = null;
    private int stanza;
    private int startItem;
    private int currentItem;
    private MyApplication myApp;
    private float secondsLeft;
    
    public SlideShow(int s, int startPtr, MyApplication app, ReportSlideShowHandler h) {
    	stanza = s;
    	startItem = startPtr;
        currentItem = startPtr;
    	myApp = app;
    	reportStatusHandler = h;
    	myApp.setSlideShowState(true);
        mRestart = new Object();
        mPaused = false;
        mFinished = false;
        message = MyApplication.UNDEF;
        
        secondsLeft = 0;
    }
    
	@Override
	public void run() {
		message = myApp.consumeMessage();
		switch (message) {
		case MyApplication.UNDEF:
			break;
		case MyApplication.PAUSE:
			mPaused = true;
			break;
		case MyApplication.RUN:
			mPaused = false;
			break;
		case MyApplication.STOP:
			mFinished = true;
			break;
		case MyApplication.RESTART:
			currentItem = startItem;
			secondsLeft = 0;
			mPaused = false;
			break;
		}
		if ((! mFinished) && (! mPaused)) {
            if (secondsLeft <= 0) {
            	Log.i("SlideShow","Current "+currentItem);
            	if (currentItem < myApp.lengthMetaData(stanza)) {
            		secondsLeft = myApp.getTourMetaDataList(stanza).get(currentItem).displength();
            		Log.i("SlideShow","Seconds left "+secondsLeft);
            		Log.i("SlideShow","Object: "+myApp.getTourMetaDataList(stanza).get(currentItem).url()+" is: "+myApp.getTourMetaDataList(stanza).get(currentItem).objectComplete());
            		Bitmap pic = (Bitmap)myApp.getTourMetaDataList(stanza).get(currentItem).object();
            		int bWidth = pic.getWidth();
            		int bHeight = pic.getHeight();
            		float scale = myApp.calcScale(bWidth,bWidth);
            		Log.i("GetTourObject","Width: "+bWidth+" Height: "+bHeight+" Scale: "+scale);
            		final Bitmap scaledPic = Bitmap.createScaledBitmap(pic, (int)(bWidth*scale), (int)(bHeight*scale), false);
            		myApp.setPicture(scaledPic);
            		currentItem++;
            	} else {
            		mFinished = true;
            	}
            } else {
            	Log.i("SlideShow","Current Item: "+currentItem+" Seconds left "+secondsLeft);
            	secondsLeft -= .25;
            }

//            synchronized (mRestart) {
//            	Log.i("SlideShow","Restarting");
//            	currentItem = startItem;
//            	secondsLeft = 0;
//                while (mPaused) {
//                    try {
//                        mRestart.wait();
//                    } catch (InterruptedException e) {
//                    }
//                }
//            }
        }
		if (!mFinished) {
			reportStatusHandler.postDelayed(this, 250);
		} else {
			myApp.setSlideShowState(false);
			Message m = reportStatusHandler.obtainMessage();
			m.setData(myApp.setStringAsBundle("SlideShowComplete"));
			reportStatusHandler.sendMessage(m);
		}
	}
	
//	public void onPause() {
//        synchronized (mRestart) {
//            mPaused = true;
//        }
//    }
//	
//	public void onResume() {
//        synchronized (mRestart) {
//            mPaused = false;
//            mRestart.notifyAll();
//        }
//    }

}
