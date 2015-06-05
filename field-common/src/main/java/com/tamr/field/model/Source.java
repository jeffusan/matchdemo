package com.tamr.field.model;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Source {
	private final int id;
	private final String name;
	// Populated only if source is obtained using getAllSources() after dedup
	private final Optional<Integer> numEntities;

	@JsonCreator
	public Source(
		@JsonProperty("id") final int id,
		@JsonProperty("name") final String name,
		@JsonProperty("numEntities") @Nullable final Integer numEntities
	) {
		this.id = id;
		this.name = name;
		this.numEntities = Optional.ofNullable(numEntities);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Optional<Integer> getNumEntities() {
		return numEntities;
	}

	@Override
	public String toString() {
		return String.format("Source{id=%d, name='%s'}", id, name);
	}
}
