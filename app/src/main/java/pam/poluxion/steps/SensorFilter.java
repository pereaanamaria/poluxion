package pam.poluxion.steps;

class SensorFilter {
    private SensorFilter() {}

    //sums x,y,z coordinates
    static float sum(float[] array) {
        float retval = 0;
        for (float v : array) {
            retval += v;
        }
        return retval;
    }

    //normalization of the vector
    static float norm(float[] array) {
        float retval = 0;
        for (float v : array) {
            retval += v * v;
        }
        return (float) Math.sqrt(retval);
    }

    //finds the intersection of the coordinates
    static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }
}
