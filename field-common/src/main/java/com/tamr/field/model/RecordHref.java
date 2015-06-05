package com.tamr.field.model;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordHref {
	private final SourceHref source;
	private final String name;

	@JsonCreator
	public RecordHref(
		@JsonProperty("source") final SourceHref source,
		@JsonProperty("name") final String name) {
		this.source = source;
		this.name = name;
	}

	public SourceHref getSource() {
		return source;
	}

	public String getName() {
		return name;
	}
}
