/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.net.msg.p2p.handshake.v1;

import org.antic.Network;
import org.antic.crypto.Key;
import org.antic.net.msg.MessageCode;

public class WorldMessage extends HandshakeMessage {

    /**
     * Create a WORLD message.
     */
    public WorldMessage(Network network, short networkVersion, String peerId, String ip, int port,
            String clientId, long latestBlockNumber, Key coinbase) {
        super(MessageCode.WORLD, null,
                network, networkVersion, peerId, ip, port, clientId, latestBlockNumber, coinbase);
    }

    /**
     * Parse a WORLD message from byte array.
     *
     * @param body
     */
    public WorldMessage(byte[] body) {
        super(MessageCode.WORLD, null, body);
    }

    @Override
    public String toString() {
        return "WorldMessage [peer=" + peer + "]";
    }
}