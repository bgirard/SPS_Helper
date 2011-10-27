#!/bin/bash
# Simple prototype using addr2line, not currently supported

ADDR2LINEPRG=arm-linux-androideabi-addr2line
FILENAME=$1
count=0

#trap "killall background 2>/dev/null" EXIT
#Start addr2line on libxul.so since its soooo slow to parse
#mkfifo libxul_addr2line
#mkfifo libxul_addr2line_out
#$ADDR2LINEPRG -C -f -e $MOZ_OBJDIR/dist/lib/libxul.so < libxul_addr2line > libxul_addr2line_out &
#exec 3>libxul_addr2line

#mkfifo libc_addr2line
#$ADDR2LINEPRG -C -f -e device_libs/libc.so < libc_addr2line &
#exec 4>libc_addr2line

cat $FILENAME | while read LINE
do
  if [[ "$LINE" == l-* ]]
  then
    symbol=${LINE#??}
    if [[ "$symbol" == *libxul.so@* ]]
    then
      intaddr=${symbol#*libxul.so@}
      addr=$(printf '0x%x\n' $intaddr)
      #echo $addr >&3
      src=$($ADDR2LINEPRG -C -f -e $MOZ_OBJDIR/dist/lib/libxul.so $addr)
      func=$(echo "$src" | head -n 1)
      line=$(echo "$src" | tail -n +2)
      echo "l-$func@$lib"
      echo "l-${line#*$MOZ_OBJDIR}"
    elif [[ "$symbol" == *libmozutils.so@* ]]
    then
      intaddr=${symbol#*libmozutils.so@}
      addr=$(printf '0x%x\n' $intaddr)
      src=$($ADDR2LINEPRG -C -f -e $MOZ_OBJDIR/dist/lib/libmozutils.so $addr)
      func=$(echo "$src" | head -n 1)
      line=$(echo "$src" | tail -n +2)
      echo "l-$func@$lib"
      echo "l-${line#*$MOZ_OBJDIR}"
    elif [[ "$symbol" == *libc.so@* ]]
    then
      intaddr=${symbol#*libc.so@}
      addr=$(printf '0x%x\n' $intaddr)
      src=$($ADDR2LINEPRG -C -f -e device_libs/libc.so $addr)
      func=$(echo "$src" | head -n 1)
      line=$(echo "$src" | tail -n +2)
      echo "l-$func@$lib"
      echo "l-${line#*$MOZ_OBJDIR}"
    elif [[ "$symbol" == *.so@* ]]
    then
      intaddr=${symbol#*.so@}
      lib=$(basename ${symbol%@*})
      addr=$(printf '0x%x\n' $intaddr)
      src=$($ADDR2LINEPRG -C -f -e device_libs/$lib $addr)
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
