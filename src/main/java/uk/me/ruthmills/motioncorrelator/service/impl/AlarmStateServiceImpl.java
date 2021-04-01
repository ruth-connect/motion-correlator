package uk.me.ruthmills.motioncorrelator.service.impl;

import static uk.me.ruthmills.motioncorrelator.model.AlarmState.ARMED_AWAY;
import static uk.me.ruthmills.motioncorrelator.model.AlarmState.ARMED_HOME;
import static uk.me.ruthmills.motioncorrelator.model.AlarmState.ARMED_NIGHT;
import static uk.me.ruthmills.motioncorrelator.model.AlarmState.COUNTDOWN;
import static uk.me.ruthmills.motioncorrelator.model.AlarmState.DISARMED;
import static uk.me.ruthmills.motioncorrelator.model.AlarmState.TRIGGERED;
import static uk.me.ruthmills.motioncorrelator.model.AlarmState.UNKNOWN;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.AlarmState;
import uk.me.ruthmills.motioncorrelator.service.AlarmStateService;

@Service
public class AlarmStateServiceImpl implements AlarmStateService {

	private volatile AlarmState alarmState;

	private final Logger logger = LoggerFactory.getLogger(AlarmStateServiceImpl.class);

	@PostConstruct
	public void initialise() {
		alarmState = UNKNOWN;
		logger.info("Alarm State set to unknown");
	}

	@Override
	public void armedAway() {
		alarmState = ARMED_AWAY;
		logger.info("Alarm State set to armed_away");
	}

	@Override
	public void armedNight() {
		alarmState = ARMED_NIGHT;
		logger.info("Alarm State set to armed_night");
	}

	@Override
	public void armedHome() {
		alarmState = ARMED_HOME;
		logger.info("Alarm State set to armed_home");
	}

	@Override
	public void disarmed() {
		alarmState = DISARMED;
		logger.info("Alarm State set to disarmed");
	}

	@Override
	public void countdown() {
		alarmState = COUNTDOWN;
		logger.info("Alarm State set to countdown");
	}

	@Override
	public void triggered() {
		alarmState = TRIGGERED;
		logger.info("Alarm State set to triggered");
	}

	public AlarmState getAlarmState() {
		return alarmState;
	}
}
