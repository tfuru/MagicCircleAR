package jp.tf_web.magiccirclear.utility;

import com.jins_jp.meme.MemeRealtimeData;

/**
 * Created by furukawanobuyuki on 2016/12/31.
 */

public interface MemeLibUtilListener {
    //接続
    void onConnect();

    //切断
    void onDisconnect();

    //リアルタイムモード時の結果を受け取る為のリスナー
    void onRealtimeData(MemeRealtimeData memeRealtimeData);
}
