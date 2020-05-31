package pam.poluxion.data;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import pam.poluxion.BuildConfig;

public class LocalData extends AsyncTask<Void, Void, JSONObject> {
    private  static final String TAG = "LocalData";
    private static final String AqicnApi = BuildConfig.AqicnApiKey;
    private String searchUrl;

    //creates API URL
    public LocalData(String locality) {
        searchUrl = "https://api.waqi.info/feed/"+locality+"/?token="+AqicnApi;

        Log.e(TAG,"Successfully created");
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(searchUrl);
            URLConnection urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            return new JSONObject(stringBuffer.toString());
        } catch (Exception ex) {
            Log.e(TAG, "yourDataTask", ex);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (result != null) {
            //get status from API
            try {
                String status = result.getString("status");
                if (status.equals("error")) {
                    searchUrl = "https://api.waqi.info/feed/here/?token=" + AqicnApi;
                }

                getData();
            } catch (Exception e) {
                Log.e(TAG, "Failed connection to API server", e);
            }
        }
    }

    //gets data from API
    private void getData() {
        JsonParser parser = new JsonParser(searchUrl);
        parser.execute();
    }
}
