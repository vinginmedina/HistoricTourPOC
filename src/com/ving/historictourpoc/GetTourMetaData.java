package com.ving.historictourpoc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class GetTourMetaData implements Runnable {
	private int id;
	private MyApplication myApp = null;
	private ReportStatusHandler reportStatusHandler = null;
	private ArrayList<NameValuePair> queryItems = null;
	private String errorMsg = null;
	private InputStream is = null;    
    private String result = null;
    private JSONArray histTourMetaData = null;
    private JSONObject json_data = null;
    private ArrayList<TourMetaData> tourMetaDataList = null;
    private Boolean isCancelled;
    
	private static final String tourInfoUrl = "http://ving.is-a-geek.net:8888/AATest/getHistTourInfo.php";

	GetTourMetaData(int i, MyApplication app, ReportStatusHandler h) {
		id = i;
		myApp = app;
		reportStatusHandler = h;
		isCancelled = false;
	}
    
	@Override
    public void run() {
		Log.i("GetTourMetaData","Starting to get data");
		myApp.setupTourData();
		for (Integer stanza : myApp.getStanzaList()) {
			if (! isCancelled) {
				queryItems = new ArrayList<NameValuePair>();
				queryItems.add(new BasicNameValuePair("id",Integer.toString(id)));
				queryItems.add(new BasicNameValuePair("stanza",Integer.toString(stanza)));
			    try{
		            HttpClient httpclient = new DefaultHttpClient();
		            HttpPost request = new HttpPost(tourInfoUrl);
		            request.setEntity(new UrlEncodedFormEntity(queryItems));
		            HttpResponse response = httpclient.execute(request);
		            HttpEntity entity = response.getEntity();
		            is = entity.getContent();
			    }catch(Exception e){
		            Log.e("GetTourMetaData", "Error in http connection "+e.toString());
		            errorMsg = e.toString();
		            isCancelled = true;
			    }
			}
		    if (! isCancelled) {
			    try{
		            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		            StringBuilder sb = new StringBuilder();
		            String line = null;
		            while ((line = reader.readLine()) != null) {
		                sb.append(line + "\n");
		            }
		            is.close();
		            result=sb.toString();
			    }catch(Exception e){
		            Log.e("GetTourMetaData", "Error getting data "+e.toString());
		            errorMsg = e.toString();
		            isCancelled = true;
			    }
			    if (! result.equals("null\n")) {
				    try{
				    	histTourMetaData = new JSONArray(result);
				    }catch(JSONException e){
			            Log.e("GetTourMetaData", "Error generating array data "+e.toString());
			            histTourMetaData = null;
			            errorMsg = e.toString();
			            isCancelled = true;
				    }
			    } else {
			    	histTourMetaData = null;
			    }
			    if (! isCancelled) {
				    tourMetaDataList = new ArrayList<TourMetaData>();
				    if (histTourMetaData != null) {
				    	for(int i=0;i<histTourMetaData.length();i++) {
				    		try {
				    			json_data = histTourMetaData.getJSONObject(i);
				    			tourMetaDataList.add(new TourMetaData(json_data));
				    		} catch (JSONException e) {
				    			Log.e("DownloadMeetingDataTask", "Error converting row from TourMetaData "+e.toString());
				    			errorMsg = e.toString();
				    			isCancelled = true;
				    		}
				    	}
				    }
			    }
			    if (! isCancelled) {
				    Collections.sort(tourMetaDataList);
				    myApp.addStanzaMetaData(tourMetaDataList);
			    }
		    }
		}
	    Log.i("GetTourMetaData","Finished getting data");
	    Message m = reportStatusHandler.obtainMessage();
	    if (isCancelled) {
	    	m.setData(myApp.setStringAsBundle(errorMsg));
	    } else {
	    	myApp.sortTourMetaData();
	    	m.setData(myApp.setStringAsBundle("MetaComplete"));
	    }
	    reportStatusHandler.sendMessage(m);
    }
}
