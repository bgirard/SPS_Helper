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

function select_device {
  adb shell echo test > /dev/null
  ADB_DEVICE=$?
  if [ $ADB_DEVICE != 0 ]
  then
    error "Could not connect to device"
  fi
}

function dump_libs {
  mkdir device_libs 2> /dev/null
  rm device_libs/* 2> /dev/null
  adb pull /system/lib device_libs
  # flatten
  find device_libs -name "*.so" -type f -exec cp '{}' device_libs \;
  ls device_libs/*
}

function main {
  check_adb

  select_device

  dump_libs

  exit 0
}

main

