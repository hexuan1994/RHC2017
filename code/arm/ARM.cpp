#include <sys/types.h>

#include <sys/socket.h>

#include <netinet/in.h>

#include <arpa/inet.h>

#include <pthread.h>

#include <iostream>

#include <fstream>

#include <cstring>

#include <unistd.h>

#include <string>

#include <cstdio>

#include <cstdlib>

#include <iomanip>

#include "SockInterface.h"

#include "fpga_cntl.h"

using namespace std;

string destIP, armIP;

extern int type;
extern int input;
extern int result;
void calculateResult();



bool LoadIPAddr()

{

    ifstream fin("./ip.txt");

    if (!fin)

        return false;

    fin >> destIP >> armIP;

    return true;

}



bool GetFileContent(const char *szFile, char *&buf, size_t &len)

{

    FILE *fp = fopen(szFile, "rb");

    if (fp == NULL)

        return false;

    fseek(fp, 0L, SEEK_END);

    len = ftell(fp);

    buf = new char[len];

    fseek(fp, 0L, SEEK_SET);

    fread(buf, 1, len, fp);

    fclose(fp);

    return true;

}



bool CheckHardware()

{

    char *buf = NULL;

    size_t len;

    // 下载控制FPGA

    bool b = GetFileContent("/home/plg/cfpga.rbf", buf, len);

    b = b && DownloadFPGA(buf, len, CFPGA);

    delete []buf;

    if (!b)

        return false;

    // 下载实验FPGA

	b = GetFileContent("/home/plg/efpga.rbf", buf, len);

    b = b && DownloadFPGA(buf, len, EFPGA);

    delete []buf;

    if (!b)

        return false;

    // 测试


// 寄存器测试暂时关闭

    // 控制FPGA

    UINT16 t1;

    int r = ReadCFpgaRegister(0x0, t1); // 版本号寄存器

    if (r < SUCCESS || t1 != 0x0472)

        return false;

    r = WriteCFpgaRegister(0x1, 0x1234);   // 读写控制FPGA

    if (r == SUCCESS)

        r = ReadCFpgaRegister(0x1, t1);

    if (r < SUCCESS || t1 != 0x1234)

        return false;

    // 实验FPGA

    UINT32 t2;

    r = ReadEFpgaRegister(0x0, t2);    // 版本号寄存器

    if (r < SUCCESS || t2 != 0x20150121)

        return false;

    r = WriteEFpgaRegister(0x1, 0x12345678);   // 读写实验FPGA

    if (r == SUCCESS)

        r = ReadEFpgaRegister(0x1, t2);

    if (r < SUCCESS || t2 != 0x12345678)

        return false;


    // 读写SRAM

    r = EnableSramAccess(CFPGA);    // 指定控制FPGA访问SRAM

    if (r < SUCCESS )

        return false;

    r = WriteSram(0x8, 0x1234abcd);

    if (r == SUCCESS)

        r = ReadSram(0x8, t2);


    if (r < SUCCESS || t2 != 0x1234abcd)

        return false;

    // 指定由实验FPGA访问SRAM

    return (EnableSramAccess(EFPGA) == SUCCESS);

}



int lisSocket;
int tcpPort;

bool Initialize()   // 初始化socket，启动监听

{

    lisSocket = socket(AF_INET, SOCK_STREAM, 0);

	if (lisSocket == -1)

	   return false;

    struct sockaddr_in serAddr;

	serAddr.sin_family = AF_INET;

	serAddr.sin_port = htons(tcpPort+1324);

	serAddr.sin_addr.s_addr = inet_addr(armIP.c_str());

	if (bind(lisSocket, (struct sockaddr*)&serAddr, sizeof(serAddr)) == -1 ||

        listen(lisSocket, 1) == -1)

	{

	    close(lisSocket);

	    return false;

	}

    return true;

}



void* RegisterToServer(void*) // 通知服务器，实验板可用

{

	int count = 0;

    while (1)

    {

		int socketfd = socket(AF_INET, SOCK_STREAM, 0);

		if(socketfd == -1)

			return NULL;



		struct sockaddr_in server;

		server.sin_family = AF_INET;

		server.sin_port = htons(9040);

		server.sin_addr.s_addr = inet_addr(destIP.c_str());

		// Connect to server.

        int connectResult = connect(socketfd, (struct sockaddr*)&server, sizeof(struct sockaddr));

	    if (connectResult == -1)

		{

			close(socketfd);

			sleep(3);

			continue;

		}

        // 连上了

        cout << dec << (++count) << " : connected to server" << endl;

        char x = tcpPort;

        int result = send(socketfd, &x, 1, 0);  // 通知服务器

        if (result == -1)   // 丢失连接

        {

            close(socketfd);

			sleep(3);

            continue;

        }

        close(socketfd);

        sleep(5);           // 停5秒钟

    }

	return NULL;

}



void* ProcessCommand(void *para);       //任务处理进程声明



int main(int argc, char* argv[])

{

#ifndef DEBUG
    // 硬件自检
    
    if (InitializeAhb() < SUCCESS)      //初始化,打开总线访问

    {

        cout << "Hardware initialization failed." << endl;

        return 0;

    }
/*
    if (!CheckHardware())

    {

        cout << "Hardware checking failed." << endl;

        return 0;

    }

    else

        cout << "Hardware checking successful." << endl;
*/
#endif
    cout << "Program Start."<< endl;
    // 初始化socket，启动监听

    if(argc < 2)
    {
        cout << "Need tcpPort argument, such as \" ./arm 123 \"" << endl;
        return false;
    }

    tcpPort = atoi(argv[1]);
    cout << tcpPort << endl;


    if (!LoadIPAddr())

    {

        cout << "Load IP config failed." << endl;

        return 0;

    }

    if (!Initialize())

    {

        cout << "Listening socket setup failed." << endl;

        return 0;

    }

    else

        cout << "Listening socket ready." << endl;


    cout << "Ready to registering to server." << endl;
    // 持续通知服务器：硬件可用

    pthread_t id;

    pthread_create(&id, NULL, RegisterToServer, 0);



    // 接收命令，开始工作

    cout << "Listening......" << endl;

    while (true)

    {

        // Accept connection

        struct sockaddr_in cliAddr;

        socklen_t len = sizeof(cliAddr);

		int serSocket = accept(lisSocket, (struct sockaddr*)&cliAddr, &len);

		if (serSocket != -1)

		{

		    cout << "Accepted one connection." << endl;

			pthread_t id;

			int ret = pthread_create(&id, NULL, ProcessCommand, (void *)serSocket);
			if(ret != 0)
                perror("thread reero:");
            pthread_detach(id);

		}

		else

		{

			cout << "Accept error." << endl;

		}

    }

#ifndef DEBUG

    UninitializeAhb();

#endif

    return 0;

}



// 通过socket返回指令执行结果

void SendData(int sock, RETURN_VALUE v)

{

    char ret = v;

    if (send(sock, &ret, 1, 0) == -1)

    	cout << "Send result failed." << endl;

}

void SendData(int sock, bool b)

{

    if (b)

        SendData(sock, RET_OK);

    else

        SendData(sock, RET_FAILED);

}

void SendData(int sock, RETURN_VALUE v, char* pData, int dataLen)

{

    SendData(sock, v);

    if (send(sock, pData, dataLen, 0) == -1)

        cout << "Send data failed." << endl;

}



// =============================================

RETURN_VALUE DownloadEFPGA(const char* pData, int dataLen)

{



#ifndef DEBUG

    if (DownloadFPGA(pData, dataLen, EFPGA))

        return RET_OK;

    else

        return RET_FAILED;

#else

    return RET_OK;

#endif

}



void ReadRegisters(int sock)

{

    UINT32 registers[256];

    int r = SUCCESS;

    for (UINT32 i = 0; i < 256 && r == SUCCESS; ++i)

        r = ReadEFpgaRegister(i, registers[i]);

    if (r == SUCCESS)

        SendData(sock, RET_OK, (char*)registers, sizeof(UINT32)*256);

    else

        SendData(sock, RET_FAILED);

}



bool ReceiveSocketData(int sock, UINT32 &para)

{

    char buf[5] = {0};

    int len = recv(sock, buf, 5, 0);

	if (len != sizeof(UINT32))

	{

	    cout << "Receive data error." << endl;

		return false;

	}

	para = *(UINT32*)buf;

	return true;

}



bool ReceiveSocketData(int sock, UINT32 &para1, UINT32 &para2, UINT32 &para3)

{

    char buf[13] = {0};

    int len = recv(sock, buf, 13, 0);

	if (len != sizeof(UINT32)*3)

	{

	    cout << "Receive data error." << endl;

		return false;

	}

	para1 = *(UINT32*)buf;

	para2 = *(UINT32*)(buf+4);

	para3 = *(UINT32*)(buf+8);

	return true;

}



bool ReceiveSocketData(int sock, char *&pData, UINT32 len)

{

    pData = new char[len];

	char *p = pData;

	int recLen = 0;
	cout <<"ReceiveData: len = " <<  len << endl;

	while (recLen < len)

	{

		int rlen = recv(sock, p, len, 0);      // MAX_SIZE = 1024
		recLen += rlen;
		p += rlen;
        cout << "ReceiveData : rlen = " << rlen << " recLen = " << recLen << " p_size = " << (int)(p-pData)<< endl; 

	}
    cout <<"ReciveData: recLen = " << recLen << endl;

    if (recLen != len)

    {
        cout << "ReceiveData : recLen = " << recLen << " len = " << len << endl;

        delete []pData;

        pData = NULL;

        return false;

    }
    cout << "ReceiveData : Successful!." << endl;
    return true;

}



char DBStatus = CFPGA;

void* ProcessCommand(void *para)

