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

package com.sleepycat.je.rep.elections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.TextProtocol.MessageExchange;
import com.sleepycat.je.rep.impl.TextProtocol.RequestMessage;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.ServiceConnectFailedException;
import com.sleepycat.je.utilint.LoggerUtils;

public class Utils {

    /**
     * Cleans up the socket and its related streams after a request/response
     * cycle.
     *
     * @param channel the channel to be closed
     * @param in the request stream to be closed
     * @param out the response stream to be closed
     */
    static public void cleanup(Logger logger,
                               EnvironmentImpl envImpl,
                               Formatter formatter,
                               DataChannel channel,
                               BufferedReader in,
                               PrintWriter out) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                /* Ignore it, it's only cleanup. */
            }
        }
        if (out != null) {
            out.close();
        }
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                /* Log it and continue. */
                LoggerUtils.logMsg
                    (logger, envImpl, formatter, Level.FINE,
                     "Channel exception on close: " + e.getMessage());
            }
        }
    }

    /**
     * @hidden
     * Utility to broadcast a request to set of targets.
     *
     * @param targets of the broadcast
     * @param requestMessage to be broadcast
     * @param threadPool used to issue message in parallel
     *
     * @return the list of futures representing the results of the broadcast
     */
    public static List<Future<MessageExchange>>
        broadcastMessage(Set<InetSocketAddress> targets,
                         String serviceName,
                         RequestMessage requestMessage,
                         ExecutorService threadPool) {

        List<Future<MessageExchange>> futures =
            new LinkedList<Future<MessageExchange>>();

        for (InetSocketAddress socketAddress : targets) {
            MessageExchange me = requestMessage.getProtocol()
            .new MessageExchange(socketAddress, serviceName, requestMessage);
            try {
                futures.add(threadPool.submit(me, me));
            } catch (RejectedExecutionException ree) {
                if (threadPool.isTerminated()) {
                    /*
                     * The thread pool has been shutdown asynchronously as
                     * part of a general elections shutdown.
                     */
                    return new LinkedList<Future<MessageExchange>>();
                }

                /*
                 * Unexpected, rethrow so it can be reported at a higher
                 * level.
                 */
                throw ree;
            }
        }
        return futures;
    }

    /**
     * Utility to wait for completion of futures.
     *
     * @param futures the futures to wait for
     * @param logger used to report any error messages
     */
    static void checkFutures(List<Future<MessageExchange>> futures,
                             Logger logger,
                             RepImpl envImpl,
                             Formatter formatter) {

        for (final Future<MessageExchange> f : futures) {
            new WithFutureExceptionHandler () {
                @Override
                protected void processFuture ()
                    throws ExecutionException, InterruptedException {
                    f.get();
                }
            }.execute(logger, envImpl, formatter);
        }
    }

    /**
     * Discard futures computations, since we no longer care about their
     * outcomes.
     *
     * @param futures futures to be discarded.
     */
    static void discardFutures(List<Future<MessageExchange>> futures) {
        for (Future<MessageExchange> f : futures) {
            f.cancel(false);
        }
    }

    /**
     * @hidden
     *
     * A utility wrapper to handle all exceptions from futures in a consistent
     * way. The above method illustrates its intended usage pattern
     */
    public static abstract class WithFutureExceptionHandler {

        /* The method represents the future process code being wrapped. */
        protected abstract void processFuture ()
            throws ExecutionException, InterruptedException;

        public final void execute(Logger logger,
                                  RepImpl envImpl,
                                  Formatter formatter) {
            try {
                processFuture();
            } catch (InterruptedException e) {

                if (envImpl != null) {
                    final RepNode rn = envImpl.getRepNode();
                    if ((rn != null) && rn.isShutdown()) {

                        /*
                         * Interrupt for shutdown, it's likely part of a
                         * "hard" stoppable thread shutdown, ignore it.
                         */
                        LoggerUtils.
                            logMsg(logger, envImpl, formatter, Level.INFO,
                                   "Election operation interrupted." +
                                   " Environment being shutdown." );
                        /* Simply exit. */
                        return;
                    }
                }

                throw EnvironmentFailureException.unexpectedException(e);
            } catch (ExecutionException e) {
                /* Get the true cause, unwrap the intermediate wrappers */
                Exception cause = (Exception)e.getCause();
                while (cause instanceof RuntimeException) {
                    Throwable t = ((RuntimeException)cause).getCause();
                    if ((t != null) && (t instanceof Exception)) {
                        cause = (Exception)t;
                    } else {
                        break;
                    }
                }
                if ((cause instanceof ConnectException) ||
                    (cause instanceof SocketException) ||
                    (cause instanceof SocketTimeoutException) ||
                    (cause instanceof ServiceConnectFailedException)){
                    // Network exceptions are expected, log it and keep moving
                    LoggerUtils.logMsg(logger, envImpl, formatter, Level.FINE,
                                       "Election connection failure " +
                                       cause.getMessage());
                    return;
                }
                /* Unanticipated exception, higher level will handle it */
                throw EnvironmentFailureException.unexpectedException(e);
            }
        }
    }
}
