package pam.poluxion.models;

import android.util.Log;

import pam.poluxion.BuildConfig;
import pam.poluxion.data.JsonParser;

public class LocalData {
    private  static final String TAG = "LocalData";

    //private static final String AccApi = BuildConfig.AccuWeatherApiKey;
    private static final String AqicnApi = BuildConfig.AqicnApiKey;
    private JsonParser parser;
    private String searchUrl;

    public LocalData(String locality) {
        searchUrl = "https://api.waqi.info/feed/"+locality+"/?token="+AqicnApi;
        getData();

        Log.e(TAG,"Successfully created");
    }

    private void getData() {
        //searchUrl = "https://api.waqi.info/feed/Melbourne/?token="+AqicnApi;
        parser = new JsonParser(searchUrl);
        parser.execute();
        parser = null;
    }
}
