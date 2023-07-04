package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.AlarmState;

/**
 * Stores the current state of the Home Assistant burglar alarm.
 * 
 * @author ruth
 */
public interface AlarmStateService {

	/**
	 * Set the alarm state to Armed Away.
	 */
	public void armedAway();

	/**
	 * Set the alarm state to Armed Night.
	 */
	public void armedNight();

	/**
	 * Set the alarm state to Armed Home.
	 */
	public void armedHome();

	/**
	 * Set the alarm state to Disarmed.
	 */
	public void disarmed();

	/**
	 * Set the alarm state to Countdown.
	 */
	public void countdown();

	/**
	 * Set the alarm state to Triggered.
	 */
	public void triggered();

	/**
	 * Report an invalid alarm code being entered.
	 */
	public void invalidCode();

	/**
	 * Get the current alarm state.
	 * 
	 * @return The alarm state.
	 */
	public AlarmState getAlarmState();
}
