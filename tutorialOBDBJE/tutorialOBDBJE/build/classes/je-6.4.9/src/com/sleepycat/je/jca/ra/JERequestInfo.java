/*-
 *
 *  This file is part of Oracle Berkeley DB Java Edition
 *  Copyright (C) 2002, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle Berkeley DB Java Edition is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle Berkeley DB Java Edition is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License in
 *  the LICENSE file along with Oracle Berkeley DB Java Edition.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 *  An active Oracle commercial licensing agreement for this product
 *  supercedes this license.
 *
 *  For more information please contact:
 *
 *  Vice President Legal, Development
 *  Oracle America, Inc.
 *  5OP-10
 *  500 Oracle Parkway
 *  Redwood Shores, CA 94065
 *
 *  or
 *
 *  berkeleydb-info_us@oracle.com
 *
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  EOF
 *
 */

package com.sleepycat.je.jca.ra;

import java.io.File;

import javax.resource.spi.ConnectionRequestInfo;

import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.TransactionConfig;

public class JERequestInfo implements ConnectionRequestInfo {
    private File rootDir;
    private EnvironmentConfig envConfig;
    private TransactionConfig transConfig;

    public JERequestInfo(File rootDir,
                         EnvironmentConfig envConfig,
                         TransactionConfig transConfig) {
        this.rootDir = rootDir;
        this.envConfig = envConfig;
        this.transConfig = transConfig;
    }

    File getJERootDir() {
        return rootDir;
    }

    EnvironmentConfig getEnvConfig() {
        return envConfig;
    }

    TransactionConfig getTransactionConfig() {
        return transConfig;
    }

    public boolean equals(Object obj) {
        JERequestInfo info = (JERequestInfo) obj;
        return rootDir.equals(info.rootDir);
    }

    public int hashCode() {
        return rootDir.hashCode();
    }

    public String toString() {
        return "</JERequestInfo rootDir=" + rootDir.getAbsolutePath() + "/>";
    }
}
