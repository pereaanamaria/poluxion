package pam.poluxion.models;

import java.text.DecimalFormat;

public class AirData {

    private static final String TAG = "AirData";
    private static final DecimalFormat df1 = new DecimalFormat("#.0");

    private int AQI;
    private double pressure, temperature;
    private String unitMeasurement = null;

    public void setAQI(int AQI) {this.AQI = AQI;}
    public int getAQI() {return AQI;}

    public void setTemperature(double temperature) {this.temperature = temperature;}
    public double getTemperature() {return temperature;}

    public void setPressure(double pressure) {this.pressure = pressure;}
    public double getPressure() {return pressure;}

    public String getPolluant(String iaqiType, double iaqiValue) {
        String finalFormat = "";
        if(unitMeasurement.equals("μg/m³")) {
            switch(iaqiType) {
                case "no2" :
                case "so2" :
                case "nh3" :
                case "co" :
                case "co2" :
                case "o3" :
                case "pb" :
                    finalFormat = df1.format(fromPpbToUgm3(iaqiType, iaqiValue)); break;
                default : finalFormat = df1.format(iaqiValue);
            }
        }
        if(unitMeasurement.equals("ppb")) {
            finalFormat = df1.format(iaqiValue);
        }
        return finalFormat;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        if (unitMeasurement.equals("ugm3")) {
            this.unitMeasurement = "μg/m³";
        } else {
            this.unitMeasurement = unitMeasurement;
        }
    }

    public String getUnitMeasurement() {
        return unitMeasurement;
    }

    public String getUnitMeasurement(String iaqiType) {
        switch(iaqiType) {
            case "pm10" :
            case "pm25" :
            case "pm1" :
            case "voc" :
                return "μg/m³";
            default:
                return unitMeasurement;
        }
    }

    private double fromPpbToUgm3(String iaqiType, double val) {
        return val / getConstant(iaqiType);
    }

    private double fromUgm3ToPpb(String iaqiType, double val) {
        return val * getConstant(iaqiType);
    }

    private double getConstant(String M) {
        double molarWeight = getMolarWeight(M);
        return (0.082057338 * (273.15 + temperature)) / molarWeight;
    }

    public double getMolarWeight(String M) {
        switch (M) {
            case "co" : return 28.0;
            case "o3" : return 48.0;
            case "so2" : return 64.0;
            case "no2" : return 46.0;
            case "nh3" : return 17.0;
            case "pb" : return 44.0;
            case "co2" : return 207.0;
        }
        return -1.0;
    }
}
