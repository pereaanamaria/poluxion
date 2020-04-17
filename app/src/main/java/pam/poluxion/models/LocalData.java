package pam.poluxion.models;

import pam.poluxion.BuildConfig;
import pam.poluxion.helper.JsonParser;

public class LocalData {

    //private static final String AccApi = BuildConfig.AccuWeatherApiKey;
    private static final String AqicnApi = BuildConfig.AqicnApiKey;
    private JsonParser parser;
    private String searchUrl;

    public LocalData() {
        searchUrl = "https://api.waqi.info/feed/here/?token="+AqicnApi;
        getData();
    }

    private void getData() {
        //searchUrl = "https://api.waqi.info/feed/Melbourne/?token="+AqicnApi;
        parser = new JsonParser(searchUrl);
        parser.execute();
        parser = null;
    }
}
