#!/bin/zsh

insmod fpga_download.ko
insmod fpga_mini2440_ahb.ko

rm arm
tftp -g -l arm 166.111.227.74
