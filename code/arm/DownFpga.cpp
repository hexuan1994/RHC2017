#include "fpga_cntl.h"
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <iostream>
#include <iomanip>
using namespace std;

static bool InitFpga(int fd_download, const char* buf, UINT32 len, ECHIP chip);

enum
{
	ZERO = 0,
	ONE = 1
};

static bool SetDclk(int fd, ECHIP chip, UINT16 setting = ONE)
{
	int rc;
	switch (chip)
	{
	case CFPGA:
		rc = pwrite(fd, &setting, sizeof(UINT16), 0x5);
		break;
	case EFPGA:
		rc = pwrite(fd, &setting, sizeof(UINT16), 0x2);
		break;
	default:
		return BAD_PARA;
	}
	return rc >= 0;
}

static bool SetNconfig(int fd, ECHIP chip, UINT16 setting = ONE)
{
	int rc ;
	switch (chip)
	{
	case CFPGA:
		rc = pwrite(fd, &setting, sizeof(UINT16), 0x3);
		break;
	case EFPGA:
		rc = pwrite(fd, &setting, sizeof(UINT16), 0x0);
		break;
	default:
		return BAD_PARA;
	}
	return rc >= 0;
}

static bool ReadNstatus(int fd, ECHIP chip, UINT16 *pData)
{
	int rc;
	switch (chip)
	{
	case CFPGA:
		rc = pread(fd, pData, sizeof(UINT16), 0x2);
		*pData >>= 11;
		break;
	case EFPGA:
		rc = pread(fd, pData, sizeof(UINT16), 0x0);
		*pData >>= 3;
		break;
	default:
		return BAD_PARA;
	}
	return rc >= 0;
}

static bool SetData0(int fd, ECHIP chip, UINT16 setting)
{
	int rc;
	switch (chip)
	{
	case CFPGA:
		rc = pwrite(fd, &setting, sizeof(UINT16), 0x4);
		break;
	case EFPGA:
		rc = pwrite(fd, &setting, sizeof(UINT16), 0x1);
		break;
	default:
		return BAD_PARA;
	}
	return rc >= 0;
}

static bool ReadConfigDone(int fd, ECHIP chip, UINT16 *pData)
{
	int rc;
	switch (chip)
	{
	case CFPGA:
		rc = pread(fd, pData, sizeof(UINT16), 0x3);
		*pData >>= 12;
		break;
	case EFPGA:
		rc = pread(fd, pData, sizeof(UINT16), 0x1);
		*pData >>= 4;
		break;
	default:
		return BAD_PARA;
	}
	return rc >= 0;
}

static bool InitFpga(int fd_download, const char* buf, UINT32 len, ECHIP chip)
{
/*
FILE *fp = fopen("ef2.rbf", "wb");

fwrite(pData, 1, dataLen, fp);

fclose(fp);
*/
	cout << "Begin download fpga ... " << endl;
	// 设置nCONFIG="0",DCLK="0"，保持2us以上
	if (!SetNconfig(fd_download, chip, ZERO) || !SetDclk(fd_download, chip, ZERO))
		return false;
	usleep(2);

	UINT16 status;	// 用于读取状态信号
	// 检测nSTATUS，如果为"0"表明FPGA已响应配置要求，可开始进行配置
	if (!ReadNstatus(fd_download, chip, &status) || status != ZERO)
		return false;

	// nCONFIG="1"，并等待5μS
	if (!SetNconfig(fd_download, chip, ONE))
		return false;
	usleep(5);

	// 传输数据
	for (UINT32 j = 0; j < len; ++j)
	{
		if ((j % 10000) == 0) cout << '.' << flush;
		UINT16 wdata = buf[j];
		for (int i = 0; i < 8; ++i)
		{
			if ((j % 10000) == 0) cout << 'x' << flush;
			// 从低位开始，在Data0上放置数据，设置DCLK="1"（上升沿触发传输），延时
			if (!SetData0(fd_download, chip, wdata & 0x01) || !SetDclk(fd_download, chip, ONE) ||
				// 设置DCLK="0"，并检测nSTATUS，若为"0"则出错
				!SetDclk(fd_download, chip, ZERO) || !ReadNstatus(fd_download, chip, &status) || status == ZERO)
			{
				cout << "Error btype : " << hex << "0X" << j << " bit : " << i << endl;
				return false;
			}
			wdata >>= 1;	// 下一位
		}
	}
	usleep(5);

	// 检测Conf_done，应变成"1"，表明FPGA的配置已完成
	if (!ReadConfigDone(fd_download, chip, &status) || status != ONE)
		return false;

	// 送出40个周期的DCLK，以使FPGA完成初始化
	for (int i = 0; i < 40; ++i)
	{
		SetDclk(fd_download, chip, ONE);
		SetDclk(fd_download, chip, ZERO);
	}
	return true;
}

/*
CPU按下列步骤操作I/O口线，即可完成对FPGA的配置：
1：nCONFIG="0"、DCLK="0"，保持2μS以上。
2：检测nSTATUS，如果为"0"，表明FPGA已响应配置要求，可开始进行配置。否则报错。正常情况下，nCONFIG="0"后1μS内nSTATUS将为"0"。
3：nCONFIG="1"，并等待5μS。
4：Data0上放置数据（LSB first），DCLK="1"，延时。
5：DCLK="0"，并检测nSTATUS，若为"0"，则报错并重新开始。
6：准备下一位数据，并重复执行步骤4、5，直到所有数据送出为止。
7：此时Conf_done应变成"1"，表明FPGA的配置已完成。如果所有数据送出后，Conf_done不为"1"，必须重新配置（从步骤1开始）
8：配置完成后，再送出40个周期的DCLK，以使FPGA完成初始化。
*/

// 将buf写入到chip指定的FPGA中 
bool DownloadFPGA(const char *buf, UINT32 len, ECHIP chip)
{
cout << "fpga size = " << len << endl;
    if (chip != CFPGA && chip != EFPGA)
        return false;
    int fd_download = open("/dev/fpga_download", O_RDWR);
	if (fd_download < 0)
		return false;
	bool ret = InitFpga(fd_download, buf, len, chip);
    close(fd_download);	
if (ret)
	cout << "OK" << endl;
else
	cout << "Failed" << endl;
	return ret;
}
