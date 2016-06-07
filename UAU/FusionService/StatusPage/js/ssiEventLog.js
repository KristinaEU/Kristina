var eventQuequeSize = 10;
var eventRefreshIntervall = 500;
var eventQueque = new Array(eventQuequeSize);
var eventWhiteList = new Array();
var clearedAt = 0;
var lastEventAt = 0;

function getEvents() {
    var eventList;
    $.get("http://137.250.171.230:11193/Fusion/Service.svc/getEventQueque", function (data) {
        for (i = data.length - 1 ; i >= 0; i--) {
            try {
                var parsed = parseXML(data[i]);
            }
            catch (e) {
                continue;
            }
            if (parsed != false && parsed.time > clearedAt) {
                console.log("pt: " + parsed.time + " / ca: " + clearedAt + " / la: " + lastEventAt );
                if (!quequeContainsEvent(parsed.string))
                    eventQueque.unshift(parsed.string);
                if (eventQueque.length >= eventQuequeSize)
                    eventQueque.pop();

            }
        }
        updateList();
    });
}

function quequeContainsEvent(event) {

    for (var val in eventQueque) {
        if (!eventQueque[val].localeCompare(event))
            return true;
    }

    return false
}

function updateList() {
    var list = "<table><thead><tr><th>Sender</th><th>Time</th><th>Value</th><th>State</th></tr></thead><tbody>";
    for (i = 0; i < eventQueque.length; i++) {
        //list += "<li>" + eventQueque[i] + "</li>";
        list += eventQueque[i];
    }
    list += "</tbody></table>";
    var div = document.getElementById("eventDiv");
    div.innerHTML = list;
}

function createEventList(entries) {
    var list = "";
    for (i = 1; i < entries; i++) {
        list += "<li>" + i + "</li>";
    }
    var div = document.getElementById("eventDiv");
    div.innerHTML = list;
}

function parseXML(xml) {
    xmlDoc = $.parseXML(xml);
    $xml = $(xmlDoc);

    var event = $xml.find('event');
    var sender = $(event).attr('event').toLowerCase() + '@' + $(event).attr('sender').toLowerCase();
    var time = parseInt($(event).attr('from'));

    //in case of newer event or pipeline-reset update the time of the most current event
    if (time > lastEventAt || time == 0)
        lastEventAt = time;

    //return false if sender not whitelisted
    if (!(eventWhiteList.indexOf(sender) == -1)) {
        var state = $(event).attr('state');
        
        var type = $(event).attr('type');
        var value = '';
        if (!type.localeCompare('EMPTY')) {
            value = 'EMPTY';
        }
        else if (!type.localeCompare('NTUPLE') || !type.localeCompare('MAP')) {
            var v, a;
            v = $xml.find('tuple');
            a = $(v).next();

            value = 'Valence:' + (Math.round(v.attr('value') * 10000) / 10000).toFixed(4) + ' | Arousal:' + (Math.round(a.attr('value') * 10000) / 10000).toFixed(4);
        }
        else if (!type.localeCompare('STRING')) {
            value = chunk($(event).text(), 30).join('<br>');
        }

        var ret = {string:'<tr><td>' + sender + '</td><td>' + time + '</td><td>' + value + '</td><td>' + state + '</td>', time:time, sender:sender};

        return ret;
    }
    else
        return false;
}

function chunk(str, n) {
    var ret = [];
    var i;
    var len;

    for (i = 0, len = str.length; i < len; i += n) {
        ret.push(str.substr(i, n))
    }

    return ret
};

function displayEvent(el) {
    var sender = el.name;
    var toggled = el.checked;
    if (toggled)
        eventWhiteList.push(sender)
    else {
        var i = eventWhiteList.indexOf(sender);
        if (i != -1) {
            eventWhiteList.splice(i, 1);
        }
    }
    clearedAt = lastEventAt;
}

function clearEventList() {
    eventQueque = new Array(eventQuequeSize);
    for (i = 0; i < eventQuequeSize; i++) {
        eventQueque[i] = ''
    }
    clearedAt = lastEventAt;
}

function resetEventList() {
    clearEventList();
    clearedAt = 0;
    lastEventAt = 0;
}

function initEventLog() {
    clearEventList();
    getEvents();
    var timer = setInterval(getEvents, eventRefreshIntervall);
}
