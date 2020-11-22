package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetectionParameters;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;

public interface PersonDetectionService {

	public PersonDetections detectPersons(Image image);

	public PersonDetections detectPersons(Image image, PersonDetectionParameters personDetectionParameters);
}
