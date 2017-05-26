#ifndef SOCK_INTERFACE_H__
#define SOCK_INTERFACE_H__

// 定义socket传输的命令
const char CMD_WriteRam = 0x30;					// 写内存
const char CMD_ReadRam = 0x31;					// 读内存
const char CMD_WriteFpgaToFlash = 0x32;			// ***废弃不用
const char CMD_DownloadFpgaFromFlash = 0x34;	// 下载控制FPGA
const char CMD_GetDBStatus = 0x35;				// +++获取总线状态(内存访问权限)，没有实际用途了
const char CMD_OpenUsbByAddr = 0x37;			// ***废弃不用
const char CMD_ResetUsb = 0x39;					// ***废弃不用
const char CMD_ResetCpld = 0x3a;				// 重启arm操作系统，暂未实现
const char CMD_DownloadFpgaFromUsb = 0x3c;		// 下载实验FPGA
const char CMD_ResetExam = 0x3f;				// 重置实验FPGA
const char CMD_GetDeviceInfo = 0x40;			// +++获取设备位置，没有用了
const char CMD_SetDeviceInfo = 0x41;			// ***原先就没有实现
const char CMD_SetDBStatus = 0xe0;				// +++设置总线状态(内存访问权限)，没有实际用途了
const char CMD_SetDataBus = 0xe1;				// ***废弃不用
const char CMD_GetRegs = 0xe2;					// 读所有寄存器值
const char CMD_SendCycles = 0xe3;				// 控制时钟走多个周期
const char CMD_FlipCycles = 0xe4;				// 时钟走1个周期
const char CMD_WriteRegs = 0xe5;
const char CMD_ExprimentType = 0xe6;


// 定义socket传输的返回值
typedef char RETURN_VALUE;
const char RET_OK = 0;    		// 操作成功
const char RET_FAILED = -1; 	// 操作失败
const char RET_LOST_DATA = 1;	// 参数错误

#endif //SOCK_INTERFACE_H__
