/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic;

import java.util.ArrayList;
import java.util.List;

import org.antic.cli.AnticCli;
import org.antic.gui.AnticGui;

public class Main {

    private static final String CLI = "--cli";
    private static final String GUI = "--gui";

    public static void main(String[] args) {
        List<String> startArgs = new ArrayList<>();
        boolean startGui = true;
        for (String arg : args) {
            if (CLI.equals(arg)) {
                startGui = false;
            } else if (GUI.equals(arg)) {
                startGui = true;
            } else {
                startArgs.add(arg);
            }
        }

        if (startGui) {
            AnticGui.main(startArgs.toArray(new String[0]));
        } else {
            AnticCli.main(startArgs.toArray(new String[0]));
        }
    }
}
