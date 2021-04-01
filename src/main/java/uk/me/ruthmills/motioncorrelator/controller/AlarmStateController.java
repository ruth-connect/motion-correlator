package uk.me.ruthmills.motioncorrelator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import uk.me.ruthmills.motioncorrelator.service.AlarmStateService;

@RestController
public class AlarmStateController {

	@Autowired
	private AlarmStateService alarmStateService;

	@PostMapping(value = "/armed_away")
	@ResponseStatus(value = HttpStatus.OK)
	public void armedAway() {
		alarmStateService.armedAway();
	}

	@PostMapping(value = "/armed_night")
	@ResponseStatus(value = HttpStatus.OK)
	public void armedNight() {
		alarmStateService.armedNight();
	}

	@PostMapping(value = "/armed_home")
	@ResponseStatus(value = HttpStatus.OK)
	public void armedHome() {
		alarmStateService.armedHome();
	}

	@PostMapping(value = "/disarmed")
	@ResponseStatus(value = HttpStatus.OK)
	public void disarmed() {
		alarmStateService.disarmed();
	}

	@PostMapping(value = "/countdown")
	@ResponseStatus(value = HttpStatus.OK)
	public void countdown() {
		alarmStateService.countdown();
	}

	@PostMapping(value = "/triggered")
	@ResponseStatus(value = HttpStatus.OK)
	public void triggered() {
		alarmStateService.triggered();
	}
}
