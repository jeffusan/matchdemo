package com.tamr.field.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResults {

	public final List<RecordPair> matches;
	public final List<RecordPair> uncertain;
	public final List<Record> distinct;
	public final List<EntityDetails> masterRecords;


	@JsonCreator
	public MatchResults(
		@JsonProperty("matches") final List<RecordPair> matches,
		@JsonProperty("uncertain") final List<RecordPair> uncertain,
		@JsonProperty("distinct") final List<Record> distinct,
		@JsonProperty("masterRecords") final List<EntityDetails> masterRecords

	) {
		this.matches = matches;
		this.uncertain = uncertain;
		this.distinct = distinct;
		this.masterRecords = masterRecords;
	}

	public int size() {
		return matches.size() + uncertain.size() + distinct.size();
	}
}
