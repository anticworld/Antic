/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.util;

import java.util.Iterator;

public interface ClosableIterator<T> extends Iterator<T> {

    /**
     * Closes the underlying resources for this iterator.
     */
    void close();
}
