var dataRefreshIntervall = 500;
var lineChartUpdateIntervall = 500;
var scatterChartAnimationDuration = 500;
var maxElements = 20;

function drawScatterChart() {

    var data = new google.visualization.DataTable();
    data.addColumn('number');
    data.addColumn('number');

   
    // Our central point, which will jiggle.
    data.addRow([0, 0]);

    var options = {
        legend: 'none',
        colors: ['#A3B2C0'],
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
    google.visualization.events.addOneTimeListener(scatterChart, 'ready', updateEmotionPlot);

    // Control all other animations by listening to the 'animationfinish' event.
    google.visualization.events.addListener(scatterChart, 'animationfinish', updateEmotionPlot);

    scatterChart.draw(data, options);

    function updateEmotionPlot() {
		
		$.get("http://137.250.171.232:11153/emotion", function (data) {
			var v = data.valence;
			var a = data.arousal;
			
			var data = new google.visualization.DataTable();
			data.addColumn('number');
			data.addColumn('number');

   
			// Our central point, which will jiggle.
			data.addRow([v, a]);
			
			scatterChart.draw(data, options);
		})
    }


    function randomWalk() {
        var x = data.getValue(data.getNumberOfRows() - 1, 0);
        var y = data.getValue(data.getNumberOfRows() - 1, 1);
        x += 5 * (Math.random() - 0.5);
        y += 5 * (Math.random() - 0.5);
        if (x * x + y * y > 1 * 1) {
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
    drawScatterChart();
}

function init() {
   

    //var timer = setInterval(getVA, dataRefreshIntervall);
    initCharts();
}
