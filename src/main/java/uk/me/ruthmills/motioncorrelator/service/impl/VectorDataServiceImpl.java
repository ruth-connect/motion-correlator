package uk.me.ruthmills.motioncorrelator.service.impl;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.VectorDataList;
import uk.me.ruthmills.motioncorrelator.service.VectorDataService;

@Service
public class VectorDataServiceImpl implements VectorDataService {

	@Override
	public void handleVectorData(String camera, String vectorData) {
		VectorDataList vectorDataList = parseVectorData(vectorData);
	}

	private VectorDataList parseVectorData(String vectorDataString) {
		return new VectorDataList();
	}
}
