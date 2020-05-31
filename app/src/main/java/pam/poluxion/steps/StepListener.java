package pam.poluxion.steps;

//listens to step alerts
public interface StepListener {
    void step(long timeNs);
}
