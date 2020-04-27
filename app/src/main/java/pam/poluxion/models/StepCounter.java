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
            case WALK_INSIDE: stepsWalkInside++;
            case WALK_OUTSIDE: stepsWalkOutside++;
            case RUN_INSIDE: stepsRunInside++;
            case RUN_OUTSIDE: stepsRunOutside++;
        }
        stepsTotal = stepsWalkInside + stepsWalkOutside + stepsRunInside + stepsRunOutside;
    }

    public int getSteps() {
        return stepsTotal;
    }

    public int getStepsWalkInside() {
        return stepsWalkInside;
    }

    public int getStepsWalkOutside() {
        return stepsWalkOutside;
    }

    public int getStepsRunInside() {
        return stepsRunInside;
    }

    public int getStepsRunOutside() {
        return stepsRunOutside;
    }
}
