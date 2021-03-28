function getNewDetections() {
	$.ajax({
		url: "/newDetections/" + camera,
		context: document.body
	}).done(function(data) {
		alert(JSON.stringify(data));
	});
}

$(document).ready(function() {
	$(document).foundation();
	lazyload();
	getNewDetections();
});
