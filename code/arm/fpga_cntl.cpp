#include "fpga_cntl.h"
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
//#define DEBUG
static int ahb = -1; /* 总线 */

/* 初始化，打开总线访问 */
int InitializeAhb()
{
	ahb = open("/dev/fpga_ahb",O_RDWR);
	if(ahb < 0)
	{
		printf("open ahb error!!\n");
		return FAIL;
	}
	return SUCCESS;
}

/* 逆初始化，关闭总线访问 */
void UninitializeAhb()
{
	close(fd);
}

/* 读取总线数据 */
static int ReadAhb(UINT32 offset, UINT16 &data)
{
	int rc;
	rc = pread(fd,&data,2,offset);
	if (rc<0)
	{
		printf("read error!!\n");
		return FAIL;
	}
	return SUCCESS;
}

/* 写入总线数据 */
static int WriteAhb(UINT32 offset, UINT16 data)
{
	int rc;
	rc = pwrite(fd,&data,2,offset);
	if(rc<0)
	{
		printf("write error !!\n");
		return FAIL;
	}
	return 0;
}


/* 从控制FPGA的寄存器中读取数据 */
int ReadCFpgaRegister(UINT32 offset, UINT16 &data)
{
	if (offset > 0x03ffff)
		return BAD_PARA;
	return ReadAhb(offset, data);
}

/* 向控制FPGA的寄存器写数据 */
int WriteCFpgaRegister(UINT32 offset, UINT16 data)
{
	if (offset > 0x03ffff)
		return BAD_PARA;
	return WriteAhb(offset, data);
}

/******************************************************************/
static const UINT32 sramAddrBase = 0x100000;
#define LOW(a) (UINT16)(a & 0xffff)
#define HIGH(a) (UINT16)((a >> 16) & 0xffff)
#define MAKE32(high, low) (((UINT32)high << 16) | (UINT32)low)

/* 读取SRAM数据 */
int ReadSram(UINT32 offset, UINT32 &data)
{
	UINT16 dataHigh, dataLow;
	UINT32 sramAddr;
	if (offset > 0xfffff)
		return BAD_PARA;
	sramAddr = sramAddrBase + offset;
	if (ReadAhb(sramAddr, dataHigh) < SUCCESS)
		return FAIL;
	if (ReadAhb(0x3, dataHigh) < SUCCESS)
		return FAIL;
	if (ReadAhb(0x4, dataLow) < SUCCESS)
		return FAIL;
	data = MAKE32(dataHigh, dataLow);
	return SUCCESS;
}

/* 向SRAM写入数据 */
int WriteSram(UINT32 offset, UINT32 data)
{
	UINT16 dataHigh, dataLow;
	UINT32 sramAddr;
	if (offset > 0xfffff)
		return BAD_PARA;
	sramAddr = sramAddrBase + offset;
	dataHigh = HIGH(data);
	dataLow = LOW(data);
	if (WriteAhb(0x2, dataHigh) < SUCCESS)
		return FAIL;
	return WriteAhb(sramAddr, dataLow);
}

/******************************************************************/
static const UINT32 efpgaRegAddrBase= 0x080000;

/* 读取实验FPGA的寄存器数据 */
int ReadEFpgaRegister(UINT32 offset, UINT32 &data)
{
	UINT16 dataHigh, dataLow;
	UINT32 efpgaRegAddr;
	if (offset > 0xff)
		return BAD_PARA;
	efpgaRegAddr = efpgaRegAddrBase + offset;
	if (ReadAhb(efpgaRegAddr, dataLow) < SUCCESS)
		return FAIL;
	if (ReadAhb(0x7, dataHigh) < SUCCESS)
		return FAIL;
	data = MAKE32(dataHigh, dataLow);
	return SUCCESS;
}

/* 向实验FPGA的寄存器写入数据 */
int WriteEFpgaRegister(UINT32 offset, UINT32 data)
{
	UINT32 efpgaRegAddr;
	UINT16 dataHigh, dataLow;
	if (offset > 0xff)
		return BAD_PARA;
	efpgaRegAddr = efpgaRegAddrBase + offset;
	dataHigh = HIGH(data);
	dataLow = LOW(data);
	if (WriteAhb(0x6, dataHigh) < SUCCESS)
		return FAIL;
	return WriteAhb(efpgaRegAddr, dataLow);
}

/******************************************************************/
/* 设置SRAM访问权是CPU/控制FPGA还是实验FPGA */
int EnableSramAccess(ECHIP chip)
{
	switch (chip)
	{
	case CFPGA:
		return WriteAhb(0x5, 0);
		break;
	case EFPGA:
		return WriteAhb(0x5, 1);
		break;
	default:
		return BAD_PARA;
	}
}
