package pam.poluxion.data;

import android.util.Log;

import pam.poluxion.BuildConfig;

public class LocalData {
    private  static final String TAG = "LocalData";
    private static final String AqicnApi = BuildConfig.AqicnApiKey;
    private String searchUrl;

    public LocalData(String locality) {
        //searchUrl = "https://api.waqi.info/feed/"+locality+"/?token="+AqicnApi;
        searchUrl = "https://api.waqi.info/feed/here/?token="+AqicnApi;
        getData();

        Log.e(TAG,"Successfully created");
    }

    private void getData() {
        JsonParser parser = new JsonParser(searchUrl);
        parser.execute();
    }
}
