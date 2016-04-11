function sendVA(){
	
	var v = document.getElementById("val").value;
	var a = document.getElementById("ar").value;
	$.post("http://localhost:11153/sendVA?valence="+v+"&arousal="+a);
};

function sendRequestFluid(){
	$.post("http://localhost:11153/sendRequestFluid");
};