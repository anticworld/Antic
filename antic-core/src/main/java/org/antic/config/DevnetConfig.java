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

public class DevnetConfig extends AbstractConfig {

    public DevnetConfig(String dataDir) {
        super(dataDir, Network.DEVNET, Constants.DEVNET_VERSION);

        this.netMaxInboundConnectionsPerIp = Integer.MAX_VALUE;

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
}
