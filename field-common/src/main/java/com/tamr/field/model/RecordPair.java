package com.tamr.field.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordPair {

	public final Record master;
	public final Record duplicate;
	public final double similarity;

	@JsonCreator
	public RecordPair(
		@JsonProperty("master") final Record master,
		@JsonProperty("duplicate") final Record duplicate,
		@JsonProperty("similarity") final double similarity
	) {
		this.master = master;
		this.duplicate = duplicate;
		this.similarity = similarity;
	}
}
