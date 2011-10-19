#!/bin/bash
# Simple prototype using addr2line, not currently supported

FILENAME=$1
count=0
cat $FILENAME | while read LINE
do
  if [[ "$LINE" == l-* ]]
  then
    symbol=${LINE#??}
    if [[ "$symbol" == *libxul.so@* ]]
    then
      intaddr=${symbol#*libxul.so@}
      addr=$(printf '0x%x\n' $intaddr)
      src=$(addr2line -C -f -e /home/benoitgirard/mozilla/mozilla-central/builds/obj-fn-opt/dist/lib/libxul.so $addr)
      func=$(echo "$src" | head -n 1)
      line=$(echo "$src" | tail -n +2)
      echo "l-$func@$lib"
      echo "l-${line#*/home/benoitgirard/mozilla/mozilla-central/tree/}"
    elif [[ "$symbol" == *libmozutils.so@* ]]
    then
      intaddr=${symbol#*libmozutils.so@}
      addr=$(printf '0x%x\n' $intaddr)
      src=$(addr2line -C -f -e /home/benoitgirard/mozilla/mozilla-central/builds/obj-fn-opt/dist/lib/libmozutils.so $addr)
      func=$(echo "$src" | head -n 1)
      line=$(echo "$src" | tail -n +2)
      echo "l-$func@$lib"
      echo "l-${line#*/home/benoitgirard/mozilla/mozilla-central/tree/}"
    elif [[ "$symbol" == *.so@* ]]
    then
      intaddr=${symbol#*.so@}
      lib=$(basename ${symbol%@*})
      addr=$(printf '0x%x\n' $intaddr)
      src=$(/home/benoitgirard/mozilla/tools/android/ndk/android-ndk-r6/toolchains/arm-linux-androideabi-4.4.3/prebuilt/linux-x86/bin/arm-linux-androideabi-addr2line -C -f -e /home/benoitgirard/mozilla/tools/android/ndk/android-ndk-r4c/lib/$lib $addr)
      func=$(echo "$src" | head -n 1)
      echo "l-$func@$lib"
      #echo "l-$src"
    else
      echo "$LINE"
    fi
  else
    echo "$LINE"
  fi
done
