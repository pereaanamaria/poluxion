package pam.poluxion.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Response;

import pam.poluxion.MainActivity;

public class JsonParser extends AsyncTask<Void, Void, JSONObject> {

    private String urlStr;
    private String data = "";
    private String param, otherParam;
    private String indexType = null;
    private String dataParsed = "";
    private Weather weather;
    private TextView view;

    private Context context;


    public JsonParser (String url, Weather weather, Context context, TextView view, String param, String otherParam) {
        this.urlStr = url;
        this.weather = weather;
        this.param = param;
        this.otherParam = otherParam;

        this.context = context;
        this.view = view;
    }

    public JsonParser (String url, Weather weather, Context context, TextView view, String param, String otherParam, String indexType) {
        this.urlStr = url;
        this.weather = weather;
        this.param = param;
        this.otherParam = otherParam;
        this.indexType = indexType;

        this.context = context;
        this.view = view;
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
        /*super.onPostExecute(result);

        //weather.setData(dataParsed);
        Toast.makeText(context, dataParsed, Toast.LENGTH_SHORT).show();*/
        if(result != null)
        {
            try {
                JSONObject obj = new JSONObject(result.getString(param));
                //weather.setData(obj.get("aqi").toString());
                if(indexType != null){
                    JSONObject objIndex = new JSONObject((new JSONObject(obj.get(otherParam).toString())).get(indexType).toString());
                    view.setText(objIndex.get("v").toString() + " ");
                    Log.e("App", param + "-" + otherParam + "-" + indexType + ": " + obj.get(otherParam));
                } else {
                    view.setText(obj.get(otherParam).toString());
                    Log.e("App", param + "-" + otherParam + ": " + obj.get(otherParam));
                }
            } catch (JSONException ex) {
                Log.e("App", "Failure", ex);
            }
        }

    }
}
