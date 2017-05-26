#include <iostream>
using namespace std;
int type;
int input;
int result;

int counter = 0;


int getAdderResult(int input){
	cout << input << endl;
	int result = (input >> 8) & 1;

	for(int i=0;i<4;++i){
		result += (input) & (1<<i);
		result += (input >> 4) & (1<<i);
	}
	return result;
}

int getPlexerResult(int input){
    int sec = 0;
    int result = 0;
    sec += (input >> 4);
    result = (input >> sec) & 1;
    return result;
}


int getDecoderResult(int input){
    int result = 0;
    switch(input){
        case 0:
            result = 63;
            break;
         case 1:
            result = 6;
            break;
         case 2:
            result = 91;
            break;
         case 3:
            result = 79;
            break;
         case 4:
            result = 102;
            break;
         case 5:
            result = 109;
            break;
         case 6:
            result = 125;
            break;
         case 7:
            result = 7;
            break;
         case 8:
            result = 127;
            break;
         case 9:
            result = 111;
            break;
         case 10:
            result = 119;
            break;
         case 11:
            result = 124;
            break;
         case 12:
            result = 57;
            break;
         case 13:
            result = 94;
            break;
         case 14:
            result = 121;
            break;
         case 15:
            result = 113;
            break;
    }
    return result;
}

int getDecimalResult(int input){
    int result = getAdderResult(input);
    int low = input % 16;
    int high = input / 16;
    cout << low << " " << high << endl;
    int dec =  getDecoderResult(low) + (getDecoderResult(high)<<7);
    low = result % 10;
    high = result / 10;
    cout << low << " " << high << endl;
    dec += getDecoderResult(low) << 14;
    dec += getDecoderResult(high)<<21;
    return dec;
}


int getCounterResult(int input){
    int reset = input & 1;
    int clk = (input >> 1) & 1;
    if(reset == 0)
        counter = 0;
    else if(clk == 1){
        counter += 1;
    }

    int low = counter % 10;
    int high = (counter % 100) / 10;
    cout << low << " " << high << endl;
    return getDecoderResult(low) + (getDecoderResult(high) << 7);

}

int getStateResult(int input){
    int reset = input & 1;
    int clk = (input >> 1) & 1;
    if(reset == 0)
        counter = 4;
    else if(clk == 1){
        counter = 16;
    }

    return counter;
}

void calculateResult(){

	switch(type){
	case 1:
		result = getAdderResult(input);
		cout << result << endl;
		break;
        case 2:
            result = getDecoderResult(input);
            cout << result << endl;
            break;
        case 3:
            result = getPlexerResult(input);
            cout << result << endl;
            break;
        case 4:
            result = getDecimalResult(input);
            cout << result << endl;
            break;
        case 5:
            result = getCounterResult(input);
            cout << result << endl;
            break;
        case 6:
            result = getStateResult(input);
	}

	 cout << "input=" << input <<", result=" << result << endl;
}
