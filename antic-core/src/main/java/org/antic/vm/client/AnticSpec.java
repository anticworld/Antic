/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.vm.client;

import org.ethereum.vm.chainspec.ConstantinopleSpec;
import org.ethereum.vm.chainspec.PrecompiledContracts;

public class AnticSpec extends ConstantinopleSpec {

    private static final PrecompiledContracts precompiledContracts = new AnticPrecompiledContracts();

    @Override
    public PrecompiledContracts getPrecompiledContracts() {
        return precompiledContracts;
    }
}
