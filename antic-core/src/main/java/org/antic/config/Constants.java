/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.config;

import org.antic.crypto.CryptoException;
import org.antic.crypto.Hex;
import org.antic.crypto.Key;

import java.security.spec.InvalidKeySpecException;

public class Constants {

    /**
     * Default data directory.
     */
    public static final String DEFAULT_DATA_DIR = ".";

    /**
     * Network versions.
     */
    public static final short MAINNET_VERSION = 0;
    public static final short TESTNET_VERSION = 0;
    public static final short DEVNET_VERSION = 0;

    /**
     * Name of this client.
     */
    public static final String CLIENT_NAME = "Antic";

    /**
     * Version of this client.
     */
    public static final String CLIENT_VERSION = "1.0.0";

    /**
     * Algorithm name for the 256-bit hash.
     */
    public static final String HASH_ALGORITHM = "BLAKE2B-256";

    /**
     * Name of the config directory.
     */
    public static final String CONFIG_DIR = "config";

    /**
     * Name of the database directory.
     */
    public static final String DATABASE_DIR = "database";

    /**
     * The default IP port for p2p protocol
     */
    public static final int DEFAULT_P2P_PORT = 6128;

    /**
     * The default IP port for RESTful API.
     */
    public static final int DEFAULT_API_PORT = 6358;

    /**
     * The default user agent for HTTP requests.
     */
    public static final String DEFAULT_USER_AGENT = "Mozilla/4.0";

    /**
     * The default connect timeout.
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 4000;

    /**
     * The default read timeout.
     */
    public static final int DEFAULT_READ_TIMEOUT = 4000;

    /**
     * The number of blocks per day.
     */
    public static final long BLOCKS_PER_DAY = 2L * 60L * 24L;

    /**
     * The number of blocks per year.
     */
    public static final long BLOCKS_PER_YEAR = 2L * 60L * 24L * 365L;

    /**
     * The public-private key pair for signing coinbase transactions.
     */
    public static final Key COINBASE_KEY;

    /**
     * Address bytes of {@link this#COINBASE_KEY}. This is stored as a cache to
     * avoid redundant h160 calls.
     */
    public static final byte[] COINBASE_ADDRESS;

    /**
     * The public-private key pair of the genesis validator.
     */
    public static final Key DEVNET_KEY;

    public static final byte[] DELEGATE_BURN_ADDRESS = Hex.decode0x("0x345ed9858fc7d52b4a4b7e7a3c6aef56db51758c");

    static {
        try {
            COINBASE_KEY = new Key(Hex.decode0x(
                    "0x302e020100300506032b6570042204203273fd39556863a446e141ce7b4a05cb83a14ab7861a397bee7d1cfa6e4be540"));
            COINBASE_ADDRESS = COINBASE_KEY.toAddress();
            DEVNET_KEY = new Key(Hex.decode0x(
                    "0x302e020100300506032b6570042204208f474a28afa91a60de288121c18fb10428a80f182ea108a6089ee0579b2f8043"));

        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        }
    }

    private Constants() {
    }
}
