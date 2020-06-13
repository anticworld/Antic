/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.core;

import java.math.BigInteger;

import static java.util.Arrays.stream;

public enum Unit {
    NANO_ANTIC(0, "nANTIC"),

    MICRO_ANTIC(3, "μANTIC"),

    MILLI_ANTIC(6, "mANTIC"),

    ANTIC(9, "ANTIC"),

    KILO_ANTIC(12, "kANTIC"),

    MEGA_ANTIC(15, "MANTIC");

    public final int exp;
    public final long factor;
    public final String symbol;

    Unit(int exp, String symbol) {
        this.exp = exp;
        this.factor = BigInteger.TEN.pow(exp).longValueExact();
        this.symbol = symbol;
    }

    /**
     * Decode the unit from symbol.
     *
     * @param symbol
     *            the symbol text
     * @return a Unit object if valid; otherwise false
     */
    public static Unit of(String symbol) {
        return stream(values()).filter(v -> v.symbol.equals(symbol)).findAny().orElse(null);
    }
}
