package de.wuespace.telestion.examples.header;

import de.wuespace.telestion.api.message.HeaderInformation;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

public class DelayCounterInformation extends HeaderInformation {

	public static final String DELAY_KEY = "delay";
	public static final String COUNTER_KEY = "counter";

	public static final int DELAY_DEFAULT_VALUE = -1;
	public static final int COUNTER_DEFAULT_VALUE = -1;

	public static DelayCounterInformation from(MultiMap headers) {
		return new DelayCounterInformation(headers);
	}

	public static DelayCounterInformation from(Message<?> message) {
		return new DelayCounterInformation(message);
	}

	public static DelayCounterInformation from(DeliveryOptions options) {
		return new DelayCounterInformation(options);
	}

	public DelayCounterInformation() {
		this(DELAY_DEFAULT_VALUE, COUNTER_DEFAULT_VALUE);
	}

	public DelayCounterInformation(int delay, int counter) {
		setDelay(delay);
		setCounter(counter);
	}

	public DelayCounterInformation(DelayCounterInformation other) {
		this(other.getDelay(), other.getCounter());
	}

	public DelayCounterInformation(MultiMap headers) {
		super(headers);
	}

	public DelayCounterInformation(Message<?> message) {
		super(message);
	}

	public DelayCounterInformation(DeliveryOptions options) {
		super(options);
	}

	public DelayCounterInformation setDelay(int delay) {
		add(DELAY_KEY, delay);
		return this;
	}

	public DelayCounterInformation setCounter(int counter) {
		add(COUNTER_KEY, counter);
		return this;
	}

	public int getDelay() {
		return getInt(DELAY_KEY, DELAY_DEFAULT_VALUE);
	}

	public int getCounter() {
		return getInt(COUNTER_KEY, COUNTER_DEFAULT_VALUE);
	}
}
