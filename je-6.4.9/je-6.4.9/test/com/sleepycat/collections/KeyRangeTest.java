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

package com.sleepycat.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import org.junit.After;
import org.junit.Test;

import com.sleepycat.bind.ByteArrayBinding;
import com.sleepycat.compat.DbCompat;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.util.keyrange.KeyRange;
import com.sleepycat.util.keyrange.KeyRangeException;
import com.sleepycat.util.test.SharedTestUtils;
import com.sleepycat.util.test.TestBase;

/**
 * @author Mark Hayes
 */
public class KeyRangeTest extends TestBase {

    private static boolean VERBOSE = false;

    private static final byte FF = (byte) 0xFF;

    private static final byte[][] KEYS = {
        /* 0 */ {1},
        /* 1 */ {FF},
        /* 2 */ {FF, 0},
        /* 3 */ {FF, 0x7F},
        /* 4 */ {FF, FF},
        /* 5 */ {FF, FF, 0},
        /* 6 */ {FF, FF, 0x7F},
        /* 7 */ {FF, FF, FF},
    };
    private static byte[][] EXTREME_KEY_BYTES = {
        /* 0 */ {0},
        /* 1 */ {FF, FF, FF, FF},
    };

    private Environment env;
    private Database store;
    private DataView view;
    private DataCursor cursor;

    private void openDb(Comparator<byte []> comparator)
        throws Exception {

        File dir = SharedTestUtils.getNewDir();
        ByteArrayBinding dataBinding = new ByteArrayBinding();
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        DbCompat.setInitializeCache(envConfig, true);
        env = new Environment(dir, envConfig);
        DatabaseConfig dbConfig = new DatabaseConfig();
        DbCompat.setTypeBtree(dbConfig);
        dbConfig.setAllowCreate(true);
        if (comparator != null) {
            DbCompat.setBtreeComparator(dbConfig, comparator);
        }
        store = DbCompat.testOpenDatabase
            (env, null, "test.db", null, dbConfig);
        view = new DataView(store, dataBinding, dataBinding, null, true, null);
    }

    private void closeDb()
        throws Exception {

        store.close();
        store = null;
        env.close();
        env = null;
    }

    @After
    public void tearDown() {
        try {
            if (store != null) {
                store.close();
            }
        } catch (Exception e) {
            System.out.println("Exception ignored during close: " + e);
        }
        try {
            if (env != null) {
                env.close();
            }
        } catch (Exception e) {
            System.out.println("Exception ignored during close: " + e);
        }
        /* Ensure that GC can cleanup. */
        env = null;
        store = null;
        view = null;
        cursor = null;
    }

    @Test
    public void testScan() throws Exception {
        openDb(null);
        doScan(false);
        closeDb();
    }

    @Test
    public void testScanComparator() throws Exception {
        openDb(new ReverseComparator());
        doScan(true);
        closeDb();
    }

    private void doScan(boolean reversed) throws Exception {

        byte[][] keys = new byte[KEYS.length][];
        final int end = KEYS.length - 1;
        cursor = new DataCursor(view, true);
        for (int i = 0; i <= end; i++) {
            keys[i] = KEYS[i];
            cursor.put(keys[i], KEYS[i], null, false);
        }
        cursor.close();
        byte[][] extremeKeys = new byte[EXTREME_KEY_BYTES.length][];
        for (int i = 0; i < extremeKeys.length; i++) {
            extremeKeys[i] = EXTREME_KEY_BYTES[i];
        }

        // with empty range

        cursor = new DataCursor(view, false);
        expectRange(KEYS, 0, end, reversed);
        cursor.close();

        // begin key only, inclusive

        for (int i = 0; i <= end; i++) {
            cursor = newCursor(view, keys[i], true, null, false, reversed);
            expectRange(KEYS, i, end, reversed);
            cursor.close();
        }

        // begin key only, exclusive

        for (int i = 0; i <= end; i++) {
            cursor = newCursor(view, keys[i], false, null, false, reversed);
            expectRange(KEYS, i + 1, end, reversed);
            cursor.close();
        }

        // end key only, inclusive

        for (int i = 0; i <= end; i++) {
            cursor = newCursor(view, null, false, keys[i], true, reversed);
            expectRange(KEYS, 0, i, reversed);
            cursor.close();
        }

        // end key only, exclusive

        for (int i = 0; i <= end; i++) {
            cursor = newCursor(view, null, false, keys[i], false, reversed);
            expectRange(KEYS, 0, i - 1, reversed);
            cursor.close();
        }

        // begin and end keys, inclusive and exclusive

        for (int i = 0; i <= end; i++) {
            for (int j = i; j <= end; j++) {
                // begin inclusive, end inclusive

                cursor = newCursor(view, keys[i], true, keys[j],
                                        true, reversed);
                expectRange(KEYS, i, j, reversed);
                cursor.close();

                // begin inclusive, end exclusive

                cursor = newCursor(view, keys[i], true, keys[j],
                                        false, reversed);
                expectRange(KEYS, i, j - 1, reversed);
                cursor.close();

                // begin exclusive, end inclusive

                cursor = newCursor(view, keys[i], false, keys[j],
                                        true, reversed);
                expectRange(KEYS, i + 1, j, reversed);
                cursor.close();

                // begin exclusive, end exclusive

                cursor = newCursor(view, keys[i], false, keys[j],
                                        false, reversed);
                expectRange(KEYS, i + 1, j - 1, reversed);
                cursor.close();
            }
        }

        // single key range

        for (int i = 0; i <= end; i++) {
            cursor = new DataCursor(view, false, keys[i]);
            expectRange(KEYS, i, i, reversed);
            cursor.close();
        }

        // start with lower extreme (before any existing key)

        cursor = newCursor(view, extremeKeys[0], true, null, false, reversed);
        expectRange(KEYS, 0, end, reversed);
        cursor.close();

        // start with higher extreme (after any existing key)

        cursor = newCursor(view, null, false, extremeKeys[1], true, reversed);
        expectRange(KEYS, 0, end, reversed);
        cursor.close();
    }

