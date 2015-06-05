package com.tamr.field.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceHref {
	private final String name;

	@JsonCreator
	public SourceHref(
		@JsonProperty("name") final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
