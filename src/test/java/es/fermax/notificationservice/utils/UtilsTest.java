package es.fermax.notificationservice.utils;

import com.google.api.client.util.ArrayMap;
import es.fermax.notificationservice.enums.TargetEnum;
import es.fermax.notificationservice.model.FcmMessage;
import es.fermax.notificationservice.model.Notification;
import es.fermax.notificationservice.rabbit.messages.AppTokenMessage;
import es.fermax.notificationservice.rabbit.messages.FcmMessageAck;

import java.util.ArrayList;
import java.util.Map;

public class UtilsTest {
	private UtilsTest(){}

	/**
	 * Getting App token message for testing.
	 *
	 * @return appTokenMessage object with data to return.
	 */
	public static AppTokenMessage getAppTokenMessage() {
		AppTokenMessage appTokenMessage =new AppTokenMessage();
		appTokenMessage.setToken("token");
		appTokenMessage.setUserId(1);
		appTokenMessage.setOs("os");
		appTokenMessage.setOsVersion("osVersion");
		appTokenMessage.setAppVersion("appVersion");
		appTokenMessage.setActive(true);
		appTokenMessage.setLocale("locale");
		return  appTokenMessage;
	}
	
	public static FcmMessageAck getFcmMessageAck() {
		FcmMessageAck fcmMessageAck =new FcmMessageAck();
		fcmMessageAck.setAttended(false);
		fcmMessageAck.setUserId(1);
		fcmMessageAck.setFirebaseMessageId("fbmessage");
		return  fcmMessageAck;
	}

	public static FcmMessage getFcmMessage (Integer ttl){
		FcmMessage fcmMessage = new FcmMessage();
		Map<String, String> data = new ArrayMap<String,String>();
		fcmMessage.setData(data);
		fcmMessage.setTarget(TargetEnum.TOKEN.target);
		fcmMessage.setTargetValue("any target");
		Notification notification = new Notification();
		notification.setBody("body");
		notification.setTitle("title");


		fcmMessage.setNotification(notification);

		return fcmMessage;
	}

}
