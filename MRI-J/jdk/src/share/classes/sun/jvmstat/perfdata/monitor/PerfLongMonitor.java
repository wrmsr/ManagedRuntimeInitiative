/*
 * Copyright 2004 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.jvmstat.perfdata.monitor;

import sun.jvmstat.monitor.*;
import sun.management.counter.Units;
import sun.management.counter.Variability;
import java.nio.LongBuffer;

/**
 * Class for monitoring a PerfData Long instrument.
 *
 * @author Brian Doherty
 * @since 1.5
 */
public class PerfLongMonitor extends AbstractMonitor implements LongMonitor {

    /**
     * The buffer containing the data for the long instrument.
     */
    LongBuffer lb;

    /**
     * Constructor to create a LongMonitor object for the long instrument
     * represented by the data in the given buffer.
     *
     * @param name the name of the long instrument
     * @param u the units of measure attribute
     * @param v the variability attribute
     * @param supported support level indicator
     * @param lb the buffer containing the long instrument data.
     */
    public PerfLongMonitor(String name, Units u, Variability v,
                           boolean supported, LongBuffer lb) {
        super(name, u, v, supported);
        this.lb = lb;
    }

    /**
     * {@inheritDoc}
     * The object returned contains a Long object containing the
     * current value of the LongInstrument.
     *
     * @return Object - the current value of the the LongInstrument. The
     *                  return type is guaranteed to be of type Long.
     */
    public Object getValue() {
        return new Long(lb.get(0));
    }

    /**
     * Return the current value of the LongInstrument as an long.
     *
     * @return long - the current value of the LongInstrument
     */
    public long longValue() {
        return lb.get(0);
    }
}
