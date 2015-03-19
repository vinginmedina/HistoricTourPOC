package com.ving.historictourpoc;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class TourMetaData implements Comparable<TourMetaData> {
	private int id;
	private int stanza;
	private String url;
	private int order;
	private String type;
	private int displength;
	private Object obj;
	private Boolean objComplete;
	
	public TourMetaData(JSONObject data) {
		try{
		    id = data.getInt("id");
		    stanza = data.getInt("stanza");
		    url = data.getString("url");
		    order = data.getInt("disporder");
		    type = data.getString("type");
		    displength = data.getInt("displength");
		    obj = null;
		    objComplete = false;
		}catch(JSONException e) {
			Log.e("myApplication","in meeting error getting JSONObject Data "+e.toString());
		}
	}
	
	public void setObject(Object newObj) {
		obj = newObj;
	}
	
	public int compareTo(TourMetaData tour) {
		int rtn;
		if (this.stanza < tour.stanza) {
			rtn = 1;
		} else if (this.stanza > tour.stanza) {
			rtn = 1;
		} else {
			if (this.order == tour.order) {
				rtn = 0;
			} else if (this.order < tour.order) {
				rtn = -1;
			} else {
				rtn = 1;
			}
		}

		return rtn;
	}
	
	public int stanza() {
		return stanza;
	}
	
	public String url() {
		return url;
	}
	
	public String type() {
		return type;
	}
	
	public int displength() {
		return displength;
	}
	
	public Object object() {
		return obj;
	}
	
	public void setObjectComplete() {
		objComplete = true;
	}
	
	public Boolean objectComplete() {
		return objComplete;
	}

}
