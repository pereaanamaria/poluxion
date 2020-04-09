package pam.poluxion.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;

public class JsonParser extends AsyncTask<Void, Void, JSONObject> {

    private String urlStr;
    private String whatToGet;
    private FirebaseHelper FBHelper;

    public JsonParser (String url, String whatToGet, Weather weather, FirebaseHelper FBHelper) {
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
                        //MainActivity.pressureTV.setText(str + " ");
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
                }
            } catch (JSONException ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }
}
