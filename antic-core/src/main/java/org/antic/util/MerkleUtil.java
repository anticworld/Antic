/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.util;

import java.util.ArrayList;
import java.util.List;

import org.antic.core.Transaction;
import org.antic.core.TransactionResult;
import org.antic.crypto.Hash;

public class MerkleUtil {

    /**
     * Compute the Merkle root of transactions.
     * 
     * @param txs
     *            transactions
     * @return
     */
    public static byte[] computeTransactionsRoot(List<Transaction> txs) {
        List<byte[]> hashes = new ArrayList<>();
        for (Transaction tx : txs) {
            hashes.add(tx.getHash());
        }
        return new MerkleTree(hashes).getRootHash();
    }

    /**
     * Computes the Merkle root of results.
     * 
     * @param results
     *            transaction results
     * @return
     */
    public static byte[] computeResultsRoot(List<TransactionResult> results) {
        List<byte[]> hashes = new ArrayList<>();
        for (TransactionResult tx : results) {
            hashes.add(Hash.h256(tx.toBytesForMerkle()));
        }
        return new MerkleTree(hashes).getRootHash();
    }

    private MerkleUtil() {
    }
}
