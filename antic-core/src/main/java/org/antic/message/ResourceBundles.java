/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.message;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This enum encapsulates available resource bundles of messages and an utility
 * function getDefaultBundle for deciding the default locale
 *
 * <p>
 * The locale used is the current value of the default locale for this instance
 * of the Java Virtual Machine.
 * </p>
 */
public enum ResourceBundles {

    GUI_MESSAGES("org/antic/gui/messages"), CLI_MESSAGES("org/antic/cli/messages");

    private final String bundleName;

    ResourceBundles(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleName() {
        return bundleName;
    }

    public static ResourceBundle getDefaultBundle(ResourceBundles bundleName) {
        ResourceBundle defaultBundle = ResourceBundle.getBundle(bundleName.getBundleName(), Locale.getDefault());
        return defaultBundle == null ? ResourceBundle.getBundle(bundleName.getBundleName(), Locale.ENGLISH)
                : defaultBundle;
    }

    @Override
    public String toString() {
        return bundleName;
    }
}
