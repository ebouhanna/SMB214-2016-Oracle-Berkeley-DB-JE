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

package com.sleepycat.je.txn;

import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.tree.Key;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.VLSN;

/*
 * Given a locker T and a record R, a WriteLockInfo stores the info needed to
 * undo the write ops (insertions, deletions, or updates) performed by T on
 * R, if T aborts. Specifically, it stores the info needed to restore R to its
 * "abort" version, i.e., the version of R that existed at the time when T
 * locked R for the 1st time (and before T actually performed its 1st write
 * op on R). Given that only transactional lockers may abort, WriteLockInfo
 * are used by Txn lockers only.
 *
 * Notice that, conceptually, to write R, T locks R in exclusive mode and
 * retains that lock until it terminates (commits or aborts). In other words,
 * T locks R only once. However, because locks are acquired on LSNs and LSNs
 * change every time a record is written, T may actually lock R multiple times,
 * Everytime T locks a new LSN of R, it creates a new WriteLockIno. However,
 * care is taken so that all WriteLockInfos for the same logical record and
 * the same txn store the same info about the abort version (for details see
 * CursorImpl.LockStanding, CursorImpl.lockLN(), and LN.logInternals()). In
 * fact, if T writes R multiple times and then aborts, only one of the logrecs
 * generated by T for R will be undone, restoring R to its abort version; the
 * rest will be skipped. 
 *
 * Info about the aort version is needed during commit as well. Specifically,
 * its LSN and on-disk size are needed to count the abort version obsolete
 * when T commits.
 */
public class WriteLockInfo {

    /*
     * The LSN of the record's abort version. This is stored persistently in
     * each logrec renerated by T on R. May be null if R was created by T
     * (will definitely be null if the txn did not reuse an existing slot for
     * the new record).
     */
    private long abortLsn;

    /*
     * Whether the record's abort version is a deletion version or not.
     * It is stored persistently in each logrec renerated by T on R.
     */
    private boolean abortKnownDeleted;

    /*
     * See comment for abortData field below.
     */
    private byte[] abortKey;

    /*
     * If the record's abort version was embedded in a BIN, the associated
     * logrec that contains that version may have been cleaned away by the
     * time the txn aborts. So, we must save in each logrec the data portion
     * of the abort version. abortdata serves this purpose. If key updates
     * are allowed in the containing DB, the key of the abort version is
     * saved in abortKey as well. Finally, if VLSN caching in BINs is enabled,
     * the VLSN of the abort version is saved in abortVLSN as well.
     */
    private byte[] abortData;

    /*
     * See comment for abortData field above.
     */
    private long abortVLSN = VLSN.NULL_VLSN_SEQUENCE;

    /*
     * The on-disk size of the abort version, or zero if abortLsn is NULL_LSN
     * or if the size is not known. Used for obsolete counting during commit.
     * Not stored persistently.
     */
    private int abortLogSize;

    /*
     * The containing database, or null if abortLsn is NULL_LSN.  Used for
     * obsolete counting during a commit.
     */
    private DatabaseImpl db;

    /*
     * True if the LSN has never been locked before by this Txn. Used so we
     * can determine when to set abortLsn.
     */
    private boolean neverLocked;

    static final WriteLockInfo basicWriteLockInfo = new WriteLockInfo();

    // public for Sizeof
    public WriteLockInfo() {
        abortLsn = DbLsn.NULL_LSN;
        abortKnownDeleted = false;
        neverLocked = true;
    }

    public boolean getAbortKnownDeleted() {
        return abortKnownDeleted;
    }

    public void setAbortKnownDeleted(boolean v) {
        abortKnownDeleted = v;
    }

    public long getAbortLsn() {
        return abortLsn;
    }

    public void setAbortLsn(long abortLsn) {
        this.abortLsn = abortLsn;
    }

    public byte[] getAbortKey() {
        return abortKey;
    }

    public void setAbortKey(byte[] v) {
        abortKey = v;
    }

    public byte[] getAbortData() {
        return abortData;
    }

    public void setAbortData(byte[] v) {
        abortData = v;
    }

   public long getAbortVLSN() {
        return abortVLSN;
    }

   public void setAbortVLSN(long v) {
        abortVLSN = v;
    }

    public int getAbortLogSize() {
        return abortLogSize;
    }

    public void setAbortLogSize(int logSize) {
        abortLogSize = logSize;
    }

    public DatabaseImpl getDb() {
        return db;
    }

    public void setDb(DatabaseImpl db) {
        this.db = db;
    }

    public boolean getNeverLocked() {
        return neverLocked;
    }

    public void setNeverLocked(boolean neverLocked) {
        this.neverLocked = neverLocked;
    }

    /*
     * Copy all the information needed to create a clone of the lock.
     */    
    public void copyAllInfo(WriteLockInfo source) {
        abortLsn = source.abortLsn;
        abortKnownDeleted = source.abortKnownDeleted;
        abortKey = source.abortKey;
        abortData = source.abortData;
        abortVLSN = source.abortVLSN;
        abortLogSize = source.abortLogSize;
        db = source.db;
        neverLocked = source.neverLocked;
    }

    @Override
    public String toString() {
        return "abortLsn=" +
            DbLsn.getNoFormatString(abortLsn) +
            " abortKnownDeleted=" + abortKnownDeleted +
            " abortKey=" + Key.getNoFormatString(abortKey) +
            " abortData=" + Key.getNoFormatString(abortData) +
            " abortLogSize=" + abortLogSize +
            " abortVLSN=" + String.format("%,d", abortVLSN) +
            " neverLocked=" + neverLocked;
    }
}
