package de.wuespace.telestion.examples.header;

import de.wuespace.telestion.api.message.HeaderInformation;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

public class TimeInformation extends HeaderInformation {

	public static final String RECEIVE_TIME = "receive-time";
	public static final String SEND_TIME = "send-time";

	public static final long NO_TIME = -1L;

	public static TimeInformation from(MultiMap headers) {
		return new TimeInformation(headers);
	}

	public static TimeInformation from(Message<?> message) {
		return new TimeInformation(message);
	}

	public static TimeInformation from(DeliveryOptions options) {
		return new TimeInformation(options);
	}

	public TimeInformation() {
		this(System.currentTimeMillis());
	}

	public TimeInformation(long sendTime) {
		this(NO_TIME, sendTime);
	}

	public TimeInformation(long receiveTime, long sendTime) {
		setReceiveTime(receiveTime);
		setSendTime(sendTime);
	}

	public TimeInformation(TimeInformation other) {
		this(other.getReceiveTime(), other.getSendTime());
	}
	public TimeInformation(MultiMap headers) {
		super(headers);
	}

	public TimeInformation(Message<?> message) {
		super(message);
	}

	public TimeInformation(DeliveryOptions options) {
		super(options);
	}

	public TimeInformation setReceiveTime(long receiveTime) {
		add(RECEIVE_TIME, receiveTime);
		return this;
	}

	public TimeInformation setSendTime(long sendTime) {
		add(SEND_TIME, sendTime);
		return this;
	}

	public long getReceiveTime() {
		return getLong(RECEIVE_TIME, NO_TIME);
	}

	public long getSendTime() {
		return getLong(SEND_TIME, NO_TIME);
	}
}
