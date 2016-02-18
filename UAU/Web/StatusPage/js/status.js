var dataRefreshIntervall = 500;
var pipeIsRunning, pipeIsReceiving, vsmIsRunning, lastchecked;

function getFusionStatus() {
    $.get("http://137.250.171.230:11193/Fusion/Service.svc/getStatus", function (data) {
        var nd = new Date();
        lastchecked = "Last checked: " + nd.getHours() + ':' + (nd.getMinutes() < 10 ? '0' + nd.getMinutes() : nd.getMinutes()) + ':' + (nd.getSeconds() < 10 ? '0' + nd.getSeconds() : nd.getSeconds()) + ' - ' + nd.getDay() + '.' + (nd.getMonth() + 1) + '.' + nd.getYear() + '</br>';
        pipeIsReceiving = data.PipeIsReceiving;
        pipeIsRunning = data.PipeIsRunning;
        vsmIsRunning = data.VsmIsRunning;
        update();
    });
};

function update() {

    // Updating the SSI PIPELINE circle
    //Statuscode:
    //  0 = UP
    //  1 = DOWN
    //  2 = UNKNOWN
    var pipeRunIcon = document.getElementById("pipeRunIcon");
    var pipRecIcon = document.getElementById("pipeResIcon");
    var vsmRunIcon = document.getElementById("vsmRunIcon");

    var lc = document.getElementById("lastChecked");
    lc.innerHTML = lastchecked
    if (pipeIsRunning == 0)
    {
        pipeRunIcon.style.backgroundImage = 'url(../img/icons/up.png)';
    }  
    else if (pipeIsRunning == 1) {
        pipeRunIcon.style.backgroundImage = 'url(../img/icons/down.png)';
    }
    else if (pipeIsRunning == 2) {
        pipeRunIcon.style.backgroundImage = 'url(../img/icons/neutral.png)';
    }

    if (pipeIsReceiving == 0) {
        pipeResIcon.style.backgroundImage = 'url(../img/icons/up.png)';
    }
    else if (pipeIsReceiving == 1) {
        pipeResIcon.style.backgroundImage = 'url(../img/icons/down.png)';
    }
    else if (pipeIsReceiving == 2) {
        pipeResIcon.style.backgroundImage = 'url(../img/icons/neutral.png)';
    }

    // Updating the VSM runtime status circle
    if (vsmIsRunning == 0) {
        vsmRunIcon.style.backgroundImage = 'url(../img/icons/up.png)';
    }
    else if (vsmIsRunning == 1) {
        vsmRunIcon.style.backgroundImage = 'url(../img/icons/down.png)';
    }
    else if (vsmIsRunning == 2) {
        vsmRunIcon.style.backgroundImage = 'url(../img/icons/neutral.png)';
    }

}

function initStatusCheck() {
    getFusionStatus();
    var timer = setInterval(getFusionStatus, dataRefreshIntervall);
}