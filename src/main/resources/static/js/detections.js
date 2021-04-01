String.prototype.toTitleCase = function () {
    return this.replace(/\w\S*/g, function(txt) { return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase(); });
};

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

function getVectorTime(detection) {
	return detection.vectorMotionDetection && detection.vectorMotionDetection.timestamp ?
		detection.vectorMotionDetection.timestamp.substring(11, 19) + "." +
			formatMilliseconds(detection.vectorMotionDetection.timestamp.substring(20, detection.vectorMotionDetection.timestamp.length)) : "";
}

function getTime(detection) {
	return detection.timestamp.substring(11, 19) + "." +
		formatMilliseconds(detection.timestamp.substring(20, detection.timestamp.length));
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

function getAlarmState(detection) {
	return detection.alarmState && detection.alarmState !== "UNKNOWN" ? detection.alarmState.replaceAll("_", " ").toTitleCase() : "";
}

function getVectorData(detection) {
	if (!detection.vectorMotionDetection && detection.roundRobin) {
		return "<td colspan=\"5\"><i>Round Robin</i></td>";
	} else if (detection.vectorMotionDetection && detection.vectorMotionDetection.externalTrigger) {
		return "<td colspan=\"5\"><b>" + detection.vectorMotionDetection.externalTrigger.code.replaceAll("_", " ") + "</b></td>";
	} else {
		return	"<td>" + getRegions(detection) + "</td>" + 
				"<td>" + getMagnitude(detection) + "</td>" +
				"<td>" + getCount(detection) + "</td>" +
				"<td>" + getBurst(detection) + "</td>" +
				"<td>" + getInterpolated(detection) + "</td>";
	}
}

function getNumPersonDetections(detection) {
	return detection.personDetections && detection.personDetections.personDetections && detection.personDetections.personDetections.length > 0 ?
		detection.personDetections.personDetections.length : "";
}

function getWeight(detection) {
	return detection.personDetections && detection.personDetections.personDetections && detection.personDetections.personDetections.length > 0 ?
		detection.personDetections.personDetections[0].weight.toFixed(3).padEnd(5, "0") : "";
}

function getDetectionTimeMilliseconds(detection) {
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
				"<td>" + (!vector.region || vector.region === "f" ? "Frame" : vector.region) + "</td>" +
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

function displayAlarmState(detection) {
	return	"<h5 style=\"margin-top: 20px; float: right;\">Burglar Alarm: <b>" + getAlarmState(detection) + "</b></h5>";
}

function displayVectors(detection) {
	if (detection.vectorMotionDetection && detection.vectorMotionDetection.frameVector) {
		return	"<h5 style=\"margin-top: 20px;\">Vectors</h5>" +
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
				"</table>";
	}
	return "";
}

function displayDetectionRow(detection, prefix, id, processTime, replaced) {
	return	"<tr id=\"" + prefix + "-tr-" + id + (processTime ? "\" data-process-time=\"" + processTime + "\"" : "") + (replaced ? "data-replaced" : "") + ">" +
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
						"<div class=\"large-12 cell\">" +
							"<div class=\"grid-x grid-padding-x\">" +
								"<div class=\"large-12 cell\">" +
									displayAlarmState(detection) +
									displayVectors(detection) +
								"</div>" +
							"</div>" +
						"</div>" +
					"</div>" +
				"</td>" +
				"<td>" + getDate(detection) + "</td>" +
				"<td>" + getTime(detection) + "</td>" +
				"<td>" + getVectorTime(detection) + "</td>" +
				getVectorData(detection) +
				"<td>" + getNumPersonDetections(detection) + "</td>" +
				"<td>" + getWeight(detection) + "</td>" +
				"<td>" + getDetectionTimeMilliseconds(detection) + "</td>" +
			"</tr>";
}

function displayLiveDetections(detections, prefix) {
	for (var i = 0; i < detections.length; i++) {
		var detection = detections[i];
		var id = detection.timestamp + "-" + detection.sequence;
		var processTime = formatTimestamp(detection.processTime);
		var element = document.getElementById(prefix + "-tr-" + id)
		if (!element) {
			var html = displayDetectionRow(detection, prefix, id, processTime, false);
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
		} else {
			var oldProcessTime = $(element).attr("data-process-time");
			if (processTime > oldProcessTime) {
				var html = displayDetectionRow(detection, prefix, id, processTime, true);
				var newNode = $(element).replaceWith(html);
				newNode.foundation();
				newNode.find("img.lazyload").lazyload();
			}
		}
	}
}

function displayDetections(detections, prefix) {
	for (var i = 0; i < detections.length; i++) {
		var detection = detections[i];
		var id = detection.timestamp + "-" + detection.sequence;
		var html = displayDetectionRow(detection, prefix, id);
		var newNode = $("#" + prefix + "-detections-tbody").append(html);
		newNode.foundation();
		newNode.find("img.lazyload").lazyload();
	}
}

function getLiveDetections() {
	$.ajax({
		url: "/liveDetections/" + camera,
		context: document.body
	}).done(function(detections) {
		if (detections.length > 0) {
			displayLiveDetections(detections, 'live');
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
			$("#load-more").removeAttr("disabled");
			$("#load-more").removeClass("disabled");
		}
	});
}

function getDetectionsForTimestamp(timestamp) {
	$.ajax({
		url: "/detections/" + camera + "/" + timestamp,
		context: document.body
	}).done(function(detections) {
		if (detections.length > 0) {
			displayDetections(detections, 'previous');
			$("#load-more").removeAttr("disabled");
			$("#load-more").removeClass("disabled");
		}
	});
}

function loadMore(event) {
	event.preventDefault();
	$("#load-more").attr("disabled", "disabled");
	$("#load-more").addClass("disabled");
	var id = $("#previous-detections-tbody").children("tr").last().attr("id");
	var timestamp = formatTimestamp(id.substring(12, id.lastIndexOf("-")));
	getDetectionsForTimestamp(timestamp);
}

function clearAll(event) {
	event.preventDefault();
	$("#live-detections-tbody").empty();
}

$(document).ready(function() {
	$(document).foundation();
	lazyload();
	getDetections();
	$("#load-more").click(loadMore);
	$("#clear-all").click(clearAll)
	setInterval(getLiveDetections, 500);
});
