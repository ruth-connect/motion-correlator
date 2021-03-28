function displayNewDetections(detections) {
	
}

function getNewDetections() {
	$.ajax({
		url: "/newDetections/" + camera,
		context: document.body
	}).done(function(detections) {
		if (detections.length > 0) {
			displayNewDetections(detections);
		}
	});
}

$(document).ready(function() {
	$(document).foundation();
	lazyload();
	setInterval(getNewDetections, 500);
});
