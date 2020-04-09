package pam.poluxion.models;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import pam.poluxion.BuildConfig;
import pam.poluxion.MainActivity;
import pam.poluxion.widgets.ProgressAnimation;

public class Weather {

    private String locality, country;
    private static final String AccApi = BuildConfig.AccuWeatherApiKey;
    private static final String AqicnApi = BuildConfig.AqicnApiKey;
    private JsonParser parser;
    private FirebaseHelper FBHelper;
    private String dataParser;
    private String searchUrl;

    public String PM10, PM25, PM1, NO2, NH3, CO, CO2, O3, SO2, VOC, Pb;
    private int AQI, temperature;
    private double pressure;

    public Weather(String locality, String country) {
        this.locality = locality;
        this.country = country;

        FBHelper = new FirebaseHelper(this);

        searchUrl = "https://api.waqi.info/feed/"+locality+"/?token="+AqicnApi;
    }

    public void getAQIData(){
        //String searchKeyUrl = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey="+AccApi+"&q="+locality+"%2C"+country;

        parser = new JsonParser(searchUrl,"AQI",this, FBHelper);
        parser.execute();

        //String searchCurrentConditions = "http://dataservice.accuweather.com/currentconditions/v1/"+key+"?apikey="+AccApi+"&details=true";
        //String searchAQI = "http://dataservice.accuweather.com/indices/v1/daily/1day/"+key+"/-10?apikey="+AccApi+"&details=true";

        parser = null;
    }

    public void getPressureData() {
        parser = new JsonParser(searchUrl,"pressure",this, FBHelper);
        parser.execute();
        parser = null;
    }

    public void getTemperatureData() {
        parser = new JsonParser(searchUrl,"temperature",this, FBHelper);
        parser.execute();
        parser = null;
    }

    public void setAQI(int aqi) {
        this.AQI = aqi;
    }
    public int getAQI() { return AQI; }

    public void setPressure(double pressure) { this.pressure = pressure; }
    public double getPressure() { return pressure; }

    public void setTemperature(int temperature) { this.temperature = temperature; }
    public int getTemperature() { return temperature; }
}
