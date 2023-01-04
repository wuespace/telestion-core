package de.wuespace.telestion.api.message;

import de.wuespace.telestion.api.MockMessage;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HeaderInformationTest {

	@Nested
	public class MultiMapContractTest {
		@Test
		void shouldImplementMultiMap() {
			assertThat(MultiMap.class.isAssignableFrom(HeaderInformation.class), is(true));
		}
	}

	@Nested
	public class UtilityTest {

		@Test
		void shouldMergeAnArrayOfHeaderInformation() {
			var array = new HeaderInformation[]{HEADER_INFORMATION_1, HEADER_INFORMATION_2, HEADER_INFORMATION_3};

			var merged = HeaderInformation.merge(array).getHeaders();

			assertThat(merged.get(PARAM_1_KEY), is(PARAM_1_VALUE));
			assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
			assertThat(merged.get(PARAM_3_KEY), is(PARAM_3_VALUE));
		}

		@Test
		void shouldMergeAListOfHeaderInformation() {
			var list = List.of(HEADER_INFORMATION_1, HEADER_INFORMATION_2, HEADER_INFORMATION_3);

			var merged = HeaderInformation.merge(list).getHeaders();

			assertThat(merged.get(PARAM_1_KEY), is(PARAM_1_VALUE));
			assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
			assertThat(merged.get(PARAM_3_KEY), is(PARAM_3_VALUE));
		}

		@Test
		void shouldMergeAStreamOfHeaderInformation() {
			var stream = Stream.of(HEADER_INFORMATION_1, HEADER_INFORMATION_2, HEADER_INFORMATION_3);

			var merged = HeaderInformation.merge(stream).getHeaders();

			assertThat(merged.get(PARAM_1_KEY), is(PARAM_1_VALUE));
			assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
			assertThat(merged.get(PARAM_3_KEY), is(PARAM_3_VALUE));
		}

		@Test
		void shouldExtractHeaderInformationFromAMultiMap() {
			assertThat(HeaderInformation.from(MULTI_MAP_1).getHeaders(), is(MULTI_MAP_1));
			assertThat(HeaderInformation.from(MULTI_MAP_2).getHeaders(), is(MULTI_MAP_2));
			assertThat(HeaderInformation.from(MULTI_MAP_3).getHeaders(), is(MULTI_MAP_3));
		}

		@Test
		void shouldExtractHeaderInformationFromAMessage() {
			var message1 = new MockMessage<>("some-address", MULTI_MAP_1, new Object());
			var message2 = new MockMessage<>("some-address", MULTI_MAP_2, new Object());
			var message3 = new MockMessage<>("some-address", MULTI_MAP_3, new Object());

			assertThat(HeaderInformation.from(message1).getHeaders(), is(MULTI_MAP_1));
			assertThat(HeaderInformation.from(message2).getHeaders(), is(MULTI_MAP_2));
			assertThat(HeaderInformation.from(message3).getHeaders(), is(MULTI_MAP_3));
		}

		@Test
		void shouldExtractHeaderInformationFromDeliveryOptions() {
			var options1 = new DeliveryOptions().setHeaders(MULTI_MAP_1);
			var options2 = new DeliveryOptions().setHeaders(MULTI_MAP_2);
			var options3 = new DeliveryOptions().setHeaders(MULTI_MAP_3);

			assertThat(HeaderInformation.from(options1).getHeaders(), is(MULTI_MAP_1));
			assertThat(HeaderInformation.from(options2).getHeaders(), is(MULTI_MAP_2));
			assertThat(HeaderInformation.from(options3).getHeaders(), is(MULTI_MAP_3));
		}
	}

	@Nested
	public class ConstructorTest {

		@Test
		void shouldCreateEmptyHeaderInformation() {
			var headerInformation = new HeaderInformation();
			assertThat(headerInformation.getHeaders().isEmpty(), is(true));
		}

		@Test
		void shouldCreateHeaderInformationFromAMultiMap() {
			assertThat(new HeaderInformation(MULTI_MAP_1).getHeaders(), is(MULTI_MAP_1));
			assertThat(new HeaderInformation(MULTI_MAP_2).getHeaders(), is(MULTI_MAP_2));
			assertThat(new HeaderInformation(MULTI_MAP_3).getHeaders(), is(MULTI_MAP_3));
		}

		@Test
		void shouldCreateHeaderInformationFromAMessage() {
			var message1 = new MockMessage<>("some-address", MULTI_MAP_1, new Object());
			var message2 = new MockMessage<>("some-address", MULTI_MAP_2, new Object());
			var message3 = new MockMessage<>("some-address", MULTI_MAP_3, new Object());

			assertThat(new HeaderInformation(message1).getHeaders(), is(MULTI_MAP_1));
			assertThat(new HeaderInformation(message2).getHeaders(), is(MULTI_MAP_2));
			assertThat(new HeaderInformation(message3).getHeaders(), is(MULTI_MAP_3));
		}

		@Test
		void shouldCreateHeaderInformationFromDeliveryOptions() {
			var options1 = new DeliveryOptions().setHeaders(MULTI_MAP_1);
			var options2 = new DeliveryOptions().setHeaders(MULTI_MAP_2);
			var options3 = new DeliveryOptions().setHeaders(MULTI_MAP_3);

			assertThat(new HeaderInformation(options1).getHeaders(), is(MULTI_MAP_1));
			assertThat(new HeaderInformation(options2).getHeaders(), is(MULTI_MAP_2));
			assertThat(new HeaderInformation(options3).getHeaders(), is(MULTI_MAP_3));
		}
	}

	@Nested
	public class ExportTest {

		@Test
		void shouldReturnCapturedMultiMap() {
			var multiMap = MultiMap.caseInsensitiveMultiMap();
			var headerInformation = new HeaderInformation(multiMap);
			assertThat(headerInformation.getHeaders(), is(multiMap));
		}

		@Test
		void shouldAttachCapturedMultiMapToDeliveryOptions() {
			var multiMap = MultiMap.caseInsensitiveMultiMap();
			var options = new DeliveryOptions();
			var headerInformation = new HeaderInformation(multiMap);

			assertThat(headerInformation.attach(options), is(options));
			assertThat(options.getHeaders(), is(multiMap));
		}

		@Test
		void shouldAttachCapturedMultiMapToNewDeliveryOptions() {
			var multiMap = MultiMap.caseInsensitiveMultiMap();
			var headerInformation = new HeaderInformation(multiMap);

			assertThat(headerInformation.toOptions().getHeaders(), is(multiMap));
		}
	}

	@Nested
	public class GetterTest {

		@Test
		void shouldReturnAStringAsOptional() {
			var filled = new HeaderInformation(STRING_VALUE.filledMap())
					.getString(STRING_VALUE.key());
			var empty = new HeaderInformation(STRING_VALUE.emptyMap())
					.getString(STRING_VALUE.key());

			assertThat(filled.isPresent(), is(true));
			assertThat(filled.get(), is(STRING_VALUE.value()));
			assertThat(empty.isEmpty(), is(true));
		}

		@Test
		void shouldReturnADefaultStringIfNotDefined() {
			var filled = new HeaderInformation(STRING_VALUE.filledMap())
					.getString(STRING_VALUE.key(), STRING_VALUE.defaultValue());
			var empty = new HeaderInformation(STRING_VALUE.emptyMap())
					.getString(STRING_VALUE.key(), STRING_VALUE.defaultValue());

			assertThat(filled, is(STRING_VALUE.value()));
			assertThat(filled, not(STRING_VALUE.defaultValue()));
			assertThat(empty, is(STRING_VALUE.defaultValue()));
			assertThat(empty, not(STRING_VALUE.value()));
		}

		@Test
		void shouldReturnAByteAsOptional() {
			var filled = new HeaderInformation(BYTE_VALUE.filledMap())
					.getByte(BYTE_VALUE.key());
			var empty = new HeaderInformation(BYTE_VALUE.emptyMap())
					.getByte(BYTE_VALUE.key());

			assertThat(filled.isPresent(), is(true));
			assertThat(filled.get(), is(BYTE_VALUE.value()));
			assertThat(empty.isEmpty(), is(true));
		}

		@Test
		void shouldReturnADefaultByteIfNotDefined() {
			var filled = new HeaderInformation(BYTE_VALUE.filledMap())
					.getByte(BYTE_VALUE.key(), BYTE_VALUE.defaultValue());
			var empty = new HeaderInformation(BYTE_VALUE.emptyMap())
					.getByte(BYTE_VALUE.key(), BYTE_VALUE.defaultValue());

			assertThat(filled, is(BYTE_VALUE.value()));
			assertThat(filled, not(BYTE_VALUE.defaultValue()));
			assertThat(empty, is(BYTE_VALUE.defaultValue()));
			assertThat(empty, not(BYTE_VALUE.value()));
		}

		@Test
		void shouldReturnAnIntegerAsOptional() {
			var filled = new HeaderInformation(INTEGER_VALUE.filledMap())
					.getInt(INTEGER_VALUE.key());
			var empty = new HeaderInformation(INTEGER_VALUE.emptyMap())
					.getInt(INTEGER_VALUE.key());

			assertThat(filled.isPresent(), is(true));
			assertThat(filled.get(), is(INTEGER_VALUE.value()));
			assertThat(empty.isEmpty(), is(true));
		}

		@Test
		void shouldReturnADefaultIntegerIfNotDefined() {
			var filled = new HeaderInformation(INTEGER_VALUE.filledMap())
					.getInt(INTEGER_VALUE.key(), INTEGER_VALUE.defaultValue());
			var empty = new HeaderInformation(INTEGER_VALUE.emptyMap())
					.getInt(INTEGER_VALUE.key(), INTEGER_VALUE.defaultValue());

			assertThat(filled, is(INTEGER_VALUE.value()));
			assertThat(filled, not(INTEGER_VALUE.defaultValue()));
			assertThat(empty, is(INTEGER_VALUE.defaultValue()));
			assertThat(empty, not(INTEGER_VALUE.value()));
		}

		@Test
		void shouldReturnALongAsOptional() {
			var filled = new HeaderInformation(LONG_VALUE.filledMap())
					.getLong(LONG_VALUE.key());
			var empty = new HeaderInformation(LONG_VALUE.emptyMap())
					.getLong(LONG_VALUE.key());

			assertThat(filled.isPresent(), is(true));
			assertThat(filled.get(), is(LONG_VALUE.value()));
			assertThat(empty.isEmpty(), is(true));
		}

		@Test
		void shouldReturnADefaultLongIfNotDefined() {
			var filled = new HeaderInformation(LONG_VALUE.filledMap())
					.getLong(LONG_VALUE.key(), LONG_VALUE.defaultValue());
			var empty = new HeaderInformation(LONG_VALUE.emptyMap())
					.getLong(LONG_VALUE.key(), LONG_VALUE.defaultValue());

			assertThat(filled, is(LONG_VALUE.value()));
			assertThat(filled, not(LONG_VALUE.defaultValue()));
			assertThat(empty, is(LONG_VALUE.defaultValue()));
			assertThat(empty, not(LONG_VALUE.value()));
		}

		@Test
		void shouldReturnAFloatAsOptional() {
			var filled = new HeaderInformation(FLOAT_VALUE.filledMap())
					.getFloat(FLOAT_VALUE.key());
			var empty = new HeaderInformation(FLOAT_VALUE.emptyMap())
					.getFloat(FLOAT_VALUE.key());

			assertThat(filled.isPresent(), is(true));
			assertThat(filled.get(), is(FLOAT_VALUE.value()));
			assertThat(empty.isEmpty(), is(true));
		}

		@Test
		void shouldReturnADefaultFloatIfNotDefined() {
			var filled = new HeaderInformation(FLOAT_VALUE.filledMap())
					.getFloat(FLOAT_VALUE.key(), FLOAT_VALUE.defaultValue());
			var empty = new HeaderInformation(FLOAT_VALUE.emptyMap())
					.getFloat(FLOAT_VALUE.key(), FLOAT_VALUE.defaultValue());

			assertThat(filled, is(FLOAT_VALUE.value()));
			assertThat(filled, not(FLOAT_VALUE.defaultValue()));
			assertThat(empty, is(FLOAT_VALUE.defaultValue()));
			assertThat(empty, not(FLOAT_VALUE.value()));
		}

		@Test
		void shouldReturnADoubleAsOptional() {
			var filled = new HeaderInformation(DOUBLE_VALUE.filledMap())
					.getDouble(DOUBLE_VALUE.key());
			var empty = new HeaderInformation(DOUBLE_VALUE.emptyMap())
					.getDouble(DOUBLE_VALUE.key());

			assertThat(filled.isPresent(), is(true));
			assertThat(filled.get(), is(DOUBLE_VALUE.value()));
			assertThat(empty.isEmpty(), is(true));
		}

		@Test
		void shouldReturnADefaultDoubleIfNotDefined() {
			var filled = new HeaderInformation(DOUBLE_VALUE.filledMap())
					.getDouble(DOUBLE_VALUE.key(), DOUBLE_VALUE.defaultValue());
			var empty = new HeaderInformation(DOUBLE_VALUE.emptyMap())
					.getDouble(DOUBLE_VALUE.key(), DOUBLE_VALUE.defaultValue());

			assertThat(filled, is(DOUBLE_VALUE.value()));
			assertThat(filled, not(DOUBLE_VALUE.defaultValue()));
			assertThat(empty, is(DOUBLE_VALUE.defaultValue()));
			assertThat(empty, not(DOUBLE_VALUE.value()));
		}

		@Test
		void shouldReturnACharacterAsOptional() {
			var filled = new HeaderInformation(CHARACTER_VALUE.filledMap())
					.getChar(CHARACTER_VALUE.key());
			var empty = new HeaderInformation(CHARACTER_VALUE.emptyMap())
					.getChar(CHARACTER_VALUE.key());

			assertThat(filled.isPresent(), is(true));
			assertThat(filled.get(), is(CHARACTER_VALUE.value()));
			assertThat(empty.isEmpty(), is(true));
		}

		@Test
		void shouldReturnADefaultCharacterIfNotDefined() {
			var filled = new HeaderInformation(CHARACTER_VALUE.filledMap())
					.getChar(CHARACTER_VALUE.key(), CHARACTER_VALUE.defaultValue());
			var empty = new HeaderInformation(CHARACTER_VALUE.emptyMap())
					.getChar(CHARACTER_VALUE.key(), CHARACTER_VALUE.defaultValue());

			assertThat(filled, is(CHARACTER_VALUE.value()));
			assertThat(filled, not(CHARACTER_VALUE.defaultValue()));
			assertThat(empty, is(CHARACTER_VALUE.defaultValue()));
			assertThat(empty, not(CHARACTER_VALUE.value()));
		}

		@Test
		void shouldReturnABooleanAsOptional() {
			var filled = new HeaderInformation(BOOLEAN_VALUE.filledMap())
					.getBoolean(BOOLEAN_VALUE.key());
			var empty = new HeaderInformation(BOOLEAN_VALUE.emptyMap())
					.getBoolean(BOOLEAN_VALUE.key());

			assertThat(filled.isPresent(), is(true));
			assertThat(filled.get(), is(BOOLEAN_VALUE.value()));
			assertThat(empty.isEmpty(), is(true));
		}

		@Test
		void shouldReturnADefaultBooleanIfNotDefined() {
			var filled = new HeaderInformation(BOOLEAN_VALUE.filledMap())
					.getBoolean(BOOLEAN_VALUE.key(), BOOLEAN_VALUE.defaultValue());
			var empty = new HeaderInformation(BOOLEAN_VALUE.emptyMap())
					.getBoolean(BOOLEAN_VALUE.key(), BOOLEAN_VALUE.defaultValue());

			assertThat(filled, is(BOOLEAN_VALUE.value()));
			assertThat(filled, not(BOOLEAN_VALUE.defaultValue()));
			assertThat(empty, is(BOOLEAN_VALUE.defaultValue()));
			assertThat(empty, not(BOOLEAN_VALUE.value()));
		}

		@Test
		void shouldReturnAllValues() {
			var key = "key";
			var list = List.of("val1", "val2", "val3");
			var multiMap = MultiMap.caseInsensitiveMultiMap().add(key, list);

			var allValues = new HeaderInformation(multiMap).getAll(key);

			assertThat(allValues, contains(list.toArray()));
		}
	}

	@Nested
	public class AdderTest {

		@Test
		void shouldAddStringsToHeaders() {
			var count = 3;
			var filledMap = filledMap(STRING_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.add(STRING_VALUE.key(), STRING_VALUE.value(), STRING_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(STRING_VALUE.key());

			assertThat(allValues, hasItems(STRING_VALUE.representation(), STRING_VALUE.defaultRepresentation()));
			// number of already existing elements + value + default value (ergo 2)
			assertThat(allValues.size(), is(count + 2));
		}

		@Test
		void shouldAddBytesToHeaders() {
			var count = 3;
			var filledMap = filledMap(BYTE_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.add(BYTE_VALUE.key(), BYTE_VALUE.value(), BYTE_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(BYTE_VALUE.key());

			assertThat(allValues, hasItems(BYTE_VALUE.representation(), BYTE_VALUE.defaultRepresentation()));
			// number of already existing elements + value + default value (ergo 2)
			assertThat(allValues.size(), is(count + 2));
		}

		@Test
		void shouldAddIntegersToHeaders() {
			var count = 3;
			var filledMap = filledMap(INTEGER_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.add(INTEGER_VALUE.key(), INTEGER_VALUE.value(), INTEGER_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(INTEGER_VALUE.key());

			assertThat(allValues, hasItems(INTEGER_VALUE.representation(), INTEGER_VALUE.defaultRepresentation()));
			// number of already existing elements + value + default value (ergo 2)
			assertThat(allValues.size(), is(count + 2));
		}

		@Test
		void shouldAddLongsToHeaders() {
			var count = 3;
			var filledMap = filledMap(LONG_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.add(LONG_VALUE.key(), LONG_VALUE.value(), LONG_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(LONG_VALUE.key());

			assertThat(allValues, hasItems(LONG_VALUE.representation(), LONG_VALUE.defaultRepresentation()));
			// number of already existing elements + value + default value (ergo 2)
			assertThat(allValues.size(), is(count + 2));
		}

		@Test
		void shouldAddFloatsToHeaders() {
			var count = 3;
			var filledMap = filledMap(FLOAT_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.add(FLOAT_VALUE.key(), FLOAT_VALUE.value(), FLOAT_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(FLOAT_VALUE.key());

			assertThat(allValues, hasItems(FLOAT_VALUE.representation(), FLOAT_VALUE.defaultRepresentation()));
			// number of already existing elements + value + default value (ergo 2)
			assertThat(allValues.size(), is(count + 2));
		}

		@Test
		void shouldAddDoublesToHeaders() {
			var count = 3;
			var filledMap = filledMap(DOUBLE_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.add(DOUBLE_VALUE.key(), DOUBLE_VALUE.value(), DOUBLE_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(DOUBLE_VALUE.key());

			assertThat(allValues, hasItems(DOUBLE_VALUE.representation(), DOUBLE_VALUE.defaultRepresentation()));
			// number of already existing elements + value + default value (ergo 2)
			assertThat(allValues.size(), is(count + 2));
		}

		@Test
		void shouldAddCharactersToHeaders() {
			var count = 3;
			var filledMap = filledMap(CHARACTER_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.add(CHARACTER_VALUE.key(), CHARACTER_VALUE.value(), CHARACTER_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(CHARACTER_VALUE.key());

			assertThat(allValues, hasItems(CHARACTER_VALUE.representation(), CHARACTER_VALUE.defaultRepresentation()));
			// number of already existing elements + value + default value (ergo 2)
			assertThat(allValues.size(), is(count + 2));
		}

		@Test
		void shouldAddBooleansToHeaders() {
			var count = 3;
			var filledMap = filledMap(BOOLEAN_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.add(BOOLEAN_VALUE.key(), BOOLEAN_VALUE.value(), BOOLEAN_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(BOOLEAN_VALUE.key());

			assertThat(allValues, hasItems(BOOLEAN_VALUE.representation(), BOOLEAN_VALUE.defaultRepresentation()));
			// number of already existing elements + value + default value (ergo 2)
			assertThat(allValues.size(), is(count + 2));
		}

		@Test
		void shouldAddAllMultiMapsToHeaders() {
			var information = new HeaderInformation().addAll(MULTI_MAP_1).addAll(MULTI_MAP_2).addAll(MULTI_MAP_3);
			var merged = information.getHeaders();

			assertThat(merged.get(PARAM_1_KEY), is(PARAM_1_VALUE));
			assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
			assertThat(merged.get(PARAM_3_KEY), is(PARAM_3_VALUE));
		}

		@Test
		void shouldAddAllMapsToHeaders() {
			var map1 = new HashMap<String, String>();
			var map2 = new HashMap<String, String>();
			var map3 = new HashMap<String, String>();

			map1.put(PARAM_1_KEY, PARAM_1_VALUE);
			map2.put(PARAM_2_KEY, PARAM_2_VALUE);
			map3.put(PARAM_3_KEY, PARAM_3_VALUE);

			var information = new HeaderInformation().addAll(map1).addAll(map2).addAll(map3);
			var merged = information.getHeaders();

			assertThat(merged.get(PARAM_1_KEY), is(PARAM_1_VALUE));
			assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
			assertThat(merged.get(PARAM_3_KEY), is(PARAM_3_VALUE));
		}

		@Test
		void shouldAddAllHeaderInformationToHeaders() {
			var information1 = new HeaderInformation(MULTI_MAP_1);
			var information2 = new HeaderInformation(MULTI_MAP_2);
			var information3 = new HeaderInformation(MULTI_MAP_3);

			var information = new HeaderInformation().addAll(information1).addAll(information2).addAll(information3);
			var merged = information.getHeaders();

			assertThat(merged.get(PARAM_1_KEY), is(PARAM_1_VALUE));
			assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
			assertThat(merged.get(PARAM_3_KEY), is(PARAM_3_VALUE));
		}
	}

	@Nested
	public class SetterTest {

		@Test
		void shouldSetStringsOnHeaders() {
			var count = 3;
			var filledMap = filledMap(STRING_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.set(STRING_VALUE.key(), STRING_VALUE.value(), STRING_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(STRING_VALUE.key());

			assertThat(allValues, hasItems(STRING_VALUE.representation(), STRING_VALUE.defaultRepresentation()));
			// should replace the existing content (ergo 2)
			assertThat(allValues.size(), is(2));
		}

		@Test
		void shouldSetBytesOnHeaders() {
			var count = 3;
			var filledMap = filledMap(BYTE_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.set(BYTE_VALUE.key(), BYTE_VALUE.value(), BYTE_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(BYTE_VALUE.key());

			assertThat(allValues, hasItems(BYTE_VALUE.representation(), BYTE_VALUE.defaultRepresentation()));
			// should replace the existing content (ergo 2)
			assertThat(allValues.size(), is(2));
		}

		@Test
		void shouldSetIntegersOnHeaders() {
			var count = 3;
			var filledMap = filledMap(INTEGER_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.set(INTEGER_VALUE.key(), INTEGER_VALUE.value(), INTEGER_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(INTEGER_VALUE.key());

			assertThat(allValues, hasItems(INTEGER_VALUE.representation(), INTEGER_VALUE.defaultRepresentation()));
			// should replace the existing content (ergo 2)
			assertThat(allValues.size(), is(2));
		}

		@Test
		void shouldSetLongsOnHeaders() {
			var count = 3;
			var filledMap = filledMap(LONG_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.set(LONG_VALUE.key(), LONG_VALUE.value(), LONG_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(LONG_VALUE.key());

			assertThat(allValues, hasItems(LONG_VALUE.representation(), LONG_VALUE.defaultRepresentation()));
			// should replace the existing content (ergo 2)
			assertThat(allValues.size(), is(2));
		}

		@Test
		void shouldSetFloatsOnHeaders() {
			var count = 3;
			var filledMap = filledMap(FLOAT_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.set(FLOAT_VALUE.key(), FLOAT_VALUE.value(), FLOAT_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(FLOAT_VALUE.key());

			assertThat(allValues, hasItems(FLOAT_VALUE.representation(), FLOAT_VALUE.defaultRepresentation()));
			// should replace the existing content (ergo 2)
			assertThat(allValues.size(), is(2));
		}

		@Test
		void shouldSetDoublesOnHeaders() {
			var count = 3;
			var filledMap = filledMap(DOUBLE_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.set(DOUBLE_VALUE.key(), DOUBLE_VALUE.value(), DOUBLE_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(DOUBLE_VALUE.key());

			assertThat(allValues, hasItems(DOUBLE_VALUE.representation(), DOUBLE_VALUE.defaultRepresentation()));
			// should replace the existing content (ergo 2)
			assertThat(allValues.size(), is(2));
		}

		@Test
		void shouldSetCharactersOnHeaders() {
			var count = 3;
			var filledMap = filledMap(CHARACTER_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.set(CHARACTER_VALUE.key(), CHARACTER_VALUE.value(), CHARACTER_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(CHARACTER_VALUE.key());

			assertThat(allValues, hasItems(CHARACTER_VALUE.representation(), CHARACTER_VALUE.defaultRepresentation()));
			// should replace the existing content (ergo 2)
			assertThat(allValues.size(), is(2));
		}

		@Test
		void shouldSetBooleansOnHeaders() {
			var count = 3;
			var filledMap = filledMap(BOOLEAN_VALUE.key(), count);
			var information = new HeaderInformation(filledMap)
					.set(BOOLEAN_VALUE.key(), BOOLEAN_VALUE.value(), BOOLEAN_VALUE.defaultValue());
			var allValues = information.getHeaders().getAll(BOOLEAN_VALUE.key());

			assertThat(allValues, hasItems(BOOLEAN_VALUE.representation(), BOOLEAN_VALUE.defaultRepresentation()));
			// should replace the existing content (ergo 2)
			assertThat(allValues.size(), is(2));
		}

		@Test
		void shouldSetAllMultiMapsOnHeaders() {
			var information = new HeaderInformation().setAll(MULTI_MAP_1).setAll(MULTI_MAP_2);
			var merged = information.getHeaders();

			// map 2 should replace map 1
			assertThat(merged.contains(PARAM_1_KEY), is(false));
			assertThat(merged.contains(PARAM_2_KEY), is(true));
			assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
		}

		@Test
		void shouldSetAllMapsOnHeaders() {
			var map1 = new HashMap<String, String>();
			var map2 = new HashMap<String, String>();

			map1.put(PARAM_1_KEY, PARAM_1_VALUE);
			map2.put(PARAM_2_KEY, PARAM_2_VALUE);

			var information = new HeaderInformation().setAll(map1).setAll(map2);
			var merged = information.getHeaders();

			// map 2 should replace map 1
			assertThat(merged.contains(PARAM_1_KEY), is(false));
			assertThat(merged.contains(PARAM_2_KEY), is(true));
			assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
		}

		@Test
		void shouldSetAllHeaderInformationOnHeaders() {
			var information1 = new HeaderInformation(MULTI_MAP_1);
			var information2 = new HeaderInformation(MULTI_MAP_2);

			var information = new HeaderInformation().setAll(information1).setAll(information2);
			var merged = information.getHeaders();

			// header information 2 should replace header information 1
			assertThat(merged.contains(PARAM_1_KEY), is(false));
			assertThat(merged.contains(PARAM_2_KEY), is(true));
			assertThat(merged.get(PARAM_2_KEY), is(PARAM_2_VALUE));
		}
	}

	@Nested
	public class MultiMapTest {

		@Test
		void shouldReturnAnAssignmentStateFromTheMultiMap() {
			var information1 = new HeaderInformation(MULTI_MAP_1);
			var information2 = new HeaderInformation(MULTI_MAP_2);
			var information3 = new HeaderInformation(MULTI_MAP_3);

			assertThat(information1.contains(PARAM_1_KEY), is(true));
			assertThat(information1.contains(PARAM_2_KEY), is(false));
			assertThat(information1.contains(PARAM_3_KEY), is(false));
			assertThat(information2.contains(PARAM_1_KEY), is(false));
			assertThat(information2.contains(PARAM_2_KEY), is(true));
			assertThat(information2.contains(PARAM_3_KEY), is(false));
			assertThat(information3.contains(PARAM_1_KEY), is(false));
			assertThat(information3.contains(PARAM_2_KEY), is(false));
			assertThat(information3.contains(PARAM_3_KEY), is(true));
		}

		@Test
		void shouldRemoveAnAssignmentFromTheMultiMap() {
			var information1 = new HeaderInformation(MULTI_MAP_1);
			var information2 = new HeaderInformation(MULTI_MAP_2);
			var information3 = new HeaderInformation(MULTI_MAP_3);

			var headers1 = information1.remove(PARAM_1_KEY).getHeaders();
			var headers2 = information2.remove(PARAM_2_KEY).getHeaders();
			var headers3 = information3.remove(PARAM_3_KEY).getHeaders();

			assertThat(headers1.contains(PARAM_1_KEY), is(false));
			assertThat(headers2.contains(PARAM_2_KEY), is(false));
			assertThat(headers3.contains(PARAM_3_KEY), is(false));
		}

		@Test
		void shouldClearTheMultiMap() {
			var information1 = new HeaderInformation(MULTI_MAP_1);
			var information2 = new HeaderInformation(MULTI_MAP_1);
			var information3 = new HeaderInformation(MULTI_MAP_1);

			var headers1 = information1.clear().getHeaders();
			var headers2 = information2.clear().getHeaders();
			var headers3 = information3.clear().getHeaders();

			assertThat(headers1.isEmpty(), is(true));
			assertThat(headers2.isEmpty(), is(true));
			assertThat(headers3.isEmpty(), is(true));
		}

		@Test
		void shouldReturnTheSizeOfTheMultiMap() {
			var information1 = new HeaderInformation(MULTI_MAP_1);
			var information2 = new HeaderInformation(MULTI_MAP_2);
			var information3 = new HeaderInformation(MULTI_MAP_3);

			assertThat(information1.size(), is(MULTI_MAP_1.size()));
			assertThat(information2.size(), is(MULTI_MAP_2.size()));
			assertThat(information3.size(), is(MULTI_MAP_3.size()));
		}

		@Test
		void shouldReturnASetOfAssignedNamesFromTheMultiMap() {
			var map = MultiMap.caseInsensitiveMultiMap()
					.add(PARAM_1_KEY, PARAM_1_VALUE)
					.add(PARAM_2_KEY, PARAM_2_VALUE)
					.add(PARAM_3_KEY, PARAM_3_VALUE);
			var information = new HeaderInformation(map);

			assertThat(information.names(), contains(PARAM_1_KEY, PARAM_2_KEY, PARAM_3_KEY));
		}

		@Test
		void shouldReturnTheEmptyStateFromTheMultiMap() {
			var emptyMap = MultiMap.caseInsensitiveMultiMap();
			var emptyInformation = new HeaderInformation(emptyMap);
			var filledInformation = new HeaderInformation(MULTI_MAP_1);

			assertThat(emptyInformation.isEmpty(), is(true));
			assertThat(filledInformation.isEmpty(), is(false));
		}
	}

	public final String PARAM_1_KEY = "key1";
	public final String PARAM_2_KEY = "key2";
	public final String PARAM_3_KEY = "key3";

	public final String PARAM_1_VALUE = "value1";
	public final String PARAM_2_VALUE = "value2";
	public final String PARAM_3_VALUE = "value3";

	public final MultiMap MULTI_MAP_1 = MultiMap.caseInsensitiveMultiMap().add(PARAM_1_KEY, PARAM_1_VALUE);
	public final MultiMap MULTI_MAP_2 = MultiMap.caseInsensitiveMultiMap().add(PARAM_2_KEY, PARAM_2_VALUE);
	public final MultiMap MULTI_MAP_3 = MultiMap.caseInsensitiveMultiMap().add(PARAM_3_KEY, PARAM_3_VALUE);

	public final HeaderInformation HEADER_INFORMATION_1 = new HeaderInformation(MULTI_MAP_1);
	public final HeaderInformation HEADER_INFORMATION_2 = new HeaderInformation(MULTI_MAP_2);
	public final HeaderInformation HEADER_INFORMATION_3 = new HeaderInformation(MULTI_MAP_3);

	public record Value<T>(String key, T value, String representation, T defaultValue, String defaultRepresentation) {
		public MultiMap emptyMap() {
			return MultiMap.caseInsensitiveMultiMap();
		}

		public MultiMap filledMap() {
			return emptyMap().add(key(), representation());
		}
	}

	public final Value<String> STRING_VALUE = new Value<>("string", "The Box", "The Box", "Another Box", "Another Box");
	public final Value<Byte> BYTE_VALUE = new Value<>("byte", (byte) 54, "54", (byte) 120, "120");
	public final Value<Integer> INTEGER_VALUE = new Value<>("int", 42, "42", 356, "356");
	public final Value<Long> LONG_VALUE = new Value<>("long", 42L, "42", 16L, "16");
	public final Value<Float> FLOAT_VALUE = new Value<>("float", 3.14f, "3.14", 1.78f, "1.78");
	public final Value<Double> DOUBLE_VALUE = new Value<>("double", 3.14, "3.14", 1.78, "1.78");
	public final Value<Character> CHARACTER_VALUE = new Value<>("char", 'T', "T", 'W', "W");
	public final Value<Boolean> BOOLEAN_VALUE = new Value<>("bool", true, "true", false, "false");

	public MultiMap filledMap(String key, int count) {
		var map = MultiMap.caseInsensitiveMultiMap();
		for (int i = 0; i < count; i++) {
			map.add(key, "value%d".formatted(i));
		}
		return map;
	}
}
