package com.tamr.field.model;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface ResponseWrapper<T> {
    enum Status { SUCCESS, FAIL }

    public T get();

	// In cases where the response is paged, includes paging and filtering information
	public Optional<Integer> getOffset();
	public Optional<Integer> getLimit();
	public Optional<Integer> getFilteredTotal();
	public Optional<Integer> getUnfilteredTotal();

	static class SuccessResponse<T> implements ResponseWrapper<T> {
		private final T payload;
		private final Optional<Integer> offset;
		private final Optional<Integer> limit;
		private final Optional<Count> filteredTotal;
		private final Optional<Count> unfilteredTotal;

		public SuccessResponse(
			final T payload,
			final Optional<Integer> offset,
			final Optional<Integer> limit,
			final Optional<Count> filteredTotal,
			final Optional<Count> unfilteredTotal
		) {
			this.payload = payload;
			this.offset = offset;
			this.limit = limit;
			this.filteredTotal = filteredTotal;
			this.unfilteredTotal = unfilteredTotal;
		}

		@Override
		public T get() {
			return payload;
		}

		@Override
		public Optional<Integer> getOffset() {
			return offset;
		}

		@Override
		public Optional<Integer> getLimit() {
			return limit;
		}

		@Override
		public Optional<Integer> getFilteredTotal() {
			if (filteredTotal.isPresent()) {
				return Optional.of(filteredTotal.get().getValue());
			}
			return Optional.empty();
		}

		@Override
		public Optional<Integer> getUnfilteredTotal() {
			if (filteredTotal.isPresent()) {
				return Optional.of(filteredTotal.get().getValue());
			}
			return Optional.empty();
		}
	}

	static class FailResponse<T> implements ResponseWrapper<T> {
		private final String message;

		public FailResponse(final String message) {
			this.message = message;
		}

		@Override
		public T get() {
			throw new OperationFailedException(message);
		}

		@Override
		public Optional<Integer> getOffset() {
			throw new OperationFailedException(message);
		}

		@Override
		public Optional<Integer> getLimit() {
			throw new OperationFailedException(message);
		}

		@Override
		public Optional<Integer> getFilteredTotal() {
			throw new OperationFailedException(message);
		}

		@Override
		public Optional<Integer> getUnfilteredTotal() {
			throw new OperationFailedException(message);
		}
	}

	@JsonCreator
	static <T> ResponseWrapper<T> deserialize(
		@JsonProperty("status") final Status status,
		@JsonProperty("message") final Optional<String> message,
		@JsonProperty("payload") final Optional<T> payload,
		@JsonProperty("offset") final Optional<Integer> offset,
		@JsonProperty("limit") final Optional<Integer> limit,
		@JsonProperty("filteredTotal") final Optional<Count> filteredTotal,
		@JsonProperty("unfilteredTotal") final Optional<Count> unfilteredTotal
	) {
		switch (status) {
		case SUCCESS:
			return new SuccessResponse<>(
				payload.orElse(null),
				offset,
				limit,
				filteredTotal,
				unfilteredTotal
			);
		default:
			return new FailResponse<>(message.orElse(""));
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	final static class Count {

		private final Integer value;

		@JsonCreator
		Count(@JsonProperty("value") final Integer value) {
			this.value = value;
		}

		Integer getValue() {
			return this.value;
		}
	}
}