    private DataCursor newCursor(DataView view,
                                 Object beginKey, boolean beginInclusive,
                                 Object endKey, boolean endInclusive,
                                 boolean reversed)
        throws Exception {

        if (reversed) {
            return new DataCursor(view, false,
                                  endKey, endInclusive,
                                  beginKey, beginInclusive);
        } else {
            return new DataCursor(view, false,
                                  beginKey, beginInclusive,
                                  endKey, endInclusive);
        }
    }

    private void expectRange(byte[][] bytes, int first, int last,
                             boolean reversed)
        throws DatabaseException {

        int i;
        boolean init;
        for (init = true, i = first;; i++, init = false) {
            if (checkRange(bytes, first, last, i <= last,
                           reversed, !reversed, init, i)) {
                break;
            }
        }
        for (init = true, i = last;; i--, init = false) {
            if (checkRange(bytes, first, last, i >= first,
                           reversed, reversed, init, i)) {
                break;
            }
        }
    }

    private boolean checkRange(byte[][] bytes, int first, int last,
                               boolean inRange, boolean reversed,
                               boolean forward, boolean init,
                               int i)
        throws DatabaseException {

        OperationStatus s;
        if (forward) {
            if (init) {
                s = cursor.getFirst(false);
            } else {
                s = cursor.getNext(false);
            }
        } else {
            if (init) {
                s = cursor.getLast(false);
            } else {
                s = cursor.getPrev(false);
            }
        }

        String msg = " " + (forward ? "next" : "prev") + " i=" + i +
                     " first=" + first + " last=" + last +
                     (reversed ? " reversed" : " not reversed");

        // check that moving past ends doesn't move the cursor
        if (s == OperationStatus.SUCCESS && i == first) {
            OperationStatus s2 = reversed ? cursor.getNext(false)
                                          : cursor.getPrev(false);
            assertEquals(msg, OperationStatus.NOTFOUND, s2);
        }
        if (s == OperationStatus.SUCCESS && i == last) {
            OperationStatus s2 = reversed ? cursor.getPrev(false)
                                          : cursor.getNext(false);
            assertEquals(msg, OperationStatus.NOTFOUND, s2);
        }

        byte[] val = (s == OperationStatus.SUCCESS)
                        ?  ((byte[]) cursor.getCurrentValue())
                        : null;

        if (inRange) {
            assertNotNull("RangeNotFound" + msg, val);

            if (!Arrays.equals(val, bytes[i])){
                printBytes(val);
                printBytes(bytes[i]);
                fail("RangeKeyNotEqual" + msg);
            }
            if (VERBOSE) {
                System.out.println("GotRange" + msg);
            }
            return false;
        } else {
            assertEquals("RangeExceeded" + msg, OperationStatus.NOTFOUND, s);
            return true;
        }
    }

    private void printBytes(byte[] bytes) {

        for (int i = 0; i < bytes.length; i += 1) {
            System.out.print(Integer.toHexString(bytes[i] & 0xFF));
            System.out.print(' ');
        }
        System.out.println();
    }

    @Test
    public void testSubRanges() {

        DatabaseEntry begin = new DatabaseEntry();
        DatabaseEntry begin2 = new DatabaseEntry();
        DatabaseEntry end = new DatabaseEntry();
        DatabaseEntry end2 = new DatabaseEntry();
        KeyRange range = new KeyRange(null);
        KeyRange range2 = null;
        
        /* Base range [1, 2] */
        begin.setData(new byte[] { 1 });
        end.setData(new byte[] { 2 });
        range = range.subRange(begin, true, end, true);

        /* Subrange (0, 1] is invalid **. */
        begin2.setData(new byte[] { 0 });
        end2.setData(new byte[] { 1 });
        try {
            range2 = range.subRange(begin2, false, end2, true);
            fail();
        } catch (KeyRangeException expected) {}

        /* Subrange [1, 3) is invalid. */
        begin2.setData(new byte[] { 1 });
        end2.setData(new byte[] { 3 });
        try {
            range2 = range.subRange(begin2, true, end2, false);
            fail();
        } catch (KeyRangeException expected) {}

        /* Subrange [2, 2] is valid. */
        begin2.setData(new byte[] { 2 });
        end2.setData(new byte[] { 2 });
        range2 = range.subRange(begin2, true, end2, true);

        /* Subrange [0, 1] is invalid. */
        begin2.setData(new byte[] { 0 });
        end2.setData(new byte[] { 1 });
        try {
            range2 = range.subRange(begin2, true, end2, true);
            fail();
        } catch (KeyRangeException expected) {}

        /* Subrange (0, 3] is invalid. */
        begin2.setData(new byte[] { 0 });
        end2.setData(new byte[] { 3 });
        try {
            range2 = range.subRange(begin2, false, end2, true);
            fail();
        } catch (KeyRangeException expected) {}

        /* Subrange [3, 3) is invalid. */
        begin2.setData(new byte[] { 3 });
        end2.setData(new byte[] { 3 });
        try {
            range2 = range.subRange(begin2, true, end2, false);
            fail();
        } catch (KeyRangeException expected) {}
    }

    @SuppressWarnings("serial")
    public static class ReverseComparator implements Comparator<byte[]>,
                                                     Serializable {
        public int compare(byte[] d1, byte[] d2) {
            int cmp = KeyRange.compareBytes(d1, 0, d1.length,
                                            d2, 0, d2.length);
            if (cmp < 0) {
                return 1;
            } else if (cmp > 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
