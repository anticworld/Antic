/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.config;

import java.util.Collections;
import java.util.Map;

import org.antic.Network;
import org.antic.core.Fork;

public class TestnetConfig extends AbstractConfig {

    public TestnetConfig(String dataDir) {
        super(dataDir, Network.TESTNET, Constants.TESTNET_VERSION);

        this.forkUniformDistributionEnabled = true;
        this.forkVirtualMachineEnabled = true;
        this.forkVotingPrecompiledUpgradeEnabled = true;
    }

    @Override
    public Map<Long, byte[]> checkpoints() {
        return Collections.emptyMap();
    }

    @Override
    public Map<Fork, Long> manuallyActivatedForks() {
        return Collections.emptyMap();
    }

    /**
     * Testnet maxes out at 10 validators to stop dead validators from breaking
     * concensus
     * 
     * @param number
     * @return
     */
    @Override
    public int getNumberOfValidators(long number) {
        return Math.min(10, super.getNumberOfValidators(number));
    }
}
