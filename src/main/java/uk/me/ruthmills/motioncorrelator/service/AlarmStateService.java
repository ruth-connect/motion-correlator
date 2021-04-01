package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.AlarmState;

public interface AlarmStateService {

	public void armedAway();

	public void armedNight();

	public void armedHome();

	public void disarmed();

	public void countdown();

	public void triggered();

	public AlarmState getAlarmState();
}
