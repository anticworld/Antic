/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.vm.client;

import java.math.BigInteger;

import org.ethereum.vm.DataWord;
import org.ethereum.vm.client.Repository;
import org.antic.core.state.AccountState;
import org.antic.core.state.DelegateState;

/**
 * Facade class for AccountState -> Repository
 *
 * We will probably want to make AccountState just implement repository but for
 * ease of initial integration, use a facade to limit scope
 */
public class AnticRepository implements Cloneable, Repository {
    private final AccountState accountState;
    private final DelegateState delegateState;

    public AnticRepository(AccountState accountState, DelegateState delegateState) {
        this.accountState = accountState;
        this.delegateState = delegateState;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public DelegateState getDelegateState() {
        return delegateState;
    }

    @Override
    public boolean exists(byte[] address) {
        return accountState.exists(address);
    }

    @Override
    public void createAccount(byte[] address) {
        if (!exists(address)) {
            accountState.setCode(address, new byte[] {});
        }
    }

    @Override
    public void delete(byte[] address) {
        if (exists(address)) {
            accountState.setCode(address, null);
        }
    }

    @Override
    public long increaseNonce(byte[] address) {
        return accountState.increaseNonce(address);
    }

    @Override
    public long setNonce(byte[] address, long nonce) {
        return accountState.setNonce(address, nonce);
    }

    @Override
    public long getNonce(byte[] address) {
        return accountState.getAccount(address).getNonce();
    }

    @Override
    public void saveCode(byte[] address, byte[] code) {
        accountState.setCode(address, code);
    }

    @Override
    public byte[] getCode(byte[] address) {
        return accountState.getCode(address);
    }

    @Override
    public void putStorageRow(byte[] address, DataWord key, DataWord value) {
        accountState.putStorage(address, key.getData(), value.getData());
    }

    @Override
    public DataWord getStorageRow(byte[] address, DataWord key) {
        byte[] data = accountState.getStorage(address, key.getData());
        if (data != null) {
            return DataWord.of(data);
        }
        return null;
    }

    @Override
    public BigInteger getBalance(byte[] address) {
        return Conversion.amountToWei(accountState.getAccount(address).getAvailable());
    }

    @Override
    public BigInteger addBalance(byte[] address, BigInteger value) {
        accountState.adjustAvailable(address, Conversion.weiToAmount(value));
        return value;
    }

    @Override
    public Repository startTracking() {
        return new AnticRepository(accountState.track(), delegateState.track());
    }

    @Override
    public Repository clone() {
        return new AnticRepository(accountState.clone(), delegateState.clone());
    }

    @Override
    public void commit() {
        accountState.commit();
        delegateState.commit();
    }

    @Override
    public void rollback() {
        accountState.rollback();
        delegateState.commit();
    }
}
