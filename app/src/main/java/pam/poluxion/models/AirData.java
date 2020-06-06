package pam.poluxion.models;

import java.text.DecimalFormat;

public class AirData {
    private static final String TAG = "AirData";
    private static final DecimalFormat df1 = new DecimalFormat("0.0");

    private int AQI;
    private double pressure, temperature;
    private String unitMeasurement = null;

    //AQI getter and setter
    public void setAQI(int AQI) {this.AQI = AQI;}
    public int getAQI() {return AQI;}

    //temperature getter and setter
    public void setTemperature(double temperature) {this.temperature = temperature;}
    public double getTemperature() {return temperature;}

    //pressure getter and setter
    public void setPressure(double pressure) {this.pressure = pressure;}
    public double getPressure() {return pressure;}

    //displays pollutant value based on unit measurement
    public String getPollutant(String iaqiType, double iaqiValue) {
		double val = getPollutantQuantity(iaqiType, iaqiValue);
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
                    finalFormat = df1.format(fromPpbToUgm3(iaqiType, val)); break;
                default : finalFormat = df1.format(val);
            }
        }
        if(unitMeasurement.equals("ppb")) {
            finalFormat = df1.format(val);
        }
        return finalFormat;
    }

    //unit measurement getter and setter
    public void setUnitMeasurement(String unitMeasurement) {
        if (unitMeasurement.equals("ugm3")) {
            this.unitMeasurement = "μg/m³";
        } else {
            this.unitMeasurement = unitMeasurement;
        }
    }
    public String getUnitMeasurement() {return unitMeasurement;}
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
	
	private double getPollutantQuantity(String iaqiType, double iaqiValue) {
        switch(iaqiType) {
            case "pm10":
            case "pm25":
            case "o3":
            case "co":
            case "no2":
            case "so2": {
                double concentrationDelta = getConcentrationDelta(iaqiType, iaqiValue);
                double indexDelta = getIndexDelta(iaqiValue);
                double concentrationMin = getMinConcentration(iaqiType, iaqiValue);
                double indexMin = getMinIndex(iaqiValue);

                double temp = (concentrationDelta / indexDelta) * (iaqiValue - indexMin) + concentrationMin;

                if (iaqiType.equals("co")) {
                    //IAQI calculated based on ppm unit measurement
                    return temp * 1000;  //conversion to ppb
                }
                return temp;
            }
            default:
                return iaqiValue;
        }
	}

    //value conversions based on unit measurements
    private double fromPpbToUgm3(String iaqiType, double val) {return val / getConstant(iaqiType);}
    public double fromUgm3ToPpb(String iaqiType, double val) {return val * getConstant(iaqiType);}

    //creates molar constant for conversion
    private double getConstant(String M) {
        double molarWeight = getMolarWeight(M);
        return (0.082057338 * (273.15 + temperature)) / molarWeight;
    }

    //gets pollutant molar weight
    private double getMolarWeight(String M) {
        switch (M) {
            case "co" : return 28.0;
            case "co2" : return 44.0;
            case "nh3" : return 17.0;
            case "no2" : return 46.0;
            case "o3" : return 48.0;
            case "so2" : return 64.0;
            case "pb" : return 207.0;
        }
        return -1.0;
    }

    private double getMinIndex(double iaqiValue) {
        if (iaqiValue <= 50) {
            return 0;
        } else if (iaqiValue <= 100) {
            return 50;
        } else if (iaqiValue <= 150) {
            return 100;
        } else if (iaqiValue <= 200) {
            return 150;
        } else if (iaqiValue <= 300) {
            return 200;
        } else if (iaqiValue <= 400) {
            return 300;
        }
        return 400;
    }

    private double getIndexDelta(double iaqiValue) {
        if(iaqiValue <= 200) {
            return 50;
        }
        return 100;
    }

    private double getMinConcentration(String iaqiType, double iaqiValue) {
        if (iaqiValue <= 50) {
            return 0;
        } else if (iaqiValue <= 100) {
            switch (iaqiType) {
                case "co" : return 4.5;
                case "no2" : return 54;
                case "o3" :
                case "pm10" :
                    return 55;
                case "pm25" : return 12;
                case "so2" : return 36;
            }
        } else if (iaqiValue <= 150) {
            switch (iaqiType) {
                case "co" : return 9.5;
                case "no2" : return 101;
                case "o3" : return 71;
                case "pm10" : return 155;
                case "pm25" : return 35.5;
                case "so2" : return 76;
            }
        } else if (iaqiValue <= 200) {
            switch (iaqiType) {
                case "co" : return 12.5;
                case "no2" : return 361;
                case "o3" : return 86;
                case "pm10" : return 255;
                case "pm25" : return 55.5;
                case "so2" : return 186;
            }
        } else if (iaqiValue <= 300) {
            switch (iaqiType) {
                case "co" : return 15.5;
                case "no2" : return 650;
                case "o3" : return 106;
                case "pm10" : return 355;
                case "pm25" : return 150.5;
                case "so2" : return 304;
            }
        } else if (iaqiValue <= 400) {
            switch (iaqiType) {
                case "co" : return 30.5;
                case "no2" : return 1250;
                case "o3" : return 405;
                case "pm10" : return 425;
                case "pm25" : return 250.5;
                case "so2" : return 605;
            }
        }
        switch (iaqiType) {
            case "co" : return 40.5;
            case "no2" : return 1650;
            case "o3" :
            case "pm10" :
                return 505;
            case "pm25" : return 350.5;
            case "so2" : return 805;
        }
        return 0;
    }

    private double getConcentrationDelta(String iaqiType, double iaqiValue) {
        if (iaqiValue <= 50) {
            switch (iaqiType) {
                case "co" : return 4.5;
                case "no2" : return 53;
                case "o3" : return 54;
                case "pm10" : return 55;
                case "pm25" : return 12;
                case "so2" : return 36;
            }
        } else if (iaqiValue <= 100) {
            switch (iaqiType) {
                case "co" : return 5;
                case "no2" : return 46;
                case "o3" : return 15;
                case "pm10" : return 100;
                case "pm25" : return 23.5;
                case "so2" : return 40;
            }
        } else if (iaqiValue <= 150) {
            switch (iaqiType) {
                case "co" : return 3;
                case "no2" : return 259;
                case "o3" : return 14;
                case "pm10" : return 100;
                case "pm25" : return 20;
                case "so2" : return 110;
            }
        } else if (iaqiValue <= 200) {
            switch (iaqiType) {
                case "co" : return 3;
                case "no2" : return 288;
                case "o3" : return 19;
                case "pm10" : return 100;
                case "pm25" : return 95;
                case "so2" : return 118;
            }
        } else if (iaqiValue <= 300) {
            switch (iaqiType) {
                case "co" : return 15;
                case "no2" : return 599;
                case "o3" : return 94;
                case "pm10" : return 70;
                case "pm25" : return 100;
                case "so2" : return 301;
            }
        } else if (iaqiValue <= 400) {
            switch (iaqiType) {
                case "co" : return 10;
                case "no2" : return 399;
                case "o3" : return 99;
                case "pm10" : return 80;
                case "pm25" : return 100;
                case "so2" : return 200;
            }
        }
        switch (iaqiType) {
            case "co" : return 10;
            case "no2" : return 399;
            case "o3" : return 99;
            case "pm10" : return 100;
            case "pm25" : return 150;
            case "so2" : return 199;
        }
        return 0;
    }

}
