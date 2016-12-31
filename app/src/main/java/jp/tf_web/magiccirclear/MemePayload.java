package jp.tf_web.magiccirclear;

import com.google.gson.Gson;
import com.jins_jp.meme.MemeRealtimeData;

/**
 * Created by furukawanobuyuki on 2016/12/31.
 */

public class MemePayload {
    private static String TAG = MemePayload.class.getSimpleName();

    private MemeRealtimeData memeRealtimeData;
    public MemePayload(MemeRealtimeData memeRealtimeData){
        this.memeRealtimeData = memeRealtimeData;
    }

    public byte[] toJsonByteArray(){
        Gson gson = new Gson();
        return gson.toJson(this.memeRealtimeData).getBytes();
    }
}
