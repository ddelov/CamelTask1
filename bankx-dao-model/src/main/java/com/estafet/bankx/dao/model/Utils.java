package com.estafet.bankx.dao.model;

/**
 * Created by Yordan Nalbantov.
 */
public class Utils {
    /**
     * Support for testing that primitive classes are equal but wrap the additional null-notnull logic.
     *
     * @param a   The first value.
     * @param b   The second value.
     * @param <T> A generic to offload to the compiler the class-comparison logic.
     * @return True if both are equal or null.
     */
    public static <T> boolean equality(T a, T b) {
        return ((a == null) && (b == null)) || ((a != null) && a.equals(b));
    }
}