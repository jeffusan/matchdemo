package com.tamr.field.model;


import java.util.Optional;

import com.google.common.collect.ListMultimap;

public class RecordUpdate {

	private final String method;
	private final String recordId;
	private final Optional<ListMultimap<String, String>> body;

	public RecordUpdate(
		final String method,
		final String recordId,
		final Optional<ListMultimap<String, String>> body
	) {
		this.method = method;
		this.recordId = recordId;
		this.body = body;
	}

	public String getRecordId() {
		return recordId;
	}

	public String getMethod() {
		return method;
	}

	public Optional<ListMultimap<String, String>> getBody() {
		return body;
	}

	public static RecordUpdate createRecordPut(final String recordId, final ListMultimap<String, String> attributes) {
		return new RecordUpdate("PUT", recordId, Optional.of(attributes));
	}

	public static RecordUpdate createRecordDelete(final String recordId) {
		return new RecordUpdate("DELETE", recordId, Optional.empty());
	}

	public static RecordUpdate createRecordPatch(
		final String recordId,
		final ListMultimap<String, String> attributes
	) {
		return new RecordUpdate("PATCH", recordId, Optional.of(attributes));
	}
}
