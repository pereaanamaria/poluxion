package pam.poluxion.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import pam.poluxion.BuildConfig;
import pam.poluxion.MainActivity;

public class Weather {

    private String temperature;
    private String locality, country;
    private static final String AccApi = BuildConfig.AccuWeatherApiKey;
    private static final String AqicnApi = BuildConfig.AqicnApiKey;
    private JsonParser parser;
    private String dataParser;
    private String searchUrl;

    private Context context;

    public Weather(String locality, String country, Context context) {
        this.locality = locality;
        this.country = country;

        this.context = context;

        searchUrl = "https://api.waqi.info/feed/"+locality+"/?token="+AqicnApi;
    }

    public void getAQI(){
        //String searchKeyUrl = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey="+AccApi+"&q="+locality+"%2C"+country;

        parser = new JsonParser(searchUrl, this, context, MainActivity.nrAqiTV,"data","aqi");
        parser.execute();

        //String searchCurrentConditions = "http://dataservice.accuweather.com/currentconditions/v1/"+key+"?apikey="+AccApi+"&details=true";
        //String searchAQI = "http://dataservice.accuweather.com/indices/v1/daily/1day/"+key+"/-10?apikey="+AccApi+"&details=true";

        parser = null;

        Log.e("AppWeather", "Success: " + dataParser);
    }

    public void getPressure() {
        parser = new JsonParser(searchUrl, this, context, MainActivity.pressureTV,"data","iaqi","p");
        parser.execute();
        parser = null;
    }

    public void setData(String buff){
        dataParser = buff;
        Log.e("AppWeatherDataSet", "Success: " + buff);
    }

    public String getTemperature() {
        return temperature;
    }
}
