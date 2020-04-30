package pam.poluxion.steps;

// Will listen to step alerts
public interface StepListener {
    void step(long timeNs);  //the method is implemented in MainActivity
}
