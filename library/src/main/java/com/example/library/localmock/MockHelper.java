package com.example.library.localmock;

import java.lang.annotation.Annotation;

public class MockHelper {

    public static boolean isMockOn(Annotation[] annotations) {
        if (annotations == null) {
            return false;
        }

        for (Annotation annotation :
                annotations) {
            if ((annotation instanceof Mock) && ((Mock) annotation).value()) {
                return true;
            }
        }

        return false;
    }
}
