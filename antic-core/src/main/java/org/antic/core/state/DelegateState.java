/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.core.state;

import java.util.List;
import java.util.Map;

import org.antic.core.Amount;
import org.antic.util.ByteArray;

public interface DelegateState {
    /**
     * Registers a delegate.
     * 
     * @param address
     * @param name
     * @param registeredAt
     * @return
     */
    boolean register(byte[] address, byte[] name, long registeredAt);

    /**
     * Registers a delegate.
     * 
     * @param address
     * @param name
     * @return
     */
    boolean register(byte[] address, byte[] name);

    /**
     * Adds vote to a delegate.
     * 
     * @param voter
     * @param delegate
     * @param value
     * 
     * @return
     */
    boolean vote(byte[] voter, byte[] delegate, Amount value);

    /**
     * Removes vote of a delegate.
     * 
     * @param voter
     * @param delegate
     * @param value
     * 
     * @return
     */
    boolean unvote(byte[] voter, byte[] delegate, Amount value);

    /**
     * Returns vote that one voter has given to the specified delegate.
     * 
     * @param voter
     * @param delegate
     * @return
     */
    Amount getVote(byte[] voter, byte[] delegate);

    /**
     * Returns all the votes for one delegate.
     *
     * @param delegate
     * @return
     */
    Map<ByteArray, Amount> getVotes(byte[] delegate);

    /**
     * Retrieves delegate by its name.
     * 
     * @param name
     * @return
     */
    Delegate getDelegateByName(byte[] name);

    /**
     * Retrieves delegate by its address.
     * 
     * @param address
     * @return
     */
    Delegate getDelegateByAddress(byte[] address);

    /**
     * Returns all delegates.
     * 
     * @return
     */
    List<Delegate> getDelegates();

    /**
     * Returns a snapshot and starts tracking updates.
     */
    DelegateState track();

    /**
     * Commits all updates since last snapshot.
     */
    void commit();

    /**
     * Clone this DelegateState, including all the uncommitted changes.
     */
    DelegateState clone();

    /**
     * Reverts all updates since last snapshot.
     */
    void rollback();
}
