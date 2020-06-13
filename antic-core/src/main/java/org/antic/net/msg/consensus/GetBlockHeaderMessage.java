/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.net.msg.consensus;

import org.antic.net.msg.Message;
import org.antic.net.msg.MessageCode;
import org.antic.util.SimpleDecoder;
import org.antic.util.SimpleEncoder;

public class GetBlockHeaderMessage extends Message {

    private final long number;

    public GetBlockHeaderMessage(long number) {
        super(MessageCode.GET_BLOCK_HEADER, BlockHeaderMessage.class);

        this.number = number;

        SimpleEncoder enc = new SimpleEncoder();
        enc.writeLong(number);
        this.body = enc.toBytes();
    }

    public GetBlockHeaderMessage(byte[] body) {
        super(MessageCode.GET_BLOCK_HEADER, BlockHeaderMessage.class);

        SimpleDecoder dec = new SimpleDecoder(body);
        this.number = dec.readLong();

        this.body = body;
    }

    public long getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "GetBlockHeaderMessage [number=" + number + "]";
    }
}
