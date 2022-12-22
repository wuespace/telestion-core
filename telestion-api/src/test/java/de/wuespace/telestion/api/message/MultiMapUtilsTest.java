package de.wuespace.telestion.api.message;

import de.wuespace.telestion.api.MockMessage;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MultiMapUtilsTest {

	@Test
	void shouldMergeAnArrayOfMultiMaps() {
		var array = new MultiMap[]{ MULTI_MAP_1, MULTI_MAP_2, MULTI_MAP_3 };

		var merged = MultiMapUtils.merge(array);

		assertThat(merged.get(PARAM_1_KEY), is(PARAM_1_VALUE));
		assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
		assertThat(merged.get(PARAM_3_KEY), is(PARAM_3_VALUE));
	}

	@Test
	void shouldMergeAListOfMultiMaps() {
		var list = List.of(MULTI_MAP_1, MULTI_MAP_2, MULTI_MAP_3);

		var merged = MultiMapUtils.merge(list);

		assertThat(merged.get(PARAM_1_KEY), is(PARAM_1_VALUE));
		assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
		assertThat(merged.get(PARAM_3_KEY), is(PARAM_3_VALUE));
	}

	@Test
	void shouldMergeAStreamOfMultiMaps() {
		var stream = Stream.of(MULTI_MAP_1, MULTI_MAP_2, MULTI_MAP_3);

		var merged = MultiMapUtils.merge(stream);

		assertThat(merged.get(PARAM_1_KEY), is(PARAM_1_VALUE));
		assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
		assertThat(merged.get(PARAM_3_KEY), is(PARAM_3_VALUE));
	}

	@Test
	void shouldExtractTheMultiMapFromAMessage() {
		var message1 = new MockMessage<>("some-address", MULTI_MAP_1, new Object());
		var message2 = new MockMessage<>("some-address", MULTI_MAP_2, new Object());
		var message3 = new MockMessage<>("some-address", MULTI_MAP_3, new Object());

		assertThat(MultiMapUtils.from(message1), is(MULTI_MAP_1));
		assertThat(MultiMapUtils.from(message2), is(MULTI_MAP_2));
		assertThat(MultiMapUtils.from(message3), is(MULTI_MAP_3));
	}

	@Test
	void shouldExtractTheMultiMapFromDeliveryOptions() {
		var options1 = new DeliveryOptions().setHeaders(MULTI_MAP_1);
		var options2 = new DeliveryOptions().setHeaders(MULTI_MAP_2);
		var options3 = new DeliveryOptions().setHeaders(MULTI_MAP_3);

		assertThat(MultiMapUtils.from(options1), is(MULTI_MAP_1));
		assertThat(MultiMapUtils.from(options2), is(MULTI_MAP_2));
		assertThat(MultiMapUtils.from(options3), is(MULTI_MAP_3));
	}

	public final String PARAM_1_KEY = "key1";
	public final String PARAM_2_KEY = "key2";
	public final String PARAM_3_KEY = "key3";

	public final String PARAM_1_VALUE = "value1";
	public final String PARAM_2_VALUE = "value1";
	public final String PARAM_3_VALUE = "value1";

	public final MultiMap MULTI_MAP_1 = MultiMap.caseInsensitiveMultiMap().add(PARAM_1_KEY, PARAM_1_VALUE);
	public final MultiMap MULTI_MAP_2 = MultiMap.caseInsensitiveMultiMap().add(PARAM_2_KEY, PARAM_2_VALUE);
	public final MultiMap MULTI_MAP_3 = MultiMap.caseInsensitiveMultiMap().add(PARAM_3_KEY, PARAM_3_VALUE);
}
