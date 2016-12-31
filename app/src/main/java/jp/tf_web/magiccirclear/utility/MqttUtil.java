package jp.tf_web.magiccirclear.utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.util.Strings;

import jp.tf_web.magiccirclear.Config;

/**
 * Created by furukawanobuyuki on 2016/12/31.
 */

public class MqttUtil {
    private static String TAG = MqttUtil.class.getSimpleName();

    private MqttAndroidClient mqttAndroidClient;

    public MqttUtil(Context context){
        //MqttAndroidClient を初期化
        mqttAndroidClient = new MqttAndroidClient(context, Config.MQTT_SERVER_URI, Config.MQTT_CLIENT_ID) {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);

                Bundle data = intent.getExtras();

                String action = data.getString("MqttService.callbackAction");
                Object parcel = data.get("MqttService.PARCEL");
                String destinationName = data.getString("MqttService.destinationName");

                if(action.equals("messageArrived"))
                {
                    Log.d(TAG,destinationName + " " + parcel.toString());
                }

            }
        };
    }

    //切断
    public void disconnect(){
        Log.d(TAG,"disconnect");
        try {
            if(mqttAndroidClient.isConnected()) {
                mqttAndroidClient.disconnect();
            }
            mqttAndroidClient.unregisterResources();
        } catch (MqttException e) {
            Log.e(TAG,e.toString());
        }
    }

    /** connect
     *
     */
    public void connect(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(Config.MQTT_USER_NAME);
        options.setPassword(Config.MQTT_USER_PASSWORD.toCharArray());

        try {
            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.d(TAG, "onSuccess");
                    /* Subscribeする場合
                    try {
                        mqttAndroidClient.subscribe("hoge@github/#", 0);
                        Log.d(TAG, "subscribe");
                    } catch (MqttException e) {
                        Log.d(TAG, e.toString());
                    }*/
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.d(TAG, "onFailure");
                }
            });

        } catch (MqttException e) {
            Log.d(TAG, e.toString());
        }
    }

    /** publish
     *
     * @param topic
     * @param payload
     * @param retained
     */
    public void publish(String topic,byte[] payload,boolean retained){
        Log.d(TAG,"publish");
        if(mqttAndroidClient == null) return;
        Log.d(TAG," "+(new String(payload)));

        try {
            if(mqttAndroidClient.isConnected()) {
                IMqttDeliveryToken token = mqttAndroidClient.publish(topic, payload, 0, retained);
            }
        } catch (MqttPersistenceException e) {
            Log.d(TAG,e.toString());
        } catch (MqttException e) {
            Log.d(TAG,e.toString());
        }
    }
}
