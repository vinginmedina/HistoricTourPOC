package com.ving.historictourpoc;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MyApplication extends Application {
	public static final int UNDEF = 0;
	public static final int PAUSE = 1;
	public static final int RUN = 2;
	public static final int STOP = 3;
	public static final int RESTART = 4;
	public static final int REWIND = 5;
	public static final int PLAYPAUSE = 6;
	public static final int PLAY = 7;
	public static final int FASTFORWARD = 8;
	private static Context mContext;
	private ImageView picture;
	private ImageButton rewindBtn;
	private ImageButton playPauseBtn;
	private ImageButton restartBtn;
	private ImageButton stopBtn;
	private ImageButton fastForwardBtn;
	private Drawable rewind;
	private Drawable rewind_disabled;
	private Drawable play;
	private Drawable play_disabled;
	private Drawable pause;
	private Drawable pause_disabled;
	private Drawable playpause;
	private Drawable playpause_disabled;
	private Drawable restart;
	private Drawable restart_disabled;
	private Drawable stop_disabled;
	private Drawable stop;
	private Drawable fastForward;
	private Drawable fastForward_disabled;
	private int sizeStanzaList;
	private ArrayList<Integer> tourStanzaList;
	private ArrayList<ArrayList<TourMetaData>> doubleMetaList;
	private Display display;
	private int width;
	private int height;
	private ProgressDialog pd;
	private Boolean slideShowActive;
	private int message;
	
	@Override
	public void onCreate() {
		mContext = null;
		picture = null;
		rewindBtn = null;
		playPauseBtn = null;
		restartBtn = null;
		stopBtn = null;
		fastForwardBtn = null;
		rewind = null;
		rewind_disabled = null;
		play = null;
		play_disabled = null;
		pause = null;
		pause_disabled = null;
		playpause = null;
		playpause_disabled = null;
		restart = null;
		restart_disabled = null;
		stop_disabled = null;
		stop = null;
		fastForward = null;
		fastForward_disabled = null;
		pd = null;
		display = null;
		width = 0;
		height = 0;
		sizeStanzaList = 0;
		tourStanzaList = null;
		doubleMetaList = null;
		slideShowActive = false;
		message = UNDEF;
		
		super.onCreate();
	}
	
	public void set(Context newContext, Display newDisplay, int newWidth, int newHeight, ImageView pic,
			ImageButton rew, ImageButton ply, ImageButton rest, ImageButton stp, ImageButton ff) {
		mContext = newContext;
		display = newDisplay;
		width = newWidth;
		height = newHeight;
		picture = pic;
		rewindBtn = rew;
		playPauseBtn =ply;
		restartBtn =rest;
		stopBtn =stp;
		fastForwardBtn =ff;
		rewind = mContext.getResources().getDrawable(R.drawable.previous);
		rewind_disabled = mContext.getResources().getDrawable(R.drawable.previous_disabled);
		play = mContext.getResources().getDrawable(R.drawable.play);
		play_disabled = mContext.getResources().getDrawable(R.drawable.play_disabled);
		pause = mContext.getResources().getDrawable(R.drawable.pause);
		pause_disabled = mContext.getResources().getDrawable(R.drawable.pause_disabled);
		playpause = mContext.getResources().getDrawable(R.drawable.playpause);
		playpause_disabled = mContext.getResources().getDrawable(R.drawable.playpause_disabled);
		restart = mContext.getResources().getDrawable(R.drawable.restart);
		restart_disabled = mContext.getResources().getDrawable(R.drawable.restart_disabled);
		stop = mContext.getResources().getDrawable(R.drawable.stop);
		stop_disabled = mContext.getResources().getDrawable(R.drawable.stop_disabled);
		fastForward = mContext.getResources().getDrawable(R.drawable.next);
		fastForward_disabled = mContext.getResources().getDrawable(R.drawable.next_disabled);
	}
	
	public int getScreenOrientation() {
	    int rotation = display.getRotation();
	    int orientation;
	    // if the device's natural orientation is portrait:
	    if ((rotation == Surface.ROTATION_0
	            || rotation == Surface.ROTATION_180) && height > width ||
	        (rotation == Surface.ROTATION_90
	            || rotation == Surface.ROTATION_270) && width > height) {
	        switch(rotation) {
	            case Surface.ROTATION_0:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;
	            case Surface.ROTATION_90:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;
	            case Surface.ROTATION_180:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	                break;
	            case Surface.ROTATION_270:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
	                break;
	            default:
	                Log.e("getScreenOrientation", "Unknown screen orientation. Defaulting to " +
	                        "portrait.");
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;              
	        }
	    }
	    // if the device's natural orientation is landscape or if the device
	    // is square:
	    else {
	        switch(rotation) {
	            case Surface.ROTATION_0:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;
	            case Surface.ROTATION_90:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;
	            case Surface.ROTATION_180:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
	                break;
	            case Surface.ROTATION_270:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	                break;
	            default:
	                Log.e("getScreenOrientation", "Unknown screen orientation. Defaulting to " +
	                        "landscape.");
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;              
	        }
	    }

	    return orientation;
	}
	
	public float calcScale(int inpHeight, int inpWidth) {
		float rtn;
		
		rtn = (float)width / (float)inpWidth;
		return rtn;
	}
	
	public Bundle setStringAsBundle(String message) {
		Bundle b = new Bundle();
		b.putString("message", message);
		return b;
	}
	
	public void setDisabled(int btn) {
		switch(btn) {
		case REWIND:
			rewindBtn.post(new Runnable() {
	            public void run() {
	            	rewindBtn.setEnabled(false);
	            	rewindBtn.setImageDrawable(rewind_disabled);
	            };
	        });
			break;
		case PLAYPAUSE:
			playPauseBtn.post(new Runnable() {
	            public void run() {
	            	playPauseBtn.setEnabled(false);
	            	playPauseBtn.setImageDrawable(playpause_disabled);
	            };
	        });
			break;
		case PLAY:
			playPauseBtn.post(new Runnable() {
	            public void run() {
	            	playPauseBtn.setEnabled(false);
	            	playPauseBtn.setImageDrawable(play_disabled);
	            };
	        });
			break;
		case PAUSE:
			playPauseBtn.post(new Runnable() {
	            public void run() {
	            	playPauseBtn.setEnabled(false);
	            	playPauseBtn.setImageDrawable(pause_disabled);
	            };
	        });
			break;
		case STOP:
			stopBtn.post(new Runnable() {
	            public void run() {
	            	stopBtn.setEnabled(false);
	            	stopBtn.setImageDrawable(stop_disabled);
	            };
	        });
			break;
		case RESTART:
			restartBtn.post(new Runnable() {
	            public void run() {
	            	restartBtn.setEnabled(false);
	            	restartBtn.setImageDrawable(restart_disabled);
	            };
	        });
			break;
		case FASTFORWARD:
			fastForwardBtn.post(new Runnable() {
	            public void run() {
	            	fastForwardBtn.setEnabled(false);
	            	fastForwardBtn.setImageDrawable(fastForward_disabled);
	            };
	        });
			break;
		}
	}
	
	public void setEnabled(int btn) {
		switch(btn) {
		case REWIND:
			rewindBtn.post(new Runnable() {
	            public void run() {
	            	rewindBtn.setEnabled(true);
	            	rewindBtn.setImageDrawable(rewind);
	            };
	        });
			break;
		case PLAYPAUSE:
			playPauseBtn.post(new Runnable() {
	            public void run() {
	            	playPauseBtn.setEnabled(true);
	            	playPauseBtn.setImageDrawable(playpause);
	            };
	        });
			break;
		case PLAY:
			playPauseBtn.post(new Runnable() {
	            public void run() {
	            	playPauseBtn.setEnabled(true);
	            	playPauseBtn.setImageDrawable(play);
	            };
	        });
			break;
		case PAUSE:
			playPauseBtn.post(new Runnable() {
	            public void run() {
	            	playPauseBtn.setEnabled(true);
	            	playPauseBtn.setImageDrawable(pause);
	            };
	        });
			break;
		case STOP:
			stopBtn.post(new Runnable() {
	            public void run() {
	            	stopBtn.setEnabled(true);
	            	stopBtn.setImageDrawable(stop);
	            };
	        });
			break;
		case RESTART:
			restartBtn.post(new Runnable() {
	            public void run() {
	            	restartBtn.setEnabled(true);
	            	restartBtn.setImageDrawable(restart);
	            };
	        });
			break;
		case FASTFORWARD:
			fastForwardBtn.post(new Runnable() {
	            public void run() {
	            	fastForwardBtn.setEnabled(true);
	            	fastForwardBtn.setImageDrawable(fastForward);
	            };
	        });
			break;
		}
	}
	
	public void setAllDisabled() {
		rewindBtn.post(new Runnable() {
            public void run() {
            	rewindBtn.setEnabled(false);
            	rewindBtn.setImageDrawable(rewind_disabled);
            };
        });
		playPauseBtn.post(new Runnable() {
            public void run() {
            	playPauseBtn.setEnabled(false);
            	playPauseBtn.setImageDrawable(playpause_disabled);
            };
        });
		restartBtn.post(new Runnable() {
            public void run() {
            	restartBtn.setEnabled(false);
            	restartBtn.setImageDrawable(restart_disabled);
            };
        });
		stopBtn.post(new Runnable() {
            public void run() {
            	stopBtn.setEnabled(false);
            	stopBtn.setImageDrawable(stop_disabled);
            };
        });
		fastForwardBtn.post(new Runnable() {
            public void run() {
            	fastForwardBtn.setEnabled(false);
            	fastForwardBtn.setImageDrawable(fastForward_disabled);
            };
        });
	}
	
	public void setAllEnabled() {
		rewindBtn.post(new Runnable() {
            public void run() {
            	rewindBtn.setEnabled(true);
            	rewindBtn.setImageDrawable(rewind);
            };
        });
		playPauseBtn.post(new Runnable() {
            public void run() {
            	playPauseBtn.setEnabled(true);
            	playPauseBtn.setImageDrawable(playpause);
            };
        });
		restartBtn.post(new Runnable() {
            public void run() {
            	restartBtn.setEnabled(true);
            	restartBtn.setImageDrawable(restart);
            };
        });
		stopBtn.post(new Runnable() {
            public void run() {
            	stopBtn.setEnabled(true);
            	stopBtn.setImageDrawable(stop);
            };
        });
		fastForwardBtn.post(new Runnable() {
            public void run() {
            	fastForwardBtn.setEnabled(true);
            	fastForwardBtn.setImageDrawable(fastForward);
            };
        });
	}
	
	public void setButtonPlay() {
		playPauseBtn.post(new Runnable() {
            public void run() {
            	playPauseBtn.setImageDrawable(play);
            };
        });
	}
	
	public void setButtonPause() {
		playPauseBtn.post(new Runnable() {
            public void run() {
            	playPauseBtn.setImageDrawable(pause);
            };
        });
	}
	
	public void setPicture(final Bitmap scaledPic) {
		picture.post(new Runnable() {
            public void run() {
            	picture.setImageBitmap(scaledPic);
            	AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0F);
            	alpha.setDuration(500);
            	alpha.setFillAfter(true);
            	picture.startAnimation(alpha);
            };
        });
	}
	
	public void faidOut() {
		picture.post(new Runnable() {
            public void run() {
            	AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.0F);
            	alpha.setDuration(1000);
            	alpha.setFillAfter(true);
            	picture.startAnimation(alpha);
            }
    	});
	}
	
	public void startProcessDialog(final String msg) {
		pd = ProgressDialog.show(mContext, "Serching", msg);
	}
	
	public void cancelProcessDialog() {
		if (pd != null) {
			pd.cancel();
		}
		pd = null;
	}
	
	public void setupStanzaData() {
		tourStanzaList = new ArrayList<Integer>();
		sizeStanzaList = 0;
	}
	
	public ArrayList<Integer> getStanzaList() {
		return tourStanzaList;
	}
	
	public void setupTourData() {
		doubleMetaList = new ArrayList<ArrayList<TourMetaData>>();;
	}
	
	public void addStanzaNumber(JSONObject json_data) {
		try {
		    tourStanzaList.add(json_data.getInt("stanza"));
		}catch(JSONException e) {
			Log.e("myApplication","in meeting error getting JSONObject Data "+e.toString());
		}
		sizeStanzaList++;
	}
	
	public int lengthStanzaList() {
		return sizeStanzaList;
	}
	
	public void addStanzaMetaData(ArrayList<TourMetaData> tourMetaDataList) {
		Log.i("MyApplication","Adding stanza of TourMetaData");
		doubleMetaList.add(tourMetaDataList);
	}
	
	public void sortTourMetaData() {
		for (ArrayList<TourMetaData> tmdl : doubleMetaList) {
			Collections.sort(tmdl);
		}
	}
	
	public int lengthMetaData(int stanza) {
		return doubleMetaList.get(stanza).size();
	}
	
	public ArrayList<TourMetaData> getTourMetaDataList(int stanza) {
		return doubleMetaList.get(stanza);
	}
	
	public void setSlideShowState(Boolean state) {
		slideShowActive = state;
	}
	
	public Boolean slideShowActive() {
		return slideShowActive;
	}
	
	public void setMessage(int m) {
		message = m;
	}
	
	public int consumeMessage() {
		int rtn = message;
		message = UNDEF;
		return rtn;
	}
	
	public Boolean allDataLoaded(int stanza) {
		Log.i("allDataLoaded","Stanza: "+stanza);
		Boolean rtn = true;
		for (TourMetaData tmd : doubleMetaList.get(stanza)) {
		    rtn = rtn && tmd.objectComplete();
		}
		
		return rtn;
	}

}
