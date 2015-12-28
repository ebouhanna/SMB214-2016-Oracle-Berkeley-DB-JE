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

package com.sleepycat.je;

import com.sleepycat.je.dbi.EnvironmentFailureReason;
import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * Thrown when an {@code IOException} or other failure occurs when writing to
 * the JE log.  This exception may be indicative of a full disk, although an
 * {@code IOException} does not contain enough information to determine this
 * definitively.
 * 
 * <p>This exception may be thrown as the result of any write operation,
 * including record writes, checkpoints, etc.</p>
 *
 * <p>Existing {@link Environment} handles are invalidated as a result of this
 * exception.</p>
 *
 * @since 4.0
 */
public class LogWriteException extends EnvironmentFailureException {

    private static final long serialVersionUID = 1;

    /** 
     * For internal use only.
     * @hidden 
     */
    public LogWriteException(EnvironmentImpl envImpl, String message) {
        super(envImpl, EnvironmentFailureReason.LOG_WRITE, message);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    public LogWriteException(EnvironmentImpl envImpl, Throwable t) {
        super(envImpl, EnvironmentFailureReason.LOG_WRITE, t);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    public LogWriteException(EnvironmentImpl envImpl,
                             String message,
                             Throwable t) {
        super(envImpl, EnvironmentFailureReason.LOG_WRITE, message, t);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    private LogWriteException(String message,
                              LogWriteException cause) {
        super(message, cause);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    @Override
    public EnvironmentFailureException wrapSelf(String msg) {
        return new LogWriteException(msg, this);
    }
}
