/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.vm.client;

import org.ethereum.vm.client.BlockStore;
import org.antic.core.Blockchain;

/**
 * Facade class for Blockchain to Blockstore
 *
 * Eventually we'll want to make blockchain just implement blockstore
 */
public class AnticBlockStore implements BlockStore {
    private final Blockchain blockchain;

    public AnticBlockStore(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    @Override
    public byte[] getBlockHashByNumber(long index) {
        return blockchain.getBlockHeader(index).getHash();
    }
}
