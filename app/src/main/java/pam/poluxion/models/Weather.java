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
    private String searchUrl;

    private int AQI, temperature;
    private double pressure, NO2, O3, PM10, SO2, PM25, VOC, PM1, CO, CO2, Pb, NH3;

    public Weather(String locality, String country, Context context) {
        this.locality = locality;
        this.country = country;

        FBHelper = new FirebaseHelper(this, context);

        searchUrl = "https://api.waqi.info/feed/"+locality+"/?token="+AqicnApi;
        //searchUrl = "https://api.waqi.info/feed/Melbourne/?token="+AqicnApi;
    }

    public void getAQIData(){
        //String searchKeyUrl = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey="+AccApi+"&q="+locality+"%2C"+country;
        //String searchCurrentConditions = "http://dataservice.accuweather.com/currentconditions/v1/"+key+"?apikey="+AccApi+"&details=true";
        //String searchAQI = "http://dataservice.accuweather.com/indices/v1/daily/1day/"+key+"/-10?apikey="+AccApi+"&details=true";
        parser = new JsonParser(searchUrl,"AQI", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getPressureData() {
        parser = new JsonParser(searchUrl,"pressure", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getTemperatureData() {
        parser = new JsonParser(searchUrl,"temperature", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getNO2Data() {
        parser = new JsonParser(searchUrl,"NO2", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getO3Data() {
        parser = new JsonParser(searchUrl,"O3", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getPM10Data() {
        parser = new JsonParser(searchUrl,"PM10", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getSO2Data() {
        parser = new JsonParser(searchUrl,"SO2", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getPM25Data() {
        parser = new JsonParser(searchUrl,"PM25", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getPM1Data() {
        parser = new JsonParser(searchUrl,"PM1", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getNH3Data() {
        parser = new JsonParser(searchUrl,"NH3", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getCOData() {
        parser = new JsonParser(searchUrl,"CO", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getCO2Data() {
        parser = new JsonParser(searchUrl,"CO2", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getVOCData() {
        parser = new JsonParser(searchUrl,"VOC", FBHelper);
        parser.execute();
        parser = null;
    }

    public void getPbData() {
        parser = new JsonParser(searchUrl,"Pb", FBHelper);
        parser.execute();
        parser = null;
    }

    public void setAQI(int AQI) {
        this.AQI = AQI;
    }
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    public void setPressure(double pressure) {
        this.pressure = pressure;
    }
    public void setPM10(double PM10) {
        this.PM10 = PM10;
    }
    public void setPM25(double PM25) {
        this.PM25 = PM25;
    }
    public void setPM1(double PM1) {
        this.PM1 = PM1;
    }
    public void setCO(double CO) {
        this.CO = CO;
    }
    public void setCO2(double CO2) {
        this.CO2 = CO2;
    }
    public void setNO2(double NO2) {
        this.NO2 = NO2;
    }
    public void setNH3(double NH3) {
        this.NH3 = NH3;
    }
    public void setO3(double o3) {
        O3 = o3;
    }
    public void setSO2(double SO2) {
        this.SO2 = SO2;
    }
    public void setVOC(double VOC) {
        this.VOC = VOC;
    }
    public void setPb(double pb) {
        Pb = pb;
    }

    public int getAQI() {
        return AQI;
    }
    public double getPressure() {
        return pressure;
    }
    public int getTemperature() {
        return temperature;
    }
    public double getPM10() {
        return PM10;
    }
    public double getPM25() {
        return PM25;
    }
    public double getPM1() {
        return PM1;
    }
    public double getCO() {
        return CO;
    }
    public double getCO2() {
        return CO2;
    }
    public double getNO2() {
        return NO2;
    }
    public double getNH3() {
        return NH3;
    }
    public double getO3() {
        return O3;
    }
    public double getSO2() {
        return SO2;
    }
    public double getPb() {
        return Pb;
    }
    public double getVOC() {
        return VOC;
    }
}
