#!/bin/bash

# ex: QEMU_LD_PREFIX=/rivos/sysroot/riscv QEMU_CPU=rivos-sentinel-ga0 CONF=linux-riscv64-server-release bash run-tests.sh test/hotspot/jtreg:tier1

CONF=${CONF:-linux-riscv64-server-release}

TESTSUITE=test/hotspot/jtreg
NATIVEPATH=$(pwd)/build/${CONF}/images/test/hotspot/jtreg/native

TMPDIR=${TMPDIR:-$(pwd)/build/run-test/tmp}
mkdir -p ${TMPDIR}

ARGS=(
    # JVM arguments
    -Xms64M
    -Xmx1600M
    -Duser.language=en
    -Duser.country=US
    -Djava.library.path="$(pwd)/build/${CONF}/images/test/failure_handler"
    -Dprogram=jtreg
    -jar /opt/jtreg/lib/jtreg.jar # download from https://builds.shipilev.net/jtreg/
    # JTreg arguments
    -agentvm
    -verbose:summary
    -retain:fail,error
    -concurrency:$(nproc)
    -timeoutFactor:16
    -vmoption:-XX:MaxRAMPercentage=12.5
    -vmoption:-Djava.io.tmpdir="${TMPDIR}"
    -automatic
    -ignore:quiet
    -e:JIB_DATA_DIR
    -e:TEST_IMAGE_DIR=$(pwd)/build/${CONF}/images/test
    -dir:$(pwd)
    -reportDir:$(pwd)/build/run-test/test-results
    -workDir:$(pwd)/build/run-test/test-support
    -compilejdk:/rivos/jdk
    -testjdk:$(pwd)/build/${CONF}/images/jdk
    -e:QEMU_LD_PREFIX -e:QEMU_CPU
    -javaoption:-XX:+UnlockExperimentalVMOptions
    # -javaoption:-XX:+UseZacas or any other JVM option for the testjdk
    $(test -n "${NATIVEPATH}" && echo "-nativepath:${NATIVEPATH}"|| true)
    -exclude:${TESTSUITE}/ProblemList.txt
    -exclude:${TESTSUITE}/ProblemList-GHA.txt
)

/rivos/jdk/bin/java ${ARGS[@]} ${*:-${TESTSUITE}}
