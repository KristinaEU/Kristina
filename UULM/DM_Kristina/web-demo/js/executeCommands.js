function executeCommmand() {

    var target, command;
    var e = document.getElementById("target");
    target = e.options[e.selectedIndex].value;
    e = document.getElementById("command");
    command = e.options[e.selectedIndex].value;
    var data = { Target: target, Command: command };
    var jsondata = JSON.stringify(data);

    $.ajax({
        type: 'POST',
        url: "http://137.250.171.230:11193/Fusion/Service.svc/executeCommand",
        data: jsondata,
        dataType: 'JSON',
        contentType: "application/json",
        error: function (err) {
            var returnMessageLabel = document.getElementById("returnMessage");
            returnMessageLabel.innerHTML = "Service-Error: " + err;
        },
        success: function (data) {
            var returnMessageLabel = document.getElementById("returnMessage");
            returnMessageLabel.innerHTML = "Service-Answer: " + data;
        }
    });
}

function toggleEx(el) {
    var value = el.options[el.selectedIndex].value;
    var commandSelector = document.getElementById("command");
    commandSelector.options.length = 0;


    if (value.localeCompare("SSI") == 0) {
        appendOption(commandSelector, 'start/stop');
    }
    else if (value.localeCompare("VSM") == 0) {
        appendOption(commandSelector, 'load');
        appendOption(commandSelector, 'unload');
        appendOption(commandSelector, 'config');
        appendOption(commandSelector, 'states');
        appendOption(commandSelector, 'status');
        appendOption(commandSelector, 'start');
        appendOption(commandSelector, 'stop');
    }
}

function appendOption(el, option) {
    var opt = document.createElement('option');
    opt.appendChild(document.createTextNode(option));
    opt.value = option; // set value property of opt
    el.appendChild(opt); // add opt to end of select box (sel)
}