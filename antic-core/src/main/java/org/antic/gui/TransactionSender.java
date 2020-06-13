/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.gui;

import org.antic.Kernel;
import org.antic.Network;
import org.antic.core.Amount;
import org.antic.core.PendingManager;
import org.antic.core.Transaction;
import org.antic.core.TransactionType;
import org.antic.gui.model.WalletAccount;
import org.antic.util.TimeUtil;

public class TransactionSender {

    public static PendingManager.ProcessingResult send(Kernel kernel, WalletAccount account, TransactionType type,
                                                       byte[] to, Amount value, Amount fee, byte[] data) {
        return send(kernel, account, type, to, value, fee, data, 0, Amount.ZERO);
    }

    public static PendingManager.ProcessingResult send(Kernel kernel, WalletAccount account, TransactionType type,
                                                       byte[] to, Amount value, Amount fee, byte[] data, long gas, Amount gasPrice) {
        PendingManager pendingMgr = kernel.getPendingManager();

        Network network = kernel.getConfig().network();
        byte[] from = account.getKey().toAddress();
        long nonce = pendingMgr.getNonce(from);
        long timestamp = TimeUtil.currentTimeMillis();
        Transaction tx = new Transaction(network, type, to, value, fee, nonce, timestamp, data, gas, gasPrice);
        tx.sign(account.getKey());

        return pendingMgr.addTransactionSync(tx);
    }
}
