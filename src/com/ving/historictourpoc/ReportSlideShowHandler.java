package com.ving.historictourpoc;

import android.os.Handler;
import android.os.Message;

public class ReportSlideShowHandler extends Handler {
	private TourPresentation parentActivity = null;
	
	public ReportSlideShowHandler(TourPresentation inParent) {
		parentActivity = inParent;
	}
	
	@Override
	public void handleMessage(Message msg){
		String pm = msg.getData().getString("message");
		parentActivity.messageReceived(pm);
	}

}
