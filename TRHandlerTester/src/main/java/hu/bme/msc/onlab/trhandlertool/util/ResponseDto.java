package hu.bme.msc.onlab.trhandlertool.util;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

public final class ResponseDto<T> {
	private final T value;

	private final Throwable exception;

	private final String reason;

	private final boolean success;

	private ResponseDto(T value, Throwable exception, String reason, boolean success) {
		this.value = value;
		this.exception = exception;
		this.reason = reason;
		this.success = success;
	}

	public static final <T> ResponseDto<T> ok() {
		return new ResponseDto<>(null, null, StringUtils.EMPTY, true);
	}

	public static final <T> ResponseDto<T> ok(@NotNull T value) {
		return new ResponseDto<>(value, null, StringUtils.EMPTY, true);
	}

	public static final <T> ResponseDto<T> fail(@NotNull String reason) {
		return new ResponseDto<>(null, null, reason, false);
	}

	public static final <T> ResponseDto<T> fail(@NotNull String reason, @NotNull Throwable exception) {
		return new ResponseDto<>(null, exception, reason, false);
	}

	public static final <T> ResponseDto<T> fail(@NotNull ResponseDto<?> original) {
		return new ResponseDto<>(null, original.getException(), original.getReason(), false);
	}

	public static final ResponseDto<Void> convertBooleanTypeToVoid(ResponseDto<Boolean> original) {
		if (original.isSuccess() && Boolean.TRUE.equals(original.getValue())) {
			return ResponseDto.ok();
		} else if (!original.isSuccess() || (original.isSuccess() && Boolean.FALSE.equals(original.getValue()))) {
			return ResponseDto.fail(original);
		} else {
			return ResponseDto.fail("This path should never run!");
		}
	}

	public T getValue() {
		return value;
	}

	public Throwable getException() {
		return exception;
	}

	public String getReason() {
		return reason;
	}

	public boolean isSuccess() {
		return success;
	}
}
