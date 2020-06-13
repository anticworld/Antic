/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.vm.client;

import java.math.BigInteger;

import org.antic.core.Amount;
import org.antic.core.Unit;

/**
 * Conversion between ETH and ANTIC. The idea is to make 1 ANTIC = 1 ETH from a
 * smart contract viewpoint.
 */
public class Conversion {

    private static final BigInteger TEN_POW_NINE = BigInteger.TEN.pow(9);

    public static Amount weiToAmount(BigInteger value) {
        BigInteger nanoANTIC = value.divide(TEN_POW_NINE);
        return Amount.of(nanoANTIC.longValue(), Unit.NANO_ANTIC);
    }

    public static BigInteger amountToWei(Amount value) {
        return value.toBigInteger().multiply(TEN_POW_NINE);
    }

    public static BigInteger amountToWei(long nanoANTIC) {
        return BigInteger.valueOf(nanoANTIC).multiply(TEN_POW_NINE);
    }
}
