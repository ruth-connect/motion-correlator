package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;

public interface TestPersonDetectionService {

	public void setPersonDetections(PersonDetections personDetections);

	public PersonDetections getPersonDetections();
}
