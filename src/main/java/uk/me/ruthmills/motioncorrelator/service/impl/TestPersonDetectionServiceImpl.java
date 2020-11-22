package uk.me.ruthmills.motioncorrelator.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetectionParameters;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.service.TestPersonDetectionService;

@Service
public class TestPersonDetectionServiceImpl implements TestPersonDetectionService {
	private PersonDetectionParameters personDetectionParameters;
	private PersonDetections personDetections;

	@PostConstruct
	public void initialise() {
		personDetectionParameters = new PersonDetectionParameters(0.2d, 4, 4, 8, 8, 1.05d);
	}

	public void setPersonDetectionParameters(PersonDetectionParameters personDetectionParameters) {
		this.personDetectionParameters = personDetectionParameters;
	}

	public PersonDetectionParameters getPersonDetectionParameters() {
		return personDetectionParameters;
	}

	@Override
	public void setPersonDetections(PersonDetections personDetections) {
		this.personDetections = personDetections;
	}

	@Override
	public PersonDetections getPersonDetections() {
		return personDetections;
	}
}
