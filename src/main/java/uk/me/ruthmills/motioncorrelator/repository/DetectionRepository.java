package uk.me.ruthmills.motioncorrelator.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import uk.me.ruthmills.motioncorrelator.model.Detection;

public interface DetectionRepository extends MongoRepository<Detection, String> {

}
