package jp.tf_web.magiccirclear;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jins_jp.meme.MemeRealtimeData;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.tf_web.magiccirclear.utility.MemeLibUtil;
import jp.tf_web.magiccirclear.utility.MemeLibUtilListener;
import jp.tf_web.magiccirclear.utility.MqttUtil;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    //MEMEライブラリ
    private MemeLibUtil memeLibUtil;

    //MQTTライブラリ
    public MqttUtil mqttUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ButterKnifeを初期化
        ButterKnife.bind(this);

        //MQTTを利用するためのライブラリ初期化
        mqttUtil = new MqttUtil(getApplicationContext());

        //MEME SDKを扱う為のユーテリティを初期化
        memeLibUtil = new MemeLibUtil(getApplicationContext());

        //リアルタイムリスナーを設定
        memeLibUtil.setListener(memeLibUtilListener);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy");

        memeLibUtil.disconnect();
    }

    @OnClick(R.id.btnSearch)
    void clickBtnSearch(){
        Log.d(TAG,"clickBtnSearch");
        memeLibUtil.startScan();
    }

    @OnClick(R.id.btnDisconnect)
    void clickBtnDisconnect(){
        Log.d(TAG,"clickBtnDisconnect");
        //接続中のMEMEから切断
        memeLibUtil.disconnect();
    }

    @OnClick(R.id.btnBrowser)
    void clickBtnBrowser(){
        Log.d(TAG,"clickBtnBrowser");
        Uri uri = Uri.parse(Config.VIEW_URL);
        Intent i = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(i);
    }

    //-----
    //リアルタイムモード時の結果を受け取る為のリスナー
    private MemeLibUtilListener memeLibUtilListener = new MemeLibUtilListener(){

        @Override
        public void onConnect() {
            //接続
            Log.d(TAG,"onConnect");

            //MQTTサーバに接続
            mqttUtil.connect();
        }

        @Override
        public void onDisconnect() {
            //切断
            Log.d(TAG,"onDisconnect");

            //MQTTサーバから切断
            mqttUtil.disconnect();
        }

        @Override
        public void onRealtimeData(MemeRealtimeData memeRealtimeData) {
            //MQTTでサーバにPublishする
            MemePayload memePayload = new MemePayload(memeRealtimeData);
            mqttUtil.publish(Config.MQTT_TOPIC,memePayload.toJsonByteArray(), true);
        }
    };
}
