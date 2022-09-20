/*
 * Copyright (c) 2022, Rivos Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.openjdk.bench.util;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.profile.InternalProfiler;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;

import java.util.Collection;
import java.util.Collections;

import static java.util.concurrent.TimeUnit.NANOSECONDS;;

public class Gem5CheckpointProfiler implements InternalProfiler {
    static {
        System.loadLibrary("Gem5CheckpointProfilerJNI");
    }

    /**
     * Human-readable one-line description of the profiler.
     * @return description
     */
    public String getDescription() {
        return "gem5 checkpointing";
    }

    /**
     * Run this code before starting the next benchmark iteration.
     *
     * @param benchmarkParams benchmark parameters used for current launch
     * @param iterationParams iteration parameters used for current launch
     */
    public void beforeIteration(BenchmarkParams benchmarkParams, IterationParams iterationParams) {
        // artificially delay by 1us to not capture the setting up of the iterations
        m5_checkpoint(1000, iterationParams.getTime().convertTo(NANOSECONDS));
    }

    /**
     * Run this code after a benchmark iteration finished
     *
     * @param benchmarkParams benchmark parameters used for current launch
     * @param iterationParams iteration parameters used for current launch
     * @param result iteration result
     * @return profiler results
     */
    public Collection<? extends Result> afterIteration(BenchmarkParams benchmarkParams, IterationParams iterationParams, IterationResult result) {
        return Collections.emptyList();
    }

    private static final native void m5_checkpoint(long ns_delay, long ns_period);
}
