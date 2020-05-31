package pam.poluxion.models;

import android.util.Log;

public class StepCounter {
    private static final String TAG = "StepCounter";

    private static final int WALK_INSIDE = 0;
    private static final int WALK_OUTSIDE = 1;
    private static final int RUN_INSIDE = 2;
    private static final int RUN_OUTSIDE = 3;

    private double pmConcentration;

    private int stepsWalkInside = 0;
    private int stepsWalkOutside = 0;
    private int stepsRunInside = 0;
    private int stepsRunOutside = 0;

    private boolean indoor = true;

    public StepCounter() {Log.e(TAG,"Successfully created");}

    //counts steps based on activity type and localisation (indoor/outdoor)
    public void countTotal(int status) {
        switch (status) {
            case WALK_INSIDE: stepsWalkInside++; break;
            case WALK_OUTSIDE: stepsWalkOutside++; break;
            case RUN_INSIDE: stepsRunInside++; break;
            case RUN_OUTSIDE: stepsRunOutside++; break;
            default: Log.e(TAG,"Unknown status : " + status);
        }
    }

    //total number of steps
    public int getSteps() {
        return stepsWalkInside + stepsWalkOutside + stepsRunInside + stepsRunOutside;
    }

    //activity minutes for walking/running
    public int getWalkMin() {return (stepsWalkInside + stepsWalkOutside) / 100;}
    public int getRunMin() {return (stepsRunInside + stepsRunOutside) / 160;}

    //pollutant concentration setter
    public void setPmConcentration(double pmConcentration) {this.pmConcentration = pmConcentration;}

    //BPI calculation
    public double getBPI() {
        double temp = 25 * (pmConcentration * stepsWalkOutside + 8.7 * stepsWalkInside) / 100 +
                45 * (pmConcentration * stepsRunOutside + 8.7 * stepsRunInside) / 160;
        return temp / 1000;
    }

    //walk inside steps getter and init
    public int getStepsWalkInside() {return stepsWalkInside;}
    public void setStepsWalkInside(int stepsWalkInside) {this.stepsWalkInside = stepsWalkInside;}

    //walk outside steps getter and init
    public int getStepsWalkOutside() {return stepsWalkOutside;}
    public void setStepsWalkOutside(int stepsWalkOutside) {this.stepsWalkOutside = stepsWalkOutside;}

    //run inside steps getter and init
    public int getStepsRunInside() {return stepsRunInside;}
    public void setStepsRunInside(int stepsRunInside) {this.stepsRunInside = stepsRunInside;}

    //run outside steps getter and init
    public int getStepsRunOutside() {return stepsRunOutside;}
    public void setStepsRunOutside(int stepsRunOutside) {this.stepsRunOutside = stepsRunOutside;}

    //check if user is indoor or outdoor
    public boolean isIndoor() {return indoor;}
    public void setIndoor(boolean indoor) {this.indoor = indoor;}
}
