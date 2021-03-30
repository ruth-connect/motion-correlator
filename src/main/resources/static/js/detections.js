function formatMilliseconds(milliseconds) {
	return milliseconds.padEnd(3, "0");
}

function formatTimestamp(timestamp) {
	return timestamp.substring(0, 19) + "." + formatMilliseconds(timestamp.substring(20, timestamp.length));
}

function getDate(detection) {
	return detection.timestamp.substring(8, 10) + "/" + detection.timestamp.substring(5, 7) + "/" +
		detection.timestamp.substring(0, 4);
}

function getTime(detection) {
	return detection.timestamp.substring(11, 19) + "." +
		formatMilliseconds(detection.timestamp.substring(20, detection.timestamp.length));
}

function getProcessTime(detection) {
	return detection.processTime ? (detection.processTime.substring(11, 19) + "." +
		formatMilliseconds(detection.processTime.substring(20, detection.processTime.length))) : "";
}

function getRegions(detection) {
	return detection.vectorMotionDetection && detection.vectorMotionDetection.regionVectors ?
		detection.vectorMotionDetection.regionVectors.map(function(vector) {
			return vector.region;
		}).join(", ") : "";
}

function getMagnitude(detection) {
	return detection.vectorMotionDetection ? detection.vectorMotionDetection.frameVector.magnitude : "";
}

function getCount(detection) {
	return detection.vectorMotionDetection ? detection.vectorMotionDetection.frameVector.count : "";
}

function getBurst(detection) {
	return detection.vectorMotionDetection && detection.vectorMotionDetection.burst ? detection.vectorMotionDetection.burst.burstCount : "";
}

function getInterpolated(detection) {
	return detection.vectorMotionDetection && detection.vectorMotionDetection.interpolated ? "Y" : "";
}

function getWeight(detection) {
	return detection.personDetections && detection.personDetections.personDetections && detection.personDetections.personDetections.length > 0 ?
		detection.personDetections.personDetections[0].weight.toFixed(3).padEnd(5, "0") : "";
}

function getSpeed(detection) {
	return detection.personDetections ? detection.personDetections.detectionTimeMilliseconds + "ms" : "";
}

function getImagePath(detection, prefix, suffix) {
	return prefix + detection.camera + "/" + detection.timestamp.substring(0, 4) + '/' +
		detection.timestamp.substring(5, 7) + '/' + detection.timestamp.substring(8, 10) + '/' +
		detection.timestamp.substring(11, 13) + '/' + formatTimestamp(detection.timestamp) + '-' +
		detection.sequence + suffix + ".jpg";
}

function displayImage(title, imagePath) {
	return	"<div class=\"large-4 medium-6 small-12 cell\">" +
				"<h4>" + title + "</h4>" +
				"<img class=\"lazyload\" data-src=\"" + imagePath + "\" style=\"width: 100%;\"/>" +
			"</div>";
}

function displayVector(vector) {
	return	"<tr>" +
				"<td>" + (vector.region === "f" ? "Frame" : vector.region) + "</td>" +
				"<td>" + vector.x + "</td>" + 
				"<td>" + vector.y + "</td>" +
				"<td>" + vector.dx + "</td>" +
				"<td>" + vector.dy + "</td>" +
				"<td>" + vector.magnitude + "</td>" +
				"<td>" + vector.count + "</td>" +
			"</tr>";
}

function displayRegionVectors(regionVectors) {
	var html = "";
	if (regionVectors) {
		for (i = 0; i < regionVectors.length; i++ ) {
			html += displayVector(regionVectors[i]);
		}
	}
	return html;
}