{

    // 接收命令

	char cmd;

	int slaveSocket = (int)para;

	int len = recv(slaveSocket, &cmd, 1, 0);

	if (len != 1)

	{

	    cout << "Receive no cmd." << endl;

		close(slaveSocket);

		return NULL;

	}

#ifdef DEBUG

    cout << "Recv cmd = " << hex << ((int)cmd & 0xff) << endl;

#endif



    // 解析命令

    switch (cmd)
    {

    case CMD_WriteRam:
    {
	cout << "WriteRam : ";
	//打开总线访问
	InitializeAhb();	
	//从服务器获取要写入的地址,num是字节数
        UINT32 num, startAddr, endAddr;
        if (!ReceiveSocketData(slaveSocket, num, startAddr, endAddr))
            SendData(slaveSocket, RET_LOST_DATA);
        else
        {
	    cout << "num= " << num << " startAddr = " << hex << "0x" << startAddr << " endAddr = " << "0x" << endAddr << endl;
            SendData(slaveSocket, RET_OK);
	
	    //获取要写入的数据
            char *pData;
            if (!ReceiveSocketData(slaveSocket, pData, num))
            {
                cout << "Write data : ReceiveSocketData failed" << endl;
                SendData(slaveSocket, RET_LOST_DATA);
                break;
            }
        cout << "Write data : After ReceiveData" << endl;
/*            //检查参数
            int len = num / 4;
            if (startAddr + len != endAddr + 1 || num % 4 != 0 ||
                startAddr + len >= 0x100000)
            {
		cout << "Check arguments wrong." << endl;
                SendData(slaveSocket, RET_LOST_DATA);
                delete []pData;
                break;
            }
*/            //w
            int temp = num % 4;
            int len = num / 4;
            if(temp != 0){
                int needToAdd = num - len * 4;
                memset(pData + num,0,needToAdd);
                len += 1;
            }

	    cout << "Write data : num = " << num << " size of Recive data=" << sizeof(pData) << endl;
        cout << "Write data : First Byte of 0x" << hex << startAddr<<"="<< hex << (int)pData[0] << " ... Last Byte of 0x" << hex << endAddr << "=" << hex << (int)pData[num-1]<<endl;
        
	    //控制权交给CFPGA
            if (EnableSramAccess(CFPGA) < SUCCESS)
            {
                cout << "EnableSramAccess(CFPGA) fail;" << endl;
                SendData(slaveSocket, RET_FAILED);
                delete []pData;
                break;
            }
	
	    //数据写入SRAM
            int r = SUCCESS;
            for (UINT32 i = 0; i < len && r == SUCCESS; ++i)
                r = WriteSram(startAddr + i, *(((int*)pData)+i));

            if (r < SUCCESS || EnableSramAccess(EFPGA) < SUCCESS)
            {
                cout << "WriteSram failed or EnableSramAccess(EFPGA) failed." << endl;
                SendData(slaveSocket, RET_FAILED);
            }
            else
                SendData(slaveSocket, RET_OK);

            delete []pData;

        }
	//关闭总线访问
	UninitializeAhb();
        break;
    
    }
    case CMD_ReadRam:
    {

	cout << "ReadRam : ";
	//打开总线访问
	InitializeAhb();		

        UINT32 num, startAddr, endAddr;
	//从服务器接受要读取的地址
        if (!ReceiveSocketData(slaveSocket, num, startAddr, endAddr))
        {
            cout << "Read ram para error" << endl;
            SendData(slaveSocket, RET_LOST_DATA);
        }
        else
        {
            cout << "startAddr = " << hex << "0x" << startAddr << " endAddr = " << "0x" << endAddr << endl;

            // 检查参数
            int len = num / 4;
            if (startAddr + len != endAddr + 1 || num % 4 != 0 ||
                startAddr + len >= 0x100000)
            {
		cout << "Check arguments wrong." << endl;
                SendData(slaveSocket, RET_LOST_DATA);
                break;
            }

	    //控制交给CFPGA
            if (EnableSramAccess(CFPGA) < SUCCESS)
            {
		cout << "EnableSramAccess(CFPGA) failed." << endl;
                SendData(slaveSocket, RET_FAILED);
                break;
            }

	    //从SRAM中读取数据
            UINT32 *ram = new UINT32[len];
            int r = SUCCESS;
            for (UINT32 i = 0; i < len && r == SUCCESS; ++i)
            {
                r = ReadSram(startAddr + i, ram[i]);
            }
	    //检查读取结果并将控制权移交给EFPGA
            if (r < SUCCESS || EnableSramAccess(EFPGA) < SUCCESS)
            {
                cout << "ReadSram fail or EnableSramAccess(EFPGA) fail." << endl;
                SendData(slaveSocket, RET_FAILED);
            }
            else
	    {
 	    //传输读取到的数据到服务器            
		cout << "Read data : 0x" << startAddr<<"="<< ram[0] << " ... 0x" << endAddr << "=" << ram[len-1]<<endl;
            	cout << "Send size = " << hex << sizeof(UINT32)*len << endl;
	    
            	SendData(slaveSocket, RET_OK, (char*)ram, sizeof(UINT32)*len);
	    }

            delete []ram;
            char ch;

            recv(slaveSocket, &ch, 1, 0);

        }
	//关闭总线访问
	UninitializeAhb();
        break;

    }

    case CMD_WriteFpgaToFlash:
    {

        char ch;

        recv(slaveSocket, &ch, 1, 0);

        int x;

        recv(slaveSocket, (char*)&x, 4, 0);
        cout << "len=" << x << endl;

        SendData(slaveSocket, RET_OK);

        char *pData = 0;

        ReceiveSocketData(slaveSocket, pData, x);



        SendData(slaveSocket, RET_OK);
        for(int i=0; i<x;++i){
            cout <<(unsigned int)pData[i] << " ";
        }
        cout << endl;
        delete []pData;

        break;

    }

    case CMD_DownloadFpgaFromFlash:

    {

        char xx[2];

        recv(slaveSocket, xx, 2, 0);

#if 0

        // 下载控制FPGA

        char *buf = NULL;

        size_t len;

        bool b = GetFileContent("/home/plg/cfpga.rbf", buf, len);

        b = b && DownloadFPGA(buf, len, CFPGA);

        delete []buf;

        if (!b)

            SendData(slaveSocket, RET_FAILED);

        else

#endif

            SendData(slaveSocket, RET_OK);

        break;

    }

    case CMD_GetDBStatus:

        SendData(slaveSocket, RET_OK);

        SendData(slaveSocket, DBStatus);

        break;

    case CMD_OpenUsbByAddr:

    {

        char ch;

        recv(slaveSocket, &ch, 1, 0);
        cout << "OpenUsbByAddr:" << (int)ch << endl;

        SendData(slaveSocket, RET_OK);

        break;

    }

    case CMD_ResetUsb:

        SendData(slaveSocket, RET_OK);

        break;

    case CMD_ResetCpld:

        SendData(slaveSocket, RET_OK);//CheckHardware());

        break;

    case CMD_DownloadFpgaFromUsb:

    {

        char ch;

        recv(slaveSocket, &ch, 1, 0);

        int len;

        recv(slaveSocket, (char*)&len, 4, 0);

        cout << "Receive EFPGA size = " << dec << len << endl;

        SendData(slaveSocket, RET_OK);

        char *pData = 0;

        if (!ReceiveSocketData(slaveSocket, pData, len))

        {

            cout << "Receive EFPGA failed " << endl;

            SendData(slaveSocket, RET_LOST_DATA);

        }

        else

        {

            cout << "Receive EFPGA success " << endl;

            SendData(slaveSocket, DownloadEFPGA(pData, len));

            ofstream fout("rbf");
            fout << pData << endl;
            fout.close();
        }

        delete []pData;

        break;

    }

    case CMD_ResetExam:

    {

        char *buf = NULL;

        size_t len;

#if 0

        // 下载实验FPGA

    	bool b = GetFileContent("/home/plg/efpga.rbf", buf, len);

        b = b && DownloadFPGA(buf, len, EFPGA);

        delete []buf;

        SendData(slaveSocket, b);

#else

        SendData(slaveSocket, RET_OK);

#endif

        break;

    }

    case CMD_GetDeviceInfo:

    {

        char buf[2] = {14, 18};

        SendData(slaveSocket, RET_OK, buf, 2);

        break;

    }

    case CMD_SetDeviceInfo:

        SendData(slaveSocket, RET_OK);

        break;

    case CMD_SetDBStatus:

        recv(slaveSocket, &DBStatus, 1, 0);

        SendData(slaveSocket, RET_OK);

        break;

    case CMD_SetDataBus:

    {

        char buf[8];

        recv(slaveSocket, buf, 8, 0);

        SendData(slaveSocket, RET_OK);

        break;

    }

    case CMD_GetRegs:
    {
        //ReadRegisters(slaveSocket);
        int registers[256] = {0};
        registers[254] = result;
        SendData(slaveSocket, RET_OK, (char*)registers, sizeof(UINT32)*256);
        break;
    }


    case CMD_SendCycles:

    case CMD_FlipCycles:

    {

        int clock;

        recv(slaveSocket, (char*)&clock, 4, 0);

        SendData(slaveSocket, RET_OK);

        break;

    }

    case CMD_WriteRegs:
    {
        int addr,num;
        recv(slaveSocket, (char*)&addr,4,0);
        recv(slaveSocket, (char*)&num,4,0);
        SendData(slaveSocket,RET_OK);
        input = num;
        calculateResult();
        break;

    }

    case CMD_ExprimentType:
    {
        char tp;
        recv(slaveSocket,(char*)&tp,1,0);
        SendData(slaveSocket, RET_OK);
        type = tp;
    }

    /*

    case CMD_WRITE_REGISTER:

    {

        UINT32 addr, num;

        if (!ReceiveSocketData(slaveSocket, addr, num))

            SendData(slaveSocket, RET_PARA_ERROR);

        else

            SendData(slaveSocket, WriteEFpgaRegister(addr, num));

        break;

    }



    case CMD_READ_FLASH:

        SendData(slaveSocket, RET_OK);

        break;

*/

    default:

        SendData(slaveSocket, RET_OK);

        break;

    }

    // 释放资源

    close(slaveSocket);

	return NULL;

}

