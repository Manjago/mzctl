package com.temnenkov.mzctl.util;

public final class SimpleMath {
    private SimpleMath() {
        throw new UnsupportedOperationException("Do not instantiate by reflection!");
    }

    public static int pow(int digit, int pow) {
        SimplePreconditions.checkState(pow >= 0, "pow must be non-negative");
        int result = 1;
        for(int i=0; i< pow; ++i) {
            result *= digit;
        }
        return result;
    }

}