function displayVectors(detection) {
	if (detection.vectorMotionDetection && detection.vectorMotionDetection.frameVector) {
		return	"<div class=\"large-12 cell\">" +
					"<div class=\"grid-x grid-padding-x\">" +
						"<div class=\"large-12 cell\">" +
							"<h5 style=\"margin-top: 20px;\">Vectors</h5>" +
							"<table>" +
								"<thead>" +
									"<th>Region</th>" +
									"<th>x</th>" +
									"<th>y</th>" +
									"<th>dx</th>" +
									"<th>dy</th>" +
									"<th>Mag</th>" +
									"<th>Count</th>" +
								"</thead>" +
								"<tbody>" +
									displayVector(detection.vectorMotionDetection.frameVector) +
									displayRegionVectors(detection.vectorMotionDetection.regionVectors) +
								"</tbody>" +
							"</table>" +
						"</div>" +
					"</div>" +
				"</div>";
	}
	return "";
}

function displayDetectionRow(detection, prefix, id) {
	return	"<tr id=\"" + prefix + "-tr-" + id + "\">" +
				"<td>" +
					"<button class=\"button\" type=\"button\" data-toggle=\"" + prefix + "-div-" + id + "\" style=\"margin-bottom: 0px;\">View</button>" +
					"<div class=\"dropdown-pane\" id=\"" + prefix + "-div-" + id + "\" data-dropdown data-hover=\"true\" data-hover-pane=\"true\" data-position=\"right\" data-alignment=\"top\" style=\"width: 79%;\">" +
						"<div class=\"large-12 cell\">" +
							"<div class=\"grid-x grid-padding-x\">" +
								displayImage("Camera", getImagePath(detection, "/stamped/", "")) +
								displayImage("Average", getImagePath(detection, "/stamped/", "-average")) +
								displayImage("Delta", getImagePath(detection, "/stamped/", "-delta")) +
							"</div>" +
						"</div>" +
						displayVectors(detection) +
					"</div>" +
				"</td>" +
				"<td>" + getDate(detection) + "</td>" +
				"<td>" + getTime(detection) + "</td>" +
				"<td>" + getProcessTime(detection) + "</td>" +
				"<td>" + getRegions(detection) + "</td>" + 
				"<td>" + getMagnitude(detection) + "</td>" +
				"<td>" + getCount(detection) + "</td>" +
				"<td>" + getBurst(detection) + "</td>" +
				"<td>" + getInterpolated(detection) + "</td>" +
				"<td>" + getWeight(detection) + "</td>" +
				"<td>" + getSpeed(detection) + "</td>" +
			"</tr>";
}

function displayDetections(detections, prefix) {
	for (var i = 0; i < detections.length; i++) {
		var detection = detections[i];
		var id = detection.timestamp + "-" + detection.sequence;
		if (!document.getElementById(prefix + "-tr-" + id)) {
			var html = displayDetectionRow(detection, prefix, id);
			var rows = document.getElementById(prefix + "-detections-tbody").children;
			var newNode = undefined;
			if (rows.length == 0) {
				newNode = $("#" + prefix + "-detections-tbody").append(html);
			} else {
				var inserted = false;
				for (var j = 0; j < rows.length && !inserted; j++) {
					var row = rows[j];
					if (row.id < prefix + "-tr-" + id) {
						newNode = $(html).insertBefore($(row));
						inserted = true;
					}
				}
				if (!inserted) {
					newNode = $("#" + prefix + "-detections-tbody").append(html);
				}
			}
			newNode.foundation();
			newNode.find("img.lazyload").lazyload();
		}
	}
}

function getNewDetections() {
	$.ajax({
		url: "/newDetections/" + camera,
		context: document.body
	}).done(function(detections) {
		if (detections.length > 0) {
			displayDetections(detections, 'latest');
		}
	});
}

function getDetections() {
	$.ajax({
		url: "/detections/" + camera,
		context: document.body
	}).done(function(detections) {
		if (detections.length > 0) {
			displayDetections(detections, 'previous');
		}
	});
}

$(document).ready(function() {
	$(document).foundation();
	lazyload();
	getDetections();
	setInterval(getNewDetections, 500);
});
