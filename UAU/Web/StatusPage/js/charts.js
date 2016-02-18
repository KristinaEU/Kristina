var dataRefreshIntervall = 500;
var lineChartUpdateIntervall = 500;
var scatterChartAnimationDuration = 5000;
var maxElements = 20;
var gestureTableData = [];
var mimicTableData = [];
var prosodyTableData = [];
var fusionTableData = [];

function getVA() {
    $.get("http://137.250.171.230:11193/Fusion/Service.svc/getEmotion", function (data) {
        for (var i = 0; i < data.m_Item1.length ; i++) {
            var a = data.m_Item1[i].Arousal;
            var v = data.m_Item1[i].Valence;
            var t = new Date();
            var dataObject = { time: t, valence: v, arousal: a };
            var orig = data.m_Item1[i].Origin;
            
            if (!orig.localeCompare("GESTURE")) {
                if (gestureTableData.length >= maxElements)
                    gestureTableData.shift();
                gestureTableData.push(dataObject);
            }
            else if (!orig.localeCompare("MIMIC")) {

                if (mimicTableData.length >= maxElements)
                    mimicTableData.shift();
                mimicTableData.push(dataObject);
            }
            else if (!orig.localeCompare("PROSODY")) {
                if (prosodyTableData.length >= maxElements)
                    prosodyTableData.shift();
                prosodyTableData.push(dataObject);
            }
            else if (!orig.localeCompare("FUSION")) {
                if (fusionTableData.length >= maxElements)
                    fusionTableData.shift();
                fusionTableData.push(dataObject);
            }
        }
    });
};

function drawLineChart(chartId, title) {

    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Time');
    data.addColumn('number', 'Valence');
    data.addColumn('number', 'Arousal');

    var options = {
        title: title,
        titleTextStyle: {
            fontName: 'Source Sans Pro',
            fontSize: 19
        },
        curveType: 'function',
        vAxis: {
            viewWindowMode: 'explicit',
            viewWindow: {
                max: 1,
                min: -1
            },
        },
        hAxis: {
            textPosition: 'none',
        }
            /*animation: {
                duration: tableRefreshTime,
                easing: 'linear',
            }*/
        };

    var lineChart = new google.visualization.LineChart(document.getElementById(chartId));

    // Start the animation by listening to the first 'ready' event.
    google.visualization.events.addOneTimeListener(lineChart, 'ready', update);

    // Control all other animations by listening to the 'animationfinish' event.
    //google.visualization.events.addListener(lineChart, 'animationfinish', update);

    lineChart.draw(data, options);
    var t = setInterval(update, lineChartUpdateIntervall);

    function update() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Time');
        data.addColumn('number', 'Valence');
        data.addColumn('number', 'Arousal');
        var tableData;
        if (!title.localeCompare("Gesture"))
            tableData = gestureTableData;
        else if (!title.localeCompare("Prosody"))
            tableData = prosodyTableData;
        else if (!title.localeCompare("Mimic"))
            tableData = mimicTableData;
        $.each(tableData, function (i, row) {
            data.addRow([
              row.time.getHours().toString() + ':' + row.time.getMinutes().toString() + ':' + row.time.getSeconds().toString() + ':' + (row.time.getMilliseconds() < 100 ? '0' : '') + row.time.getMilliseconds().toString() + '\n' + row.time.getDate().toString() + '.' + (row.time.getMonth() + 1).toString(),
              parseFloat(row.valence),
              parseFloat(row.arousal)
            ]);
        });

        lineChart.draw(data, options);
    }
};

function drawScatterChart() {

    var data = new google.visualization.DataTable();
    data.addColumn('number');
    data.addColumn('number');

   
    // Our central point, which will jiggle.
    data.addRow([0, 0]);

    var options = {
        legend: 'none',
        colors: ['#2E9AFE'],
        pointShape: 'circle',
        pointSize: 19,
        backgroundColor: 'none',
        animation: {
            duration: scatterChartAnimationDuration,
            easing: 'inAndOut',
        },
        vAxis: {
            //title: 'Arousal',
            viewWindowMode: 'explicit',
            textPosition: 'none',
            viewWindow: {
                max: 1,
                min: -1
            },
            gridlines: {
                count: 0    
            }
        },
        hAxis: {
            //title: 'Valence',
            viewWindowMode: 'explicit',
            textPosition: 'none',
            viewWindow: {
                max: 1,
                min: -1
            },
            gridlines: {
                count: 0
            }
        }
    };

    var scatterChart = new google.visualization.ScatterChart(document.getElementById('scatterChart'));

    // Start the animation by listening to the first 'ready' event.
    google.visualization.events.addOneTimeListener(scatterChart, 'ready', update);

    // Control all other animations by listening to the 'animationfinish' event.
    google.visualization.events.addListener(scatterChart, 'animationfinish', update);

    scatterChart.draw(data, options);


    function update() {
        
        var x = fusionTableData[fusionTableData.length - 1].valence;
        var y = fusionTableData[fusionTableData.length - 1].arousal;

        data.setValue(data.getNumberOfRows() - 1, 0, x);
        data.setValue(data.getNumberOfRows() - 1, 1, y);

        scatterChart.draw(data, options);
    }

    function randomWalk() {
        var x = data.getValue(data.getNumberOfRows() - 1, 0);
        var y = data.getValue(data.getNumberOfRows() - 1, 1);
        x += 5 * (Math.random() - 0.5);
        y += 5 * (Math.random() - 0.5);
        if (x * x + y * y > radius * radius) {
            // Out of bounds. Bump toward center.
            x += Math.random() * ((x < 0) ? 5 : -5);
            y += Math.random() * ((y < 0) ? 5 : -5);
        }
        data.setValue(data.getNumberOfRows() - 1, 0, x);
        data.setValue(data.getNumberOfRows() - 1, 1, y);
        scatterChart.draw(data, options);
    }
}



function initCharts() {
    drawLineChart('lineChart', 'Gesture');
    drawLineChart('lineChart2', 'Prosody');
    drawLineChart('lineChart3', 'Mimic');
    drawScatterChart();
}

function init() {
    //init tabledata with zero, values
    var dataObject = { time: new Date(), valence: 0, arousal: 0 };
    for(var i = 0; i < maxElements; i++) 
        fusionTableData[i] = dataObject;

    var timer = setInterval(getVA, dataRefreshIntervall);
    initCharts();
}
