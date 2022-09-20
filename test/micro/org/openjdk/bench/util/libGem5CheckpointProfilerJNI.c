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

#include <jni.h>
#include <stdint.h>
#include <dlfcn.h>

static void *m5_lib;
static void (*m5_checkpoint)(uint64_t ns_delay, uint64_t ns_period);

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    (*vm)->GetEnv(vm, (void **)&env, JNI_VERSION_1_6);

    m5_lib = dlopen("gem5", RTLD_LAZY | RTLD_LOCAL);
    if (!m5_lib) {
        return (*env)->ThrowNew(env, (*env)->FindClass(env, "java.lang.LinkageError"),
                                    "Failed to load gem5 library");
    }

    m5_checkpoint = (void (*)(uint64_t ns_delay, uint64_t ns_period))dlsym(m5_lib, "m5_checkpoint");
    if (!m5_checkpoint) {
        return (*env)->ThrowNew(env, (*env)->FindClass(env, "java.lang.LinkageError"),
                                    "Failed to load m5_checkpoint from gem5 library");
    }

    return JNI_VERSION_1_8;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
    m5_checkpoint = NULL;
    dlclose(m5_lib);
}

JNIEXPORT void JNICALL
Java_org_openjdk_bench_util_Gem5CheckpointProfiler_m5_checkpoint(
        JNIEnv *env, jclass cls, jlong ns_delay, jlong ns_period) {
    m5_checkpoint(ns_delay, ns_period);
}
