package uk.me.ruthmills.motioncorrelator.service.impl;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.service.TestPersonDetectionService;

@Service
public class TestPersonDetectionServiceImpl implements TestPersonDetectionService {
	private PersonDetections personDetections;

	@Override
	public void setPersonDetections(PersonDetections personDetections) {
		this.personDetections = personDetections;
	}

	@Override
	public PersonDetections getPersonDetections() {
		return personDetections;
	}
}
