package pam.poluxion.models;

import android.util.Log;

public class StepCounter {
    private static final String TAG = "StepCounter";

    private static final int WALK_INSIDE = 0;
    private static final int WALK_OUTSIDE = 1;
    private static final int RUN_INSIDE = 2;
    private static final int RUN_OUTSIDE = 3;

    private int stepsTotal;
    private int stepsWalkInside = 0;
    private int stepsWalkOutside = 0;
    private int stepsRunInside = 0;
    private int stepsRunOutside = 0;

    public StepCounter() {
        Log.e(TAG,"Successfully created");
    }

    public void countTotal(int status) {
        switch (status) {
            case WALK_INSIDE: stepsWalkInside++; break;
            case WALK_OUTSIDE: stepsWalkOutside++; break;
            case RUN_INSIDE: stepsRunInside++; break;
            case RUN_OUTSIDE: stepsRunOutside++; break;
        }
        stepsTotal = stepsWalkInside + stepsWalkOutside + stepsRunInside + stepsRunOutside;
    }

    public int getSteps() {
        stepsTotal = stepsWalkInside + stepsWalkOutside + stepsRunInside + stepsRunOutside;
        return stepsTotal;
    }

    public int getStepsWalkInside() {
        return stepsWalkInside;
    }
    public void setStepsWalkInside(int stepsWalkInside) {
        this.stepsWalkInside = stepsWalkInside;
    }

    public int getStepsWalkOutside() {
        return stepsWalkOutside;
    }
    public void setStepsWalkOutside(int stepsWalkOutside) {
        this.stepsWalkOutside = stepsWalkOutside;
    }

    public int getStepsRunInside() {
        return stepsRunInside;
    }
    public void setStepsRunInside(int stepsRunInside) {
        this.stepsRunInside = stepsRunInside;
    }

    public int getStepsRunOutside() {
        return stepsRunOutside;
    }
    public void setStepsRunOutside(int stepsRunOutside) {
        this.stepsRunOutside = stepsRunOutside;
    }
}
