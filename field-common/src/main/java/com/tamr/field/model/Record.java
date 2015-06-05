package com.tamr.field.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {

	private final String name;
	private final Source source;

	@JsonCreator
	public Record(
		@JsonProperty("name") final String name,
		@JsonProperty("source") final Source source) {
		this.name = name;
		this.source = source;
	}

	public Source getSource() {
		return source;
	}

	public String getName() {
		return name;
	}
}
