#include<stdio.h>
#include<fcntl.h>
#include<unistd.h>
#include<stdlib.h>
#include<string.h>
#include<time.h>

#define N 1024
#define mem_addr_base 0x100000
#define efpga_reg_base 0x080000
#define LOW(a) (unsigned short)(a & 0xffff)
#define HIGH(a) (unsigned short)((a>>16) & 0xffff)
#define LONG(d_h,d_l) (((unsigned int)d_h) << 16) |((unsigned int)d_l)

//**************************************测试数据
unsigned short ahb_read (unsigned int offset)
{
	int fd;
	int rc;
	unsigned short date;

	fd = open("/dev/fpga_ahb",O_RDWR);
	if(fd<0)
	{
		printf("open error!!\n");
		return -1;
	}
	rc = pread(fd,&date,2,offset);
	if (rc<0)
	{
		printf("read error!!\n");
		return -1;
	}
	printf(" 读出总线值：%x\n",date);
	close(fd);
	return date;
}

int ahb_write(unsigned int offset , unsigned short *date)
{
	int fd;
	int rc;

	fd = open("/dev/fpga_ahb",O_RDWR);
	if(fd<0)
	{
		printf("open error!!\n");
		return -1;
	}
	rc = pwrite(fd,date,2,offset);
	if(rc<0)
	{
		printf("write error !!\n");
		return -1;
	}
	printf(" 写入总线地址：%x \n",offset);
	printf(" 写入总线值：%x \n",*date);
	close(fd);
	//printf("ok!\n");
	return 0;
}

unsigned short cfpga_read(unsigned int offset)
{
	unsigned short date;
	if(offset < 0 || offset > 0x03ffff)
	{
		printf("cfpga read addr error!\n");
		return -1 ;
	}
	date = ahb_read(offset);
	return date;
}

int cfpga_write(unsigned int offset , unsigned short date)
{
	if (offset < 0 || offset > 0x03ffff)
	{
		printf("cfpga write addr error!\n");
		return -1 ;
	}
	ahb_write(offset,&date);
	return 0;
}

int mem_write (unsigned int offset , unsigned int date)
{
	unsigned short date_H,date_L;
	unsigned int mem_addr;
	if(offset < 0 || offset > 0xfffff)
	{
		printf("mem write addr error !!\n");
		return -1 ;
	}
	mem_addr=mem_addr_base + offset ;
	date_H=HIGH(date);
	date_L=LOW(date);
	ahb_write(0x2,&date_H);
	ahb_write(mem_addr,&date_L);
	return 0;
}

unsigned int mem_read(unsigned int offset)
{
        unsigned int date;
	unsigned short date_H,date_L;
	unsigned int mem_addr;
	if(offset < 0 || offset > 0xfffff)
	{
		printf("mem read addr error!!\n");
		return -1 ;
	}
	mem_addr=mem_addr_base + offset ;
	ahb_read(mem_addr);
	date_H=ahb_read(0x3);
	date_L=ahb_read(0x4);
	date=LONG(date_H,date_L);
	return date;

}


int efpga_write (unsigned int offset , unsigned short  date)
{
	unsigned int efpga_addr;
	unsigned short dataH,dataL;

	if(offset < 0 || offset > 0xff)
	{
		printf("efpga write addr error !!\n");
		return -1 ;
	}
//	printf("Efpga_write(%x,%x)\n",offset,date);
	efpga_addr = efpga_reg_base + offset;
	dataH=HIGH(date);
	dataL=LOW(date);
//	printf("efpga_addr=%x+%x\n",efpga_reg_base,offset);
//	printf("Efpga_reg=%x\n",efpga_addr);
	ahb_write(0x6,&dataH);
	ahb_write(efpga_addr,&dataL);
	return 0;
}

unsigned int efpga_read(unsigned int offset)
{
	unsigned short dataL,dataH;
	unsigned int date;
	unsigned int efpga_addr;
	if(offset < 0 || offset > 0xff)
	{
		printf("efpga read addr error!!\n");
		return -1;
	}
	efpga_addr=efpga_reg_base + offset;
	dataL=ahb_read(efpga_addr);
	dataH=ahb_read(0x7);
	date=LONG(dataH,dataL);
//	printf("dataL =%x \n",dataL);
//	printf("dataH =%x \n",dataH);
//	printf("date =%x \n",date);
	return date;

}

int c_mem_en()
{
	unsigned short date;
	date=0x0;
	ahb_write(0x5,&date);
	printf("efpga:disable\n");
	printf("cfpga:enable\n");
	return 0;
}

int e_mem_en()
{
	unsigned short date;
	date=0x1;
	ahb_write(0x5,&date);
	printf("cfpga:disable\n");
	printf("efpga:enable\n");
	return 0;
}

int main()
{

	char s1[N];
	const char * split = "(,)";
	char * p;
	char * cmd ;
	int i = 0 ;
	unsigned int offset;
	unsigned int date;
	unsigned short ahb_date;
	unsigned short read_d;
	unsigned int memdate_read;
//------------------------------------1
	unsigned short int Ver_Cfpga = 0x0;
	unsigned short int REG_8 = 0x8;	//数码管0-15 	ONLY write
	unsigned short int REG_9 = 0x9;	//数码管16-31	ONLY read
	unsigned short int REG_A = 0xA;	//数码管32-47	ONLY read
	unsigned short int REG_B = 0xB;	//数码管48-55	ONLY read
//------------------------------------2
	unsigned int cfpga_write_data;
	unsigned int cfpga_write_data_back;
	int cfpga_reg=0x1;
//******************************************************
//四位加法器测试，实验FPGA 下载好测试代码，通过软件对数码管进行写入数据操作，通过控制FPGA对数码管总线进行操作，是否实验FPGA获取成功，在获取成功后实验FPGA进行处理后返回总线，检查实验FPGA能否检测并获取到
//	unsigned short int WREG_FB =0xFB;		//写入的寄存器地址
//	unsigned short int WREG_ADDER_DATA = 0x0087;	//写入到数据 0000 F:0000 B:1000 A 0001
//	unsigned short int WREG_ADDER = 0xFC;		//结果寄存器	
//	unsigned short int WREG_ADDER_RESULT;		//读出数据应该是  0000 0000 0000 1111
//------------------------------------3
	unsigned int write_mem_data = 0x1234abcd;
	int write_mem_addr = 0x108;
//------------------------------------4
	unsigned int read_mem_data;
//------------------------------------5
	int read_Efpga_ver;
	int read_Efpga_VFD;
 	int Ver_Efpga = 0x0;
	int Efpga_reg_FD=0xFD;
//------------------------------------6
	int Efpga_reg=0x1;
	unsigned int write_Efpga_data = 0x1234;
	unsigned int readback_Efpga_data;
//------------------------------------7
//------------------------------------8
//------------------------------------
	char cmd_cfpga_read[]="cfpga_read";
	char cmd_cfpga_write[]="cfpga_write";
	char cmd_mem_read[]="mem_read";
	char cmd_mem_write[]="mem_write";
	char cmd_efpga_read[]="efpga_read";
	char cmd_efpga_write[]="efpga_write";
	char cmd_c_mem_en[]="c_mem_en";
	char cmd_e_mem_en[]="e_mem_en";

	while(1)
	{

	fgets(s1,N,stdin);
	if(s1[strlen(s1) -1 == '\n']){
		s1[strlen(s1)] = '\0';
	}
	fflush(stdin);
	/*printf("%s",s1);*/

	p = strtok (s1,split);
	while(p !=NULL){
	printf("%s\n",p);
	if(i==0)
	{cmd = p;}
	if (i==1)
	{offset =strtoul(p,0,0);}
	if (i==2)
	{date = strtoul(p,0,0);}
	p = strtok(NULL,split);
	i++;
	}
/*****************add cmd *******************/
	printf("*************\n");
	printf("*Test Begin!*\n");
	printf("*************\n");
//**************************************************test access cfpga
    		cmd=cmd_cfpga_read; //
	if (strcmp(cmd,"cfpga_read")==0)//
	{
		printf("1.控制FPGA读测试:");
		//***************************版本验证
	        read_d = cfpga_read(Ver_Cfpga); 
		printf("  read date = %x\n",read_d);

		if (read_d==0x0472)
		{
			printf("  (Right)读控制FPGA正确！\n");
			
		}
		else
		{
			printf("  (Error)读控制FPGA错误！\n");
			
		}
	}
//**************************************************写控制FPGA测试
		sleep(1);
		cmd=cmd_cfpga_write;
	if(strcmp(cmd,"cfpga_write")==0)
	{
		cfpga_write_data=0x1234;			//写入读数值
		printf("2.控制FPGA写测试：\n");
		printf("写入到FPGA里到数据是：%x \n",cfpga_write_data);
		ahb_date=LOW(cfpga_write_data);			//把写如数据放到AHB总线上
		printf("写到总线上到数据是：%x \n",ahb_date);
		cfpga_write(cfpga_reg,ahb_date);		//控制FPGA写函数
		printf("写入到寄存器地址：%x \n",cfpga_reg);
		sleep(1);
		cfpga_write_data_back = cfpga_read(cfpga_reg);	//读出刚写入到寄存器中读数据
		printf("读取寄存器 %x 的数值是：%x \n",cfpga_reg,cfpga_write_data_back);
		if(cfpga_write_data_back==cfpga_write_data)	//与读取刚写入寄存器中读数据进行对比
		{
			printf("  (Right)写FPGA正确!\n");
			
		}
		else
		{
			printf("  (Error)写FPGA错误!!\n");
			
		}
//***************************************************************加法器测试
/*		printf("  加法器测试：\n");
		printf("  写入数据：%x\n",WREG_ADDER_DATA);
		printf("  写入到寄存器：%x\n",WREG_FB);
		ahb_date=LOW(WREG_ADDER_DATA);
		printf("  写入总线完成!\n");
		cfpga_write(WREG_FB,ahb_date);
		printf("  写入控制FPGA完成!\n");
		printf("  开始读取控制FPGA：\n");
		sleep(1);
		WREG_ADDER_RESULT = cfpga_read(0xFC);
		printf("  读取控制FPGA结果：%x\n",WREG_ADDER_RESULT);*/
//*******************************************************通过软件对数码管进行点亮测试
		cfpga_write_data=0xAAAA;	//要写入的数据
		ahb_date=LOW(cfpga_write_data);	//写到总线
		cfpga_write(0x8,ahb_date);	//总线上到数据写到控制FPGA寄存器8中
		sleep(1);
		read_d = cfpga_read(REG_8);
		printf(" REG8=%x\n",read_d);
		read_d = cfpga_read(REG_9);
		printf(" REG9=%x\n",read_d);
		read_d = cfpga_read(REG_A);
		printf(" REGA=%x\n",read_d);
		read_d = cfpga_read(REG_B);
		printf(" REGB=%x\n",read_d);
	}
//**************************************************写内存
		cmd=cmd_mem_write;
		c_mem_en();
	if(strcmp(cmd,"mem_write")==0)
	{
		printf("3.写内存测试:");
		mem_write(write_mem_addr,write_mem_data);
		sleep(1);
		printf("  mem_addr = %x",write_mem_addr);
		printf("  写入数据 = %x\n",write_mem_data);
	//	printf("  Done!\n");
	}

//**************************************************读实验FPGA测试
		cmd=cmd_efpga_read;
	if (strcmp(cmd,"efpga_read")==0)
	{
		printf("5.EFPGA读测试：");
	        read_Efpga_ver = efpga_read(Ver_Efpga);
		read_Efpga_VFD=	efpga_read(Efpga_reg_FD);//测试寄存器FD的值 0xffffffff
		printf("  read date = %x\n",read_Efpga_ver);
		
	}
	if(read_Efpga_ver==0x20150121) 
	{
	printf("  (Right)读EFPGA正确！\n");
	}
	else
	{
	printf("  (Error)读EFPGA错误！！\n");
	}
		printf("  实验FPGA寄存器FD的值 = %x\n",read_Efpga_VFD);
//**************************************************写实验FPGA测试
		cmd=cmd_efpga_write;
	if(strcmp(cmd,"efpga_write")==0)
	{
		printf("6.EFPGA写测试：");
		ahb_date=LOW(write_Efpga_data);
		printf("  ahb_date=%x！",ahb_date);
		efpga_write(Efpga_reg,ahb_date);
		readback_Efpga_data=efpga_read(Efpga_reg);
//		printf("  Efpga_reg_HERE=%x！\n",Efpga_reg);
		printf("  readback=%x\n",readback_Efpga_data);
		if (readback_Efpga_data==0x1234)
		{
		printf("  (Right)写EFPGA正确！\n");
		}
		else
		{
		printf("  (Error)写EFPGA错误！！\n");
		}
//		printf("ok!\n");
	}
//**************************************************读内存
		cmd=cmd_mem_read;
		c_mem_en();
	if (strcmp(cmd,"mem_read")==0)
	{
		printf("4.读内存测试:");
	    
		for(int i = 0;i < 12;i++){
			read_mem_data = 0;
			read_mem_data = mem_read(i);
			printf(" 0x%x = %x\n",i,read_mem_data);
		}
//		printf("  mem_addr = %x",write_mem_addr);
//		printf("  读出数据 = %x\n",read_mem_data);
	//	printf("  Done!\n");
	}
	if (write_mem_data==read_mem_data)
	{
		printf("  (Right)内存读写测试正确!\n");
		
	}	
	else
	{
		printf("  (Error)内存读写测试错误!!\n");
		
	}
/*	
//**************************************************
		cmd=cmd_c_mem_en;
	if (strcmp(cmd,"c_mem_en")==0)
	{
		printf("7.c_mem_en OK!!\n");
		c_mem_en();
	}
//**************************************************
		cmd=cmd_e_mem_en;
	if(strcmp(cmd,"e_mem_en")==0)
	{
		printf("8.e_mem_en OK!!\n");
		e_mem_en();
	}
*/
	i=0;
	}
//**************************************************EFPGA read LED

//***************************************************
	getchar();
	return 0;
}
