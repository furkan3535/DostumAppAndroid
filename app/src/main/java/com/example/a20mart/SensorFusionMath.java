package com.example.a20mart;

public class SensorFusionMath {

    private SensorFusionMath() {
    }

    public static float sum(float[] array) {
        float retval = 0;
        for (int i = 0; i < array.length; i++) {
            retval += array[i];
        }
        return retval;
    }


    public static float norm(float[] array) {
        float retval = 0;
        for (int i = 0; i < array.length; i++) {
            retval += array[i] * array[i];
        }
        return (float) Math.sqrt(retval);
    }

    // Note: only works with 3D vectors.
    public static float dot(float[] a, float[] b) {
        float retval = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        return retval;
    }


}
