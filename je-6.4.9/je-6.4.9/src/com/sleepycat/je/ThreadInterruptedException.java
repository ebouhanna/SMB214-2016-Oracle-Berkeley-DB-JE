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
 * Thrown when {@code java.lang.InterruptedException} (a thread interrupt) or
 * {@code java.nio.channels.ClosedChannelException} (which also results from a
 * thread interrupt) occurs in any JE method.  This occurs when the application,
 * or perhaps a library or container that the application is using, calls
 * {@link Thread#interrupt}.
 *
 * <p>Calling {@code Thread.interrupt} is not recommended for an active JE
 * thread if the goal is to stop the thread or do thread coordination. If you
 * interrupt a thread that is executing a JE operation, the state of the
 * environment will be undefined.  That's because JE might have been in the
 * middle of I/O activity when the operation was aborted midstream, and it
 * becomes very difficult to detect and handle all possible outcomes.</p>
 *
 * <p>When JE detects the interrupt, it will mark the environment invalid and
 * will throw a {@code ThreadInterruptedException}. This tells you that you
 * must close the environment and re-open it before using it again. This is
 * necessary, because if JE didn't throw {@code ThreadInterruptedException}, it
 * is very likely that you would get some other exception that is less
 * meaningful, or simply see corrupted data.</p>
 * 
 * <p>Instead, applications should use other mechanisms like {@code
 * Object.notify} and {@code wait} to coordinate threads. For example, use a
 * {@code keepRunning} variable of some kind in each thread. Check this
 * variable in your threads, and return from the thread when it is false. Set
 * it to false when you want to stop the thread. If this thread is waiting to
 * be woken up to do another unit of work, use {@code Object.notify} to wake it
 * up. This is the recommended technique.</p>
 *
 * <p>However, if the use of {@code Thread.interrupt} is unavoidable, be sure
 * to use it only when shutting down the environment.  In this situation,
 * the {@code ThreadInterruptedException} should be expected.  Note that
 * by shutting down the environment abnormally, recovery time will be longer
 * when the environment is subsequently opened, because a final checkpoint was
 * not performed.</p>
 *
 * <p>Existing {@link Environment} handles are invalidated as a result of this
 * exception.</p>
 *
 * @since 4.0
 */
public class ThreadInterruptedException extends EnvironmentFailureException {

    private static final long serialVersionUID = 1;

    /** 
     * For internal use only.
     * @hidden 
     */
    public ThreadInterruptedException(EnvironmentImpl env, Throwable t) {
        super(env, EnvironmentFailureReason.THREAD_INTERRUPTED, t);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    public ThreadInterruptedException(EnvironmentImpl env, String message) {
        super(env, EnvironmentFailureReason.THREAD_INTERRUPTED, message);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    public ThreadInterruptedException(EnvironmentImpl env,
                                      String message,
                                      Throwable t) {
        super(env, EnvironmentFailureReason.THREAD_INTERRUPTED, message, t);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    private ThreadInterruptedException(String message,
                                       ThreadInterruptedException cause) {
        super(message, cause);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    @Override
    public EnvironmentFailureException wrapSelf(String msg) {
        return new ThreadInterruptedException(msg, this);
    }
}