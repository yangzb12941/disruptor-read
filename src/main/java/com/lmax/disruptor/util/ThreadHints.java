/*  Copyright 2016 Gil Tene
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lmax.disruptor.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;


import static java.lang.invoke.MethodType.methodType;

/**
 * 在JDK9的Thread中增加了{@code onSpinWait}方法，如果允许环境是JDK9的话，则在自旋等待时，尝试调用{@code onSpinWait}方法。
 *
 * This class captures possible hints that may be used by some
 * runtimes to improve code performance. It is intended to capture hinting
 * behaviours that are implemented in or anticipated to be spec'ed under the
 * {@link java.lang.Thread} class in some Java SE versions, but missing in prior
 * versions.
 */
public final class ThreadHints
{
    private static final MethodHandle ON_SPIN_WAIT_METHOD_HANDLE;

    static
    {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        MethodHandle methodHandle = null;
        try
        {
            methodHandle = lookup.findStatic(Thread.class, "onSpinWait", methodType(void.class));
        }
        catch (final Exception ignore)
        {
        }

        ON_SPIN_WAIT_METHOD_HANDLE = methodHandle;
    }

    private ThreadHints()
    {
    }

    /**
     * Indicates that the caller is momentarily unable to progress, until the
     * occurrence of one or more actions on the part of other activities.  By
     * invoking this method within each iteration of a spin-wait loop construct,
     * the calling thread indicates to the runtime that it is busy-waiting. The runtime
     * may take action to improve the performance of invoking spin-wait loop constructions.
     *
     * 表示调用方暂时无法继续，直到其他活动发生一个或多个操作。通过在自旋等待循环构造的每个迭代中调用此方法，
     * 调用线程向运行时指示它正在忙着等待。运行时可以采取措施来提高调用自旋等待循环构造的性能。
     */
    public static void onSpinWait()
    {
        // Call java.lang.Thread.onSpinWait() on Java SE versions that support it. Do nothing otherwise.
        // This should optimize away to either nothing or to an inlining of java.lang.Thread.onSpinWait()
        if (null != ON_SPIN_WAIT_METHOD_HANDLE)
        {
            try
            {
                ON_SPIN_WAIT_METHOD_HANDLE.invokeExact();
            }
            catch (final Throwable ignore)
            {
            }
        }
    }
}
