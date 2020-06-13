/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.consensus.exception;

public class AnticBftException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AnticBftException() {
    }

    public AnticBftException(String s) {
        super(s);
    }

    public AnticBftException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AnticBftException(Throwable throwable) {
        super(throwable);
    }

    public AnticBftException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
