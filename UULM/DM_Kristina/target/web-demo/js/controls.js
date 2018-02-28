function sendVA(){
	
	var v = document.getElementById("val").value;
	var a = document.getElementById("ar").value;
	$.post("http://172.31.26.245:11150/sendVA?valence="+v+"&arousal="+a);
};

function sendRequestFluid(){
	$.post("http://172.31.26.245:11150/sendRequestFluid");
};