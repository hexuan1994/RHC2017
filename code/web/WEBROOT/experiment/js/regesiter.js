/*
 * 控制寄存器显示 这里由于未用到寄存器，所有寄存器设置为0
 */

$(function() {
	initReg();
});

function initReg() {
	// init regesiter number
	for (var i = 0; i < 256; i++) {
		var tmpI = ""+i;
		if (i<10) tmpI = "00"+i;
		else if(i<100) tmpI = "0"+i;

		var regI = $('<tr><td>'+tmpI+'</td><td>00000000</td>');
		$("#regs").append(regI);
	}
}