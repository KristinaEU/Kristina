var dataRefreshIntervall = 500;
var almaRunning, almaRunning, lastchecked;

function getDMStatus() {
    $.get("http://localhost:11153/status", function (data) {
        var nd = new Date();
        lastchecked = "Last checked: " + nd.getHours() + ':' + (nd.getMinutes() < 10 ? '0' + nd.getMinutes() : nd.getMinutes()) + ':' + (nd.getSeconds() < 10 ? '0' + nd.getSeconds() : nd.getSeconds()) + ' - ' + nd.getDay() + '.' + (nd.getMonth() + 1) + '.' + nd.getYear() + '</br>';
        owlRunning = data.owlRunning;
        almaRunning = data.almaRunning;
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
    if (owlRunning == 0)
    {
        pipeRunIcon.style.backgroundImage = 'url(../img/icons/up.png)';
    }  
    else if (owlRunning == 1) {
        pipeRunIcon.style.backgroundImage = 'url(../img/icons/down.png)';
    }
    else if (owlRunning == 2) {
        pipeRunIcon.style.backgroundImage = 'url(../img/icons/neutral.png)';
    }

    if (almaRunning == 0) {
        pipeResIcon.style.backgroundImage = 'url(../img/icons/up.png)';
    }
    else if (almaRunning == 1) {
        pipeResIcon.style.backgroundImage = 'url(../img/icons/down.png)';
    }
    else if (almaRunning == 2) {
        pipeResIcon.style.backgroundImage = 'url(../img/icons/neutral.png)';
    }


}

function initStatusCheck() {
    getDMStatus();
    var timer = setInterval(getDMStatus, dataRefreshIntervall);
}