package pam.poluxion.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;

import pam.poluxion.helper.FirebaseHelper;

public class JsonParser extends AsyncTask<Void, Void, JSONObject> {

    private String urlStr;
    private String whatToGet;
    private FirebaseHelper FBHelper;

    public JsonParser (String url, String whatToGet, FirebaseHelper FBHelper) {
        this.urlStr = url;
        this.whatToGet = whatToGet;
        this.FBHelper = FBHelper;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            return new JSONObject(stringBuffer.toString());
        }
        catch(Exception ex)
        {
            Log.e("App", "yourDataTask", ex);
            return null;
        }
        finally
        {
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    protected void onPostExecute(JSONObject  result) {
        if(result != null)
        {
            try {
                switch (whatToGet) {
                    case "AQI":
                        JSONObject obj = new JSONObject(result.getString("data"));
                        String str = obj.get("aqi").toString();
                        FBHelper.inputAQI(str);
                        Log.e("App", "AQI = " + str);
                        break;
                    case "pressure":
                        obj = new JSONObject(result.getString("data"));
                        JSONObject objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("p").toString());
                        str = objIndex.get("v").toString();
                        FBHelper.inputPressure(str);
                        Log.e("App", "Pressure = " + str);
                        break;
                    case "temperature":
                        obj = new JSONObject(result.getString("data"));
                        objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("t").toString());
                        str = objIndex.get("v").toString();
                        FBHelper.inputTemperature(str);
                        Log.e("App", "Temperature = " + str);
                        break;
                    case "NO2":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("no2").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputNO2(str);
                        Log.e("App", "NO2 = " + str);
                        break;
                    case "O3":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("o3").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputO3(str);
                        Log.e("App", "O3 = " + str);
                        break;
                    case "PM10":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("pm10").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputPM10(str);
                        Log.e("App", "PM10 = " + str);
                        break;
                    case "SO2":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("so2").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputSO2(str);
                        Log.e("App", "SO2 = " + str);
                        break;
                    case "PM25":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("pm25").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputPM25(str);
                        Log.e("App", "PM25 = " + str);
                        break;
                    case "PM1":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("pm1").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputPM1(str);
                        Log.e("App", "PM1 = " + str);
                        break;
                    case "NH3":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("nh3").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputNH3(str);
                        Log.e("App", "NH3 = " + str);
                        break;
                    case "CO":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("co").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputCO(str);
                        Log.e("App", "CO = " + str);
                        break;
                    case "CO2":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("co2").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputCO2(str);
                        Log.e("App", "CO2 = " + str);
                        break;
                    case "VOC":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("voc").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputVOC(str);
                        Log.e("App", "VOC = " + str);
                        break;
                    case "Pb":
                        obj = new JSONObject(result.getString("data"));
                        try{
                            objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get("pb").toString());
                            str = objIndex.get("v").toString();
                        } catch(Exception e) {
                            str = null;
                        }
                        FBHelper.inputPb(str);
                        Log.e("App", "Pb = " + str);
                        break;
                    default: Log.e("App", "Unknown information");
                }
            } catch (JSONException ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }
}
