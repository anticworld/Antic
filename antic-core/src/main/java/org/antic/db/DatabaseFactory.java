/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.db;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface DatabaseFactory {
    /**
     * Returns a KVDB instance for the specified database.
     * 
     * @param name
     * @return
     */
    Database getDB(DatabaseName name);

    /**
     * Close all opened resources.
     */
    void close();

    /**
     * Returns the data directory of created databases.
     *
     * @return
     */
    Path getDataDir();

    /**
     * @param path
     *            the destination path.
     */
    default void moveTo(Path path) throws IOException {
        Files.move(getDataDir(), path, REPLACE_EXISTING, ATOMIC_MOVE);
    }
}
