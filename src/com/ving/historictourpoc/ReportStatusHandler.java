package com.ving.historictourpoc;

import android.os.Handler;
import android.os.Message;

public class ReportStatusHandler extends Handler {
	private MainActivity parentActivity = null;
	
	public ReportStatusHandler(MainActivity inParent) {
		parentActivity = inParent;
	}
	
	@Override
	public void handleMessage(Message msg){
		String pm = msg.getData().getString("message");
		parentActivity.messageReceived(pm);
	}

}
