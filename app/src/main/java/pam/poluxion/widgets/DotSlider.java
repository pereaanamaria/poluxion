package pam.poluxion.widgets;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import pam.poluxion.R;

public class DotSlider {

    private Context context;
    private int width;
    private LinearLayout sliderDots;
    private int[] layouts = {R.layout.activity_settings,R.layout.activity_main,R.layout.activity_tracker};

    public DotSlider(Context context, int screenWidth, LinearLayout linearLayout, int currentPosition) {
        this.context = context;
        this.width = screenWidth;
        this.sliderDots = linearLayout;

        createDots(currentPosition);
    }

    private void createDots(int currentPosition) {
        ImageView[] dots = new ImageView[layouts.length];

        for(int i=0; i<layouts.length; i++) {
            dots[i] = new ImageView(context);
            int padding = (width-50)/(layouts.length*layouts.length);
            dots[i].setPadding(padding,0,padding,0);
            if(i == currentPosition) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context,R.drawable.inactive_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);
            sliderDots.addView(dots[i],params);
        }
    }
}
