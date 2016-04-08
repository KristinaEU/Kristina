var eventQuequeSize = 10;
var eventRefreshIntervall = 500;
var eventQueque = new Array(eventQuequeSize);
var eventWhiteList = new Array();
var selection;

function getEvents() {
    var eventList;
	clearEventList();
    $.get("http://137.250.171.232:11153/workspace", function (data) {
		selection = data.selection;
		data = data.workspace;
        for (i = data.length - 1 ; i >= 0; i--) {
            var parsed = data[i];
            eventQueque.unshift(parsed);
            eventQueque.pop();
        }
        updateList();
    });
}

function updateList() {
    var list = "<table><thead><tr><th>Workspace</th></tr></thead><tbody>";
    for (i = 0; i < eventQueque.length; i++) {
		if(selection == eventQueque[i]){
			list += "<tr class=\"sel\"><td>"+eventQueque[i]+"</td></tr>";
		}
        else{
			list += "<tr><td>"+eventQueque[i]+"</td></tr>";
		}
    }
    list += "</tbody></table>";
    var div = document.getElementById("eventDiv");
    div.innerHTML = list;
}

function clearEventList() {
    eventQueque = new Array(eventQuequeSize);
    for (i = 0; i < eventQuequeSize; i++) {
        eventQueque[i] = ''
    }
}

function initEventLog() {
    getEvents();
    var timer = setInterval(getEvents, eventRefreshIntervall);
}
