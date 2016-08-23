function sendVA(){
	
	var v = document.getElementById("val").value;
	var a = document.getElementById("ar").value;
	$.post("http://localhost:11153/sendVA?valence="+v+"&arousal="+a);
};

function sendRequestFluid(){
	$.post("http://localhost:11153/sendRequestFluid");
};

function sendUsrAct(){
	var usrA = document.getElementById("usrAct").value;
	$.post("http://localhost:11153/usrAct?action="+usrA);
};

function sendSysAct(){
	var sysA = document.getElementById("sysAct").value;
	$.post("http://localhost:11153/sysAct?action="+sysA);
};