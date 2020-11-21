package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.vector.VectorDataList;

public interface VectorDataService {

	public VectorDataList parseVectorData(String vectorData);
}
