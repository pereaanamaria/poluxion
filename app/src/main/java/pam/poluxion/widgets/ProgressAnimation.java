package pam.poluxion.widgets;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ProgressAnimation extends Animation {
    private ArcProgress arc;
    private int from;
    private int to;

    public ProgressAnimation(ArcProgress arc, int from, int to) {
        super();
        this.arc = arc;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        arc.setProgress((int) value);
    }
}
