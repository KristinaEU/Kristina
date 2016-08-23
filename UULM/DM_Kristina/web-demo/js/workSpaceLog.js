var workSpaceQuequeSize = 10;
var workSpaceRefreshIntervall = 500;
var workSpaceQueque = new Array(workSpaceQuequeSize);
var workSpaceWhiteList = new Array();
var selection;

function getWorkspace() {
    var workSpaceList;
	clearWorkspace();
    $.get("http://localhost:11153/workspace", function (data) {
		selection = data.selection;
		data = data.workspace;
        for (i = data.length - 1 ; i >= 0; i--) {
            var parsed = data[i];
            workSpaceQueque.unshift(parsed);
            workSpaceQueque.pop();
        }
        updateWorkspace();
    });
}

function updateWorkspace() {
    var list = "<table><thead><tr><th>Workspace</th></tr></thead><tbody>";
    for (i = 0; i < workSpaceQueque.length; i++) {
    	var selected = false;
    	for(j = 0; j < selection.length; j++){
    		if(selection[j] == workSpaceQueque[i]){
    			list += "<tr class=\"sel\"><td>"+workSpaceQueque[i]+"</td></tr>";
    			selected = true;
    		}
    	}
		if(selected == false){
			list += "<tr><td>"+workSpaceQueque[i]+"</td></tr>";
		}
    }
    list += "</tbody></table>";
    var div = document.getElementById("workspaceDiv");
    div.innerHTML = list;
}

function clearWorkspace() {
    workSpaceQueque = new Array(workSpaceQuequeSize);
    for (i = 0; i < workSpaceQuequeSize; i++) {
        workSpaceQueque[i] = ''
    }
}

function initWorkspaceLog() {
    getWorkspace();
    var timer = setInterval(getWorkspace, workSpaceRefreshIntervall);
}
