function formatMilliseconds(milliseconds) {
	return "00".substring(0, 3 - new String(milliseconds).length) + milliseconds;
}

function formatTimestamp(timestamp) {
	return timestamp.substring(0, 20) + formatMilliseconds(timestamp.substring(20, timestamp.substring.length));
}

function getDate(detection) {
	return detection.timestamp.substring(8, 10) + "/" + detection.timestamp.substring(5, 7) + "/" +
		detection.timestamp.substring(0, 4);
}

function getTime(detection) {
	return detection.timestamp.substring(11, 20) +
		formatMilliseconds(detection.timestamp.substring(20, detection.timestamp.length));
}

function getImagePath(detection) {
	return '/images/' + detection.camera + "/" + detection.timestamp.substring(0, 4) + '/' + detection.timestamp.substring(5, 7) + '/' +
		detection.timestamp.substring(8, 10) + '/' + detection.timestamp.substring(11, 13) + '/';
}

function getStampedImagePath(detection) {
	var detections = detection.personDetections && detection.personDetections.personDetections.length > 0 ?
		"-" + detection.personDetections.personDetections.length +
		"-" + detection.personDetections.personDetections[0].weight.toFixed(3) : "";
	return getImagePath(detection) + formatTimestamp(detection.timestamp) + "-" + detection.sequence + "-stamped" + detections + ".jpg";
}

function getAverageImagePath(detection) {
	return getImagePath(detection) + formatTimestamp(detection.timestamp) + "-" + detection.sequence + "-average.jpg";
}

function getDeltaImagePath(detection) {
	return getImagePath(detection) + formatTimestamp(detection.timestamp) + "-" + detection.sequence + "-delta.jpg";
}

function displayNewDetections(detections) {
	for (var i = 0; i < detections.length; i++) {
		var detection = detections[i];
		var id = detection.timestamp + "_" + detection.sequence;
		if (!document.getElementById("tr_" + id)) {
			var html =
				"<tr id=\"tr_" + id + "\">" +
					"<td>" +
						"<button class=\"button\" type=\"button\" data-toggle=\"div_" + id + "\" style=\"margin-bottom: 0px;\">View</button>" +
						"<div class=\"dropdown-pane\" id=\"div_" + id + "\" data-dropdown data-hover=\"true\" data-hover-pane=\"true\" data-position=\"right\" data-alignment=\"top\" style=\"width: 79%;\">" +
							"<div class=\"large-12 cell\">" +
								"<div class=\"grid-x grid-padding-x\">" +
									"<div class=\"large-4 medium-6 small-12 cell\">" +
										"<h4>Camera</h4>" +
										"<img class=\"lazyload\" data-src=\"" + getStampedImagePath(detection) + "\" style=\"width: 100%;\"/>" +
									"</div>" +
									"<div class=\"large-4 medium-6 small-12 cell\">" +
										"<h4>Average</h4>" +
										"<img class=\"lazyload\" data-src=\"" + getAverageImagePath(detection) + "\" style=\"width: 100%;\"/>" +
									"</div>" +
									"<div class=\"large-4 medium-6 small-12 cell\">" +
										"<h4>Delta</h4>" +
										"<img class=\"lazyload\" data-src=\"" + getDeltaImagePath(detection) + "\" style=\"width: 100%;\"/>" +
									"</div>" +
								"</div>" +
							"</div>" +
						"</div>" +
					"</td>" +
					"<td>" + getDate(detection) + "</td>" +
					"<td>" + getTime(detection) + "</td>" +
					"<td>" + (detection.vectorMotionDetection ? detection.vectorMotionDetection.frameVector.magnitude : "") + "</td>" +
					"<td>" + (detection.vectorMotionDetection ? detection.vectorMotionDetection.frameVector.count : "") + "</td>" +
					"<td>" + (detection.vectorMotionDetection && detection.vectorMotionDetection.interpolated ? "Y" : "") + "</td>" +
					"<td>" + detection.strongestPersonDetectionWeightString + "</td>" +
					"<td>" + (detection.personDetections ? detection.personDetections.detectionTimeMilliseconds + "ms" : "") + "</td>" +
				"</tr>";
			
			$("#new-detections-tbody").append(html).foundation().lazyload();
		}
	}
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
