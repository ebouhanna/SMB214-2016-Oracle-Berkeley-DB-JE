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

package com.sleepycat.je.latch;

import static com.sleepycat.je.EnvironmentFailureException.unexpectedState;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.sleepycat.je.ThreadInterruptedException;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.utilint.StatGroup;

/**
 * An exclusive latch without stats.
 *
 * SharedLatch (not just Latch) is implemented to support exclusive-only BIN
 * latches.
 */
@SuppressWarnings("serial")
public class LatchImpl extends ReentrantLock implements SharedLatch {

    private final LatchContext context;
    private OwnerInfo lastOwnerInfo;

    LatchImpl(final LatchContext context) {
        this.context = context;
    }

    String getName() {
        return context.getLatchName();
    }

    @Override
    public boolean isExclusiveOnly() {
        return true;
    }

    @Override
    public void acquireExclusive() {

        if (isHeldByCurrentThread()) {
            throw unexpectedState(
                context.getEnvImplForFatalException(),
                "Latch already held: " + debugString());
        }

        if (LatchSupport.INTERRUPTIBLE_WITH_TIMEOUT) {
            try {
                if (!tryLock(
                    context.getLatchTimeoutMs(), TimeUnit.MILLISECONDS)) {
                    throw LatchSupport.handleTimeout(this, context);
                }
            } catch (InterruptedException e) {
                throw new ThreadInterruptedException(
                    context.getEnvImplForFatalException(), e);
            }
        } else {
            lock();
        }

        if (LatchSupport.TRACK_LATCHES) {
            LatchSupport.trackAcquire(this, context);
        }
        if (LatchSupport.CAPTURE_OWNER) {
            lastOwnerInfo = new OwnerInfo(context);
        }
        assert EnvironmentImpl.maybeForceYield();
    }

    @Override
    public boolean acquireExclusiveNoWait() {

        if (isHeldByCurrentThread()) {
            throw unexpectedState(
                context.getEnvImplForFatalException(),
                "Latch already held: " + debugString());
        }

        if (!tryLock()) {
            return false;
        }

        if (LatchSupport.TRACK_LATCHES) {
            LatchSupport.trackAcquire(this, context);
        }
        if (LatchSupport.CAPTURE_OWNER) {
            lastOwnerInfo = new OwnerInfo(context);
        }
        assert EnvironmentImpl.maybeForceYield();
        return true;
    }

    @Override
    public void acquireShared() {
        acquireExclusive();
    }

    @Override
    public void release() {
        if (!isHeldByCurrentThread()) {
            throw unexpectedState(
                context.getEnvImplForFatalException(),
                "Latch not held: " + debugString());
        }
        if (LatchSupport.TRACK_LATCHES) {
            LatchSupport.trackRelease(this, context);
        }
        if (LatchSupport.CAPTURE_OWNER) {
            lastOwnerInfo = null;
        }
        unlock();
    }

    @Override
    public void releaseIfOwner() {
        if (!isHeldByCurrentThread()) {
            return;
        }
        if (LatchSupport.TRACK_LATCHES) {
            LatchSupport.trackRelease(this, context);
        }
        if (LatchSupport.CAPTURE_OWNER) {
            lastOwnerInfo = null;
        }
        unlock();
    }

    @Override
    public boolean isOwner() {
        return isHeldByCurrentThread();
    }

    @Override
    public boolean isExclusiveOwner() {
        return isHeldByCurrentThread();
    }

    @Override
    public Thread getExclusiveOwner() {
        return getOwner();
    }

    @Override
    public int getNWaiters() {
        return getQueueLength();
    }

    @Override
    public StatGroup getStats() {
        throw unexpectedState();
    }

    @Override
    public void clearStats() {
        throw unexpectedState();
    }

    @Override
    public String toString() {
        return LatchSupport.toString(this, context, lastOwnerInfo);
    }

    @Override
    public String debugString() {
        return LatchSupport.debugString(this, context, lastOwnerInfo);
    }
}
