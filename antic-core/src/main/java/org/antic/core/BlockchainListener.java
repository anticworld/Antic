/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.core;

public interface BlockchainListener {

    /**
     * Callback when a new block was added.
     * 
     * @param block
     */
    void onBlockAdded(Block block);
}
