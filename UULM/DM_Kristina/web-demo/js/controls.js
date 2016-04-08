function sendVA(){
	
	var v = document.getElementById("val").value;
	var a = document.getElementById("ar").value;
	$.post("http://137.250.171.232:11153/sendVA?valence="+v+"&arousal="+a);
};

function sendRequestFluid(){
	$.post("http://137.250.171.232:11153/sendRequestFluid");
};