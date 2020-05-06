package pam.poluxion.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import pam.poluxion.MainActivity;
import pam.poluxion.widgets.ProgressAnimation;

public class JsonParser extends AsyncTask<Void, Void, JSONObject> {

    private static final String TAG = "JsonParser";

    private static final int colorAccent = Color.rgb(255,255,255);
    private static final int colorPrimary = Color.rgb(142, 171, 140);
    private static final int colorPrimaryDarker = Color.rgb(75,89,73);

    private int AQI;
    private String[] iaqiDataTypes = {"pm10", "pm1", "pm25", "no2", "so2", "nh3", "co", "co2", "o3", "pb", "voc", "p", "t"};
    private Map<String, Double> iaqiData = new HashMap<>();

    private String urlStr;
    @SuppressLint("StaticFieldLeak")
    private Button clicked;

    JsonParser(String urlStr) {
        this.urlStr = urlStr;
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
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            return new JSONObject(stringBuffer.toString());
        } catch (Exception ex) {
            Log.e("App", "yourDataTask", ex);
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
            try {
                JSONObject obj = new JSONObject(result.getString("data"));
                String str = obj.get("aqi").toString();
                postAQI(str);
            } catch (Exception e) {
                Log.e(TAG, "No AQI", e);
            }

            for (String iaqiDataType : iaqiDataTypes) {
                try {
                    JSONObject obj = new JSONObject(result.getString("data"));
                    String str;
                    try {
                        JSONObject objIndex = new JSONObject((new JSONObject(obj.get("iaqi").toString())).get(iaqiDataType).toString());
                        str = objIndex.get("v").toString();
                    } catch (Exception e) {
                        str = null;
                    }
                    postIAQI(iaqiDataType, str);
                } catch (Exception e) {
                    Log.e(TAG, "No " + iaqiDataType);
                }
            }

            GeneralClass.getAirData().setPolluants(iaqiData);
        }
    }

    private void iaqiButtonListener(final String iaqiType, String iaqiValue, final Button btn) {
        try {
            if (iaqiValue != null) {
                double iaqi = Double.parseDouble(iaqiValue);
                iaqiData.put(iaqiType, iaqi);
                btn.setVisibility(View.VISIBLE);
                setButtonNotClicked(btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.measurementTV.setText(iaqiData.get(iaqiType) + " ");
                        MainActivity.unitsTV.setText(GeneralClass.getAirData().getUnitMeasurement(iaqiType));
                        setButtonNotClicked(clicked);
                        clicked = btn;
                        setButtonClicked(clicked);
                    }
                });
                if (iaqiType.equals("pm10")) {
                    MainActivity.measurementTV.setText(iaqiData.get(iaqiType) + " ");
                    MainActivity.unitsTV.setText(GeneralClass.getAirData().getUnitMeasurement(iaqiType));
                    clicked = btn;
                    setButtonClicked(clicked);
                }
            } else {
                iaqiData.put(iaqiType, 0.0);
                MainActivity.btnSlider.removeView(btn);
            }
        } catch (Exception e) {
            Log.e(TAG, iaqiType + " does not exist.");
        }
    }

    private void getAQIPercentage() {
        String status, status2 = "";
        int progress;
        if (AQI <= 50) {
            status = "Good";
        } else if (AQI <= 100) {
            status = "Moderate";
        } else if (AQI <= 150) {
            status = "Unhealthy";
            status2 += "Active children and adults, and people with respiratory disease, such as asthma, should limit prolonged outdoor exertion.";
        } else if (AQI <= 200) {
            status = "Unhealthy";
            status2 += "Active children and adults, and people with respiratory disease, such as asthma, should avoid prolonged outdoor exertion.";
        } else if (AQI <= 300) {
            status = "Very Unhealthy";
            status2 += "Active children and adults, and people with respiratory disease, such as asthma, should avoid all outdoor exertion.";
        } else {
            status = "Hazardous";
            status2 += "Everyone should avoid all outdoor exertion.";
        }

        double progr = AQI / 3.5;
        progress = 100 - (int) progr;

        ProgressAnimation anim = new ProgressAnimation(MainActivity.arcProgressBar, 0, progress);
        anim.setDuration(1000);
        MainActivity.arcProgressBar.startAnimation(anim);

        MainActivity.arcProgressBar.setProgress(progress);
        MainActivity.arcProgressBar.setBottomText(status);
        MainActivity.arcProgressTV.setText(status2);
    }

    private void postAQI(String aqi) {
        AQI = Integer.parseInt(aqi);
        MainActivity.nrAqiTV.setText(aqi);
        getAQIPercentage();
        GeneralClass.getAirData().setAQI(AQI);
    }

    private void postPressure(String pressure) {
        double Pressure = Double.parseDouble(pressure);
        MainActivity.pressureTV.setText(pressure + " ");
        GeneralClass.getAirData().setPressure(Pressure);
    }

    private void postTemperature(String temperature) {
        double temp = Double.parseDouble(temperature);
        GeneralClass.getAirData().setTemperature(temp);
        int Temperature = (int) Math.floor(temp);
        MainActivity.temperatureTV.setText(Temperature + " ");
    }

    private void postIAQI(String iaqiType, String iaqiValue) {
        Button btn = null;
        switch (iaqiType) {
            case "pm10": btn = MainActivity.pm10Btn; break;
            case "pm25": btn = MainActivity.pm25Btn; break;
            case "pm1": btn = MainActivity.pm1Btn; break;
            case "no2": btn = MainActivity.no2Btn; break;
            case "so2": btn = MainActivity.so2Btn; break;
            case "o3": btn = MainActivity.o3Btn; break;
            case "co": btn = MainActivity.coBtn; break;
            case "co2": btn = MainActivity.co2Btn; break;
            case "nh3": btn = MainActivity.nh3Btn; break;
            case "pb": btn = MainActivity.pbBtn; break;
            case "voc": btn = MainActivity.vocBtn; break;
            case "t": postTemperature(iaqiValue); break;
            case "p": postPressure(iaqiValue); break;
            default: Log.e(TAG, "Unknown IAQI");
        }
        if (btn != null) {
            iaqiButtonListener(iaqiType, iaqiValue, btn);
        }
    }

    private void setButtonClicked(Button btn) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(3,colorPrimary);
        gradientDrawable.setColor(colorAccent);
        btn.setTextColor(colorPrimaryDarker);
        btn.setBackground(gradientDrawable);
    }

    private void setButtonNotClicked(Button btn) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(colorPrimary);
        btn.setTextColor(colorAccent);
        btn.setBackground(gradientDrawable);
    }
}
