/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.net.msg.p2p;

import org.antic.net.msg.Message;
import org.antic.net.msg.MessageCode;
import org.antic.util.SimpleDecoder;
import org.antic.util.SimpleEncoder;
import org.antic.util.TimeUtil;

public class PongMessage extends Message {

    private final long timestamp;

    /**
     * Create a PONG message.
     */
    public PongMessage() {
        super(MessageCode.PONG, null);

        this.timestamp = TimeUtil.currentTimeMillis();

        SimpleEncoder enc = new SimpleEncoder();
        enc.writeLong(timestamp);
        this.body = enc.toBytes();
    }

    /**
     * Parse a PONG message from byte array.
     * 
     * @param body
     */
    public PongMessage(byte[] body) {
        super(MessageCode.PONG, null);

        SimpleDecoder dec = new SimpleDecoder(body);
        this.timestamp = dec.readLong();

        this.body = body;
    }

    @Override
    public String toString() {
        return "PongMessage [timestamp=" + timestamp + "]";
    }
}
