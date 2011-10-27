#!/bin/bash

function error {
  local exit_status=${1:-$?}
  echo "error:" $1
  exit -1
}


function check_adb {
  hash adb 2>&-
  HAS_ADB=$?

  if [ $HAS_ADB != 0 ]
  then
    error "ADB not found on path"
  fi
}

function check_addr2line {
  hash arm-linux-androideabi-addr2line 2>&-
  HAS_ADB=$?

  if [ $HAS_ADB != 0 ]
  then
    error "arm-linux-androideabi-addr2line not found on path. Add the ndk toolchain bin directory to your path."
  fi
}

function select_device {
  adb shell echo test > /dev/null
  ADB_DEVICE=$?
  if [ $ADB_DEVICE != 0 ]
  then
    error "Could not connect to device"
  fi
}

function find_pid {
  # By default, this will attach to the parent process.
  # Use "plugin-container" as argument to attach to child.
  if [ -z $1 ]; then
    GREP="S org.mozilla.fennec"
  else
    GREP=$1
  fi
  export PID=`adb shell ps | grep "$GREP" | head -n 1 | cut -c11-16`
  echo $PID
  if [ -z $PID ]; then
    error "Could not find fennec process: '$GREP'. Make sure fennec is running."
  fi

  PACKAGE=`adb shell ps | grep "$GREP" | awk '{ print \$9 }' | tr '\r' ' ' | tr '\n' ' '`
  echo $PACKAGE
}

function clear_profile {
  adb shell rm /sdcard/profile_*_*.txt > /dev/null
}

function dump_profile {
  adb shell run-as $PACKAGE kill -42 $PID
  echo adb shell run-as $PACKAGE kill -42 $PID
  sleep 2
  mkdir tmp 2> /dev/null
  rm tmp/profile_*.txt 2> /dev/null
  for file in $(adb shell ls /sdcard/profile_*_*.txt | tr "\n" " " | tr "\r" " "); do
    adb pull $file tmp 2> /dev/null
  done
  cat tmp/*
}

function main {
  check_adb
  check_addr2line

  select_device

  find_pid

  clear_profile

  dump_profile

  exit 0
}

main

