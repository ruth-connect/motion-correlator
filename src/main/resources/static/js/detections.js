function displayNewDetections(detections) {
	for (var i = 0; i < detections.length; i++) {
		var detection = detections[i];
		var id = detection.timestamp + "_" + detection.sequence;
		if (!$("#td_" + id).length) {
			var html =
				"<tr id=\"" + id + "\">" +
					"<td>" +
						"<button class=\"button\" type=\"button\" data-toggle=\"div_" + id + "\" style=\"margin-bottom: 0px;\">View</button>" +
						"<div class=\"dropdown-pane\" id=\"div_" + id + "\" data-dropdown data-hover=\"true\" data-hover-pane=\"true\" data-position=\"right\" data-alignment=\"top\" style=\"width: 79%;\">" +
							"<div class=\"large-12 cell\">" +
								"<div class=\"grid-x grid-padding-x\">" +
									"<div class=\"large-4 medium-6 small-12 cell\">" +
										"<h4>Camera</h4>" +
										"<img class=\"lazyload\" data-src=\"" + detection.stampedImagePath + "\" style=\"width: 100%;\"/>" +
									"</div>" +
									"<div class=\"large-4 medium-6 small-12 cell\">" +
										"<h4>Average</h4>" +
										"<img class=\"lazyload\" data-src=\"" + detection.averageImagePath + "\" style=\"width: 100%;\"/>" +
									"</div>" +
									"<div class=\"large-4 medium-6 small-12 cell\">" +
										"<h4>Delta</h4>" +
										"<img class=\"lazyload\" data-src=\"" + detection.deltaImagePath + "\" style=\"width: 100%;\"/>" +
									"</div>" +
								"</div>" +
							"</div>" +
						"</div>" +
					"</td>" +
					"<td>" + detection.date + "</td>" +
					"<td>" + detection.time + "</td>" +
					"<td>" + (detection.vectorMotionDetection ? detection.vectorMotionDetection.frameVector.magnitude : "") + "</td>" +
					"<td>" + (detection.vectorMotionDetection ? detection.vectorMotionDetection.frameVector.count : "") + "</td>" +
					"<td>" + (detection.vectorMotionDetection && detection.vectorMotionDetection.interpolated ? "Y" : "") + "</td>" +
					"<td>" + detection.strongestPersonDetectionWeightString + "</td>" +
					"<td>" + (detection.personDetections ? detection.personDetections.detectionTimeMilliseconds + "ms" : "") + "</td>" +
				"</tr>";
			
			$("#new-detections-tbody").append(html);
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
