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

package com.sleepycat.persist;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.DatabaseEntry;

/**
 * A ValueAdapter where the "value" is the data, although the data in this case
 * is the primary key in a KeysIndex.
 *
 * @author Mark Hayes
 */
class DataValueAdapter<V> implements ValueAdapter<V> {

    private EntryBinding dataBinding;

    DataValueAdapter(Class<V> keyClass, EntryBinding dataBinding) {
        this.dataBinding = dataBinding;
    }

    public DatabaseEntry initKey() {
        return new DatabaseEntry();
    }

    public DatabaseEntry initPKey() {
        return null;
    }

    public DatabaseEntry initData() {
        return new DatabaseEntry();
    }

    public void clearEntries(DatabaseEntry key,
                             DatabaseEntry pkey,
                             DatabaseEntry data) {
        key.setData(null);
        data.setData(null);
    }

    public V entryToValue(DatabaseEntry key,
                          DatabaseEntry pkey,
                          DatabaseEntry data) {
        return (V) dataBinding.entryToObject(data);
    }

    public void valueToData(V value, DatabaseEntry data) {
        throw new UnsupportedOperationException
            ("Cannot change the data in a key-only index");
    }
}
