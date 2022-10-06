package whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Utils;

import android.util.Log;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification {
    public static void SendNotification(String message, String receiver, String notificationKey) throws JSONException {
        Log.i("OneSignalExample", "notificationKey: " + notificationKey);
        JSONObject jsonObject = new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + notificationKey + "']}");

        OneSignal.postNotification(jsonObject,
                new OneSignal.PostNotificationResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                    }

                    @Override
                    public void onFailure(JSONObject response) {
                        Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                    }
                });
    }
}

