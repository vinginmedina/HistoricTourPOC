package com.ving.historictourpoc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

import android.os.Message;
import android.util.Log;

public class getTourStanzaList implements Runnable {
	
	private int id;
	private MyApplication myApp = null;
	private ReportStatusHandler reportStatusHandler = null;
	private ArrayList<NameValuePair> queryItems = null;
	private JSONArray histTourStanzaData = null;
    private JSONObject json_data = null;
	private Boolean isCancelled;
	private String errorMsg = null;
	private InputStream is = null;    
    private String result = null;
	private static final String stanzaInfoUrl = "http://ving.is-a-geek.net:8888/AATest/getHistTourStanzaInfo.php";
	
	getTourStanzaList(int i, MyApplication app, ReportStatusHandler h) {
		id = i;
		myApp = app;
		reportStatusHandler = h;
		isCancelled = false;
	}
	
	@Override
    public void run() {
		Log.i("GetTourStanzaList","Starting to get data");
		myApp.setupStanzaData();
	    myApp.setupTourData();
		queryItems = new ArrayList<NameValuePair>();
		queryItems.add(new BasicNameValuePair("id",Integer.toString(id)));
	    try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost request = new HttpPost(stanzaInfoUrl);
            request.setEntity(new UrlEncodedFormEntity(queryItems));
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
	    }catch(Exception e){
            Log.e("GetTourStanzaList", "Error in http connection "+e.toString());
            errorMsg = e.toString();
            isCancelled = true;
	    }
	    if (! isCancelled) {
		    // Get response from server
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
			    	histTourStanzaData = new JSONArray(result);
			    }catch(JSONException e){
		            Log.e("GetTourMetaData", "Error generating array data "+e.toString());
		            histTourStanzaData = null;
		            errorMsg = e.toString();
		            isCancelled = true;
			    }
		    } else {
		    	histTourStanzaData = null;
		    }
		    if (histTourStanzaData != null) {
		    	for(int i=0;i<histTourStanzaData.length();i++) {
		    		try {
		    			json_data = histTourStanzaData.getJSONObject(i);
		    			Log.i("GetTourStanzaList","Adding Data: "+json_data);
		    			myApp.addStanzaNumber(json_data);
		    		} catch (JSONException e) {
		    			Log.e("DownloadMeetingDataTask", "Error converting row from meetingTypeData "+e.toString());
		    			errorMsg = e.toString();
		    			isCancelled = true;
		    		}
		    	}
		    }
	    }
	    Log.i("GetTourStanzaList","Finished getting data");
	    Message m = reportStatusHandler.obtainMessage();
	    if (isCancelled) {
	    	m.setData(myApp.setStringAsBundle(errorMsg));
	    } else {
	    	myApp.sortTourMetaData();
	    	m.setData(myApp.setStringAsBundle("StanzaListComplete"));
	    }
	    reportStatusHandler.sendMessage(m);
    }

}
