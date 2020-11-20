package uk.me.ruthmills.motioncorrelator.model.vector;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ExternalTrigger extends VectorData {

	private String code;

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String toString() {
		return new ToStringBuilder(this).append("code", code).toString();
	}
}
