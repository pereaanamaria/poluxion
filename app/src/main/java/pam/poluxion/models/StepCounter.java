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
    private boolean walking = true;

    public StepCounter() {
        Log.e(TAG,"Successfully created");
    }

    public void countTotal(int status) {
        switch (status) {
            case WALK_INSIDE: walking = true; stepsWalkInside++; break;
            case WALK_OUTSIDE: walking = true; stepsWalkOutside++; break;
            case RUN_INSIDE: walking = false; stepsRunInside++; break;
            case RUN_OUTSIDE: walking = false; stepsRunOutside++; break;
            default: Log.e(TAG,"Unknown status : " + status);
        }
    }

    public int getSteps() {
        return stepsWalkInside + stepsWalkOutside + stepsRunInside + stepsRunOutside;
    }

    public int getWalkMin() {return (stepsWalkInside + stepsWalkOutside) / 100;}
    public int getRunMin() {return (stepsRunInside + stepsRunOutside) / 150;}

    public void setPmConcentration(double pmConcentration) {this.pmConcentration = pmConcentration;}
    public double getIntakeDose() {
        double temp = 25 * (pmConcentration * stepsWalkOutside + 8.7 * stepsWalkInside) / 100 +
                45 * (pmConcentration * stepsRunOutside + 8.7 * stepsRunInside) / 150;
        return temp / 1000;
    }

    int getStepsPerMin() {
        if(walking) {
            return 100;
        } else {
            return 150;
        }
    }

    double getAverageSpeed() {
        if(walking) {
            return 1.3888;  // 5 km/h ~= 1.38 m/s
        } else {
            return 6.6666;  // 24 km/h ~= 6.66 m/s
        }
    }

    public int getStepsWalkInside() {return stepsWalkInside;}
    public void setStepsWalkInside(int stepsWalkInside) {this.stepsWalkInside = stepsWalkInside;}

    public int getStepsWalkOutside() {return stepsWalkOutside;}
    public void setStepsWalkOutside(int stepsWalkOutside) {this.stepsWalkOutside = stepsWalkOutside;}

    public int getStepsRunInside() {return stepsRunInside;}
    public void setStepsRunInside(int stepsRunInside) {this.stepsRunInside = stepsRunInside;}

    public int getStepsRunOutside() {return stepsRunOutside;}
    public void setStepsRunOutside(int stepsRunOutside) {this.stepsRunOutside = stepsRunOutside;}

    public boolean isIndoor() {return indoor;}
    public void setIndoor(boolean indoor) {this.indoor = indoor;}
}
