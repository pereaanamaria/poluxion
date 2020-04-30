package pam.poluxion.models;

import java.util.HashMap;
import java.util.Map;

public class AirData {
    private int AQI, temperature;
    private double pressure;
    private Map<String,Double> polluants;
    private String unitMeasurement = "μg/m³";

    public void setAQI(int AQI) {this.AQI = AQI;}
    public int getAQI() {return AQI;}

    public void setTemperature(int temperature) {this.temperature = temperature;}
    public int getTemperature() {return temperature;}

    public void setPressure(double pressure) {this.pressure = pressure;}
    public double getPressure() {return pressure;}

    public void setPolluants(Map<String, Double> polluants) {this.polluants = polluants;}
    public double getPolluant(String str) {return polluants.get(str);}

    public void setUnitMeasurement(String unitMeasurement) {this.unitMeasurement = unitMeasurement;}
    public String getUnitMeasurement() {return unitMeasurement;}
}
