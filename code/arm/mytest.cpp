#include<iostream>


int main(int argc, char* argv[])
{
#ifndef DEBUG
    std::cout << "Debug!"<< std::endl;
#endif
    std::cout << "Program Start."<< std::endl;
    return 0;
}