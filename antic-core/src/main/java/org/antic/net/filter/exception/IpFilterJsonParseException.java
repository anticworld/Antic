/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.net.filter.exception;

public class IpFilterJsonParseException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public IpFilterJsonParseException() {
    }

    public IpFilterJsonParseException(String s) {
        super(s);
    }

    public IpFilterJsonParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public IpFilterJsonParseException(Throwable cause) {
        super(cause);
    }
}
