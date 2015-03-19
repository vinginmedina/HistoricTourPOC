package com.ving.historictourpoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class GetTourObject implements Runnable {
	private Context mContext = null;
	private String tag = null;
	private TourMetaData tourData = null;
	private ReportStatusHandler reportStatusHandler = null;
	private ProgressDialog pd = null;
	private String errorMsg = null;
	private InputStream inp = null;
	private URL url = null;
	private URLConnection cnx = null;
	private Boolean isCancelled;
	private MyApplication myApp;
	
	GetTourObject(TourMetaData inpTourData, Context inpContext, MyApplication app, ReportStatusHandler h) {
		tourData = inpTourData;
		mContext = inpContext;
		myApp = app;
		reportStatusHandler = h;
		isCancelled = false;
	}
    
    public void run() {
		Log.i("GetTourObject","Starting to get data "+tourData.url());
		Message m = reportStatusHandler.obtainMessage();
		String fileName = tourData.url().substring(tourData.url().lastIndexOf('/') + 1);
		Log.i("GetTourObject","File name from url: "+fileName);
		File downloadingMediaFile = new File(mContext.getCacheDir(),fileName);
		if (downloadingMediaFile.exists()) {
			Log.i("GetTourObject","Already downloaded "+tourData.url());
			tourData.setObject(downloadingMediaFile.toString());
			tourData.setObjectComplete();
			m.setData(myApp.setStringAsBundle("ObjectLoaded"));
			reportStatusHandler.sendMessage(m);
    		return;
		}
	    try{
	    	url = new URL(tourData.url());
			cnx = url.openConnection();
			cnx.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			cnx.setDoInput(true);
            cnx.setDoOutput(true);
            cnx.setUseCaches(false);
            cnx.addRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/x-shockwave-flash, */*");
            inp = cnx.getInputStream();
	    }catch(Exception e){
	    	e.printStackTrace();
			errorMsg = e.toString();
			isCancelled = true;
	    }
	    if (! isCancelled) {
	    	if (tourData.type().equals("picture")) {
	    		try {
			    	Bitmap myBitmap = BitmapFactory.decodeStream(inp);
			    	tourData.setObject(myBitmap);
	    		}catch(Exception e){
	    			e.printStackTrace();
	    			errorMsg = e.toString();
	    			isCancelled = true;
	    		}
	    	} else {
	    		try {
	    			Log.i("Download Audio","File: "+downloadingMediaFile);
	    		    FileOutputStream out = new FileOutputStream(downloadingMediaFile);   
	    		    byte buf[] = new byte[16384];
	    		    int numread;
	    		    while ((numread = inp.read(buf)) > 0) {   
	    		        out.write(buf, 0, numread);
	    		    }
	    		    inp.close();
	    		    tourData.setObject(downloadingMediaFile.toString());
	    		} catch(Exception e){
	    	    	e.printStackTrace();
	    			errorMsg = e.toString();
	    			isCancelled = true;
	    	    }
	    	}
	    }
	    Log.i("GetTourObject","Finished getting data "+tourData.url());
	    if (isCancelled) {
	    	m.setData(myApp.setStringAsBundle(errorMsg));
	    } else {
    		tourData.setObjectComplete();
    		m.setData(myApp.setStringAsBundle("ObjectLoaded"));
    	}
	    reportStatusHandler.sendMessage(m);
    }

}

