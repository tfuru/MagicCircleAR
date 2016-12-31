package jp.tf_web.magiccirclear.utility;

import android.content.Context;
import android.util.Log;

import com.jins_jp.meme.MemeCalibStatus;
import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;
import com.jins_jp.meme.MemeScanListener;

import jp.tf_web.magiccirclear.Config;
import jp.tf_web.magiccirclear.MainActivity;

/**
 * Created by furukawanobuyuki on 2016/12/31.
 */

public class MemeLibUtil {
    private static String TAG = MemeLibUtil.class.getSimpleName();

    //MEMEライブラリ
    private MemeLib memeLib;

    //リアルタイムリスナー
    public MemeLibUtilListener listener;

    public MemeLibUtil(Context context){
        //MEMEライブラリを初期化
        //Config.MEME_APP_ID,Config.MEME_APP_SECREAT は サイトで取得したアプリID,アプリSecretを設定する
        MemeLib.setAppClientID(context, Config.MEME_APP_ID, Config.MEME_APP_SECREAT);
        memeLib = MemeLib.getInstance();

        //自動接続をOFFにする
        memeLib.setAutoConnect(false);

        //接続結果を取得するためのリスナーを設定
        memeLib.setMemeConnectListener(memeConnectListener);
    }

    //切断
    public void disconnect(){
        if(memeLib.isConnected()){
            memeLib.disconnect();
        }
    }

    public void setListener(MemeLibUtilListener listener){
        this.listener = listener;
    }

    //検索
    public void startScan(){
        //検索を開始
        memeLib.startScan(memeScanListener);
    }

    //スキャンの結果リスナー
    private MemeScanListener memeScanListener = new MemeScanListener(){

        @Override
        public void memeFoundCallback(String s) {
            Log.d(TAG,"memeFoundCallback "+s);
            //見つかった MEMEに接続する
            memeLib.connect(s);
        }
    };

    //接続結果を取得するためのリスナー
    private MemeConnectListener memeConnectListener = new MemeConnectListener(){

        @Override
        public void memeConnectCallback(boolean b) {
            //Log.d(TAG,"memeConnectCallback "+b);
            //接続
            if(listener != null) {
                listener.onConnect();
            }
            //リアルタイムモードリスナーを設定
            memeLib.startDataReport(memeRealtimeListener);
        }

        @Override
        public void memeDisconnectCallback() {
            //Log.d(TAG,"memeDisconnectCallback");

            //切断
            if(listener != null) {
                listener.onDisconnect();
            }
        }
    };

    //リアルタイムモード時の結果を受け取る為のリスナー
    private MemeRealtimeListener memeRealtimeListener = new MemeRealtimeListener(){

        @Override
        public void memeRealtimeCallback(MemeRealtimeData memeRealtimeData) {
            //ルアルタイムモードのデータ取得に成功
            //Log.d(TAG,"memeRealtimeCallback");
            //キャリブレーション状態を取得
            MemeCalibStatus memeCalibStatus = memeLib.isCalibrated();
            if(memeCalibStatus != MemeCalibStatus.CALIB_BOTH_FINISHED) {
                //キャリブレーションが完了していない
                Log.d(TAG,"memeCalibStatus "+memeCalibStatus);
                return;
            }

            //Log.d(TAG, "memeRealtimeData "+ memeRealtimeData);
            if(listener != null){
                listener.onRealtimeData(memeRealtimeData);
            }
        }
    };
}
