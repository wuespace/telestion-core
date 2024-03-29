package de.wuespace.telestion.api.message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.MockMessage;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class JsonMessageTest {

	@Nested
	public class AsynchronousDecodingTest {

		@Nested
		public class DecodingViaHandlerTest {

			@Test
			void shouldCallExtendedHandlerOnValidBuffer() {
				var buffer = VALID_JSON_OBJECT.toBuffer();

				var result = JsonRecord.on(TestMessage.class, buffer,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)),
						err -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, buffer,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)),
						err -> fail());

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldCallExtendedExceptionHandlerOnInvalidBuffer() {
				var buffer = INVALID_JSON_OBJECT.toBuffer();

				var result = JsonRecord.on(TestMessage.class, buffer,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, buffer,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}

			@Test
			void shouldCallBasicHandlerOnValidBuffer() {
				var buffer = VALID_JSON_OBJECT.toBuffer();

				var result = JsonRecord.on(TestMessage.class, buffer,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, buffer,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)));

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldReturnFalseOnInvalidBufferInBasicMethod() {
				var buffer = INVALID_JSON_OBJECT.toBuffer();

				var result = JsonRecord.on(TestMessage.class, buffer, message -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, buffer, message -> fail());

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}

			@Test
			void shouldCallExtendedHandlerOnValidJsonString() {
				var jsonString = VALID_JSON_OBJECT.toString();

				var result = JsonRecord.on(TestMessage.class, jsonString,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)),
						err -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, jsonString,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)),
						err -> fail());

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldCallExtendedExceptionHandlerOnInvalidJsonString() {
				var jsonString = INVALID_JSON_OBJECT.toString();

				var result = JsonRecord.on(TestMessage.class, jsonString,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, jsonString,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}

			@Test
			void shouldCallBasicHandlerOnValidJsonString() {
				var jsonString = VALID_JSON_OBJECT.toString();

				var result = JsonRecord.on(TestMessage.class, jsonString,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, jsonString,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)));

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldReturnFalseOnInvalidJsonStringInBasicMethod() {
				var jsonString = INVALID_JSON_OBJECT.toString();

				var result = JsonRecord.on(TestMessage.class, jsonString, message -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, jsonString, message -> fail());

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}

			@Test
			void shouldCallExtendedHandlerOnValidPOJO() {
				var result = JsonRecord.on(TestMessage.class, VALID_POJO,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)),
						err -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, VALID_POJO,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)),
						err -> fail());

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldCallExtendedExceptionHandlerOnInvalidPOJO() {
				var result = JsonRecord.on(TestMessage.class, INVALID_POJO,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, INVALID_POJO,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}

			@Test
			void shouldCallBasicHandlerOnValidPOJO() {
				var result = JsonRecord.on(TestMessage.class, VALID_POJO,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, VALID_POJO,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)));

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldReturnFalseOnInvalidPOJOInBasicMethod() {
				var result = JsonRecord.on(TestMessage.class, INVALID_POJO, message -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, INVALID_POJO, message -> fail());

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}

			@Test
			void shouldCallExtendedHandlerOnValidJsonObject() {
				var result = JsonRecord.on(TestMessage.class, VALID_JSON_OBJECT,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)),
						err -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, VALID_JSON_OBJECT,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)),
						err -> fail());

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldCallExtendedExceptionHandlerOnInvalidJsonObject() {
				var result = JsonRecord.on(TestMessage.class, INVALID_JSON_OBJECT,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, INVALID_JSON_OBJECT,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}

			@Test
			void shouldCallBasicHandlerOnValidJsonObject() {
				var result = JsonRecord.on(TestMessage.class, VALID_JSON_OBJECT,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, VALID_JSON_OBJECT,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)));

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldReturnFalseOnInvalidJsonObjectInBasicMethod() {
				var result = JsonRecord.on(TestMessage.class, INVALID_JSON_OBJECT, message -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, INVALID_JSON_OBJECT,
						message -> fail());

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}

			@Test
			void shouldCallExtendedHandlerOnValidEventBusMessage() {
				var result = JsonRecord.on(TestMessage.class, VALID_EVENTBUS_MESSAGE,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)),
						err -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, VALID_EVENTBUS_MESSAGE,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)),
						err -> fail());

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldCallExtendedExceptionHandlerOnInvalidEventBusMessage() {
				var result = JsonRecord.on(TestMessage.class, INVALID_EVENTBUS_MESSAGE,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, INVALID_EVENTBUS_MESSAGE,
						message -> fail(),
						err -> assertThat(err, isA(RuntimeException.class)));

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}

			@Test
			void shouldCallBasicHandlerOnValidEventBusMessage() {
				var result = JsonRecord.on(TestMessage.class, VALID_EVENTBUS_MESSAGE,
						message -> assertThat(message, is(VALID_TEST_MESSAGE)));
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, VALID_EVENTBUS_MESSAGE,
						message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)));

				assertThat(result, is(true));
				assertThat(anotherResult, is(true));
			}

			@Test
			void shouldReturnFalseOnInvalidEventBusMessageInBasicMethod() {
				var result = JsonRecord.on(TestMessage.class, INVALID_EVENTBUS_MESSAGE, message -> fail());
				var anotherResult = JsonRecord.on(AnotherTestMessage.class, INVALID_EVENTBUS_MESSAGE,
						message -> fail());

				assertThat(result, is(false));
				assertThat(anotherResult, is(false));
			}
		}

		@Nested
		public class DecodingViaFutureTest {

			@Test
			void shouldSucceedOnValidBuffer() {
				var buffer = VALID_JSON_OBJECT.toBuffer();

				JsonRecord.on(TestMessage.class, buffer)
						.onSuccess(message -> assertThat(message, is(VALID_TEST_MESSAGE)))
						.onFailure(err -> fail());
				JsonRecord.on(AnotherTestMessage.class, buffer)
						.onSuccess(message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)))
						.onFailure(err -> fail());
			}

			@Test
			void shouldFailOnInvalidBuffer() {
				var buffer = INVALID_JSON_OBJECT.toBuffer();

				JsonRecord.on(TestMessage.class, buffer)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
				JsonRecord.on(AnotherTestMessage.class, buffer)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
			}

			@Test
			void shouldSucceedOnValidJsonString() {
				var jsonString = VALID_JSON_OBJECT.toString();

				JsonRecord.on(TestMessage.class, jsonString)
						.onSuccess(message -> assertThat(message, is(VALID_TEST_MESSAGE)))
						.onFailure(err -> fail());
				JsonRecord.on(AnotherTestMessage.class, jsonString)
						.onSuccess(message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)))
						.onFailure(err -> fail());
			}

			@Test
			void shouldFailOnInvalidJsonString() {
				var jsonString = INVALID_JSON_OBJECT.toString();

				JsonRecord.on(TestMessage.class, jsonString)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
				JsonRecord.on(AnotherTestMessage.class, jsonString)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
			}

			@Test
			void shouldSucceedOnValidPOJO() {
				JsonRecord.on(TestMessage.class, VALID_POJO)
						.onSuccess(message -> assertThat(message, is(VALID_TEST_MESSAGE)))
						.onFailure(err -> fail());
				JsonRecord.on(AnotherTestMessage.class, VALID_POJO)
						.onSuccess(message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)))
						.onFailure(err -> fail());
			}

			@Test
			void shouldFailOnInvalidPOJO() {
				JsonRecord.on(TestMessage.class, INVALID_POJO)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
				JsonRecord.on(AnotherTestMessage.class, INVALID_POJO)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
			}

			@Test
			void shouldSucceedOnValidJsonObject() {
				JsonRecord.on(TestMessage.class, VALID_JSON_OBJECT)
						.onSuccess(message -> assertThat(message, is(VALID_TEST_MESSAGE)))
						.onFailure(err -> fail());
				JsonRecord.on(AnotherTestMessage.class, VALID_JSON_OBJECT)
						.onSuccess(message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)))
						.onFailure(err -> fail());
			}

			@Test
			void shouldFailOnInvalidJsonObject() {
				JsonRecord.on(TestMessage.class, INVALID_JSON_OBJECT)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
				JsonRecord.on(AnotherTestMessage.class, INVALID_JSON_OBJECT)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
			}

			@Test
			void shouldSucceedOnValidEventBusMessage() {
				JsonRecord.on(TestMessage.class, VALID_EVENTBUS_MESSAGE)
						.onSuccess(message -> assertThat(message, is(VALID_TEST_MESSAGE)))
						.onFailure(err -> fail());
				JsonRecord.on(AnotherTestMessage.class, VALID_EVENTBUS_MESSAGE)
						.onSuccess(message -> assertThat(message, is(VALID_ANOTHER_TEST_MESSAGE)))
						.onFailure(err -> fail());
			}

			@Test
			void shouldFailOnInvalidEventBusMessage() {
				JsonRecord.on(TestMessage.class, INVALID_EVENTBUS_MESSAGE)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
				JsonRecord.on(AnotherTestMessage.class, INVALID_EVENTBUS_MESSAGE)
						.onSuccess(message -> fail())
						.onFailure(err -> assertThat(err, isA(RuntimeException.class)));
			}
		}
	}

	@Nested
	public class SynchronousDecodingTest {

		@Test
		void shouldDecodeAValidBuffer() {
			var buffer = VALID_JSON_OBJECT.toBuffer();

			var testMessage = JsonRecord.from(buffer, TestMessage.class);
			var anotherTestMessage = JsonRecord.from(buffer, AnotherTestMessage.class);

			assertThat(testMessage, is(VALID_TEST_MESSAGE));
			assertThat(anotherTestMessage, is(VALID_ANOTHER_TEST_MESSAGE));
		}

		@Test
		void shouldNotDecodeAnInvalidBuffer() {
			var buffer = INVALID_JSON_OBJECT.toBuffer();

			// capture both DecodeException and IllegalArgumentException
			assertThrows(RuntimeException.class, () -> {
				var testMessage = JsonRecord.from(buffer, TestMessage.class);
				var anotherTestMessage = JsonRecord.from(buffer, AnotherTestMessage.class);

				assertThat(testMessage, not(VALID_TEST_MESSAGE));
				assertThat(anotherTestMessage, not(VALID_ANOTHER_TEST_MESSAGE));
			});
		}

		@Test
		void shouldDecodeAValidJsonString() {
			var jsonString = VALID_JSON_OBJECT.toString();

			var testMessage = JsonRecord.from(jsonString, TestMessage.class);
			var anotherTestMessage = JsonRecord.from(jsonString, AnotherTestMessage.class);

			assertThat(testMessage, is(VALID_TEST_MESSAGE));
			assertThat(anotherTestMessage, is(VALID_ANOTHER_TEST_MESSAGE));
		}

		@Test
		void shouldNotDecodeAnInvalidJsonString() {
			var jsonString = INVALID_JSON_OBJECT.toString();

			// capture both DecodeException and IllegalArgumentException
			assertThrows(RuntimeException.class, () -> {
				var testMessage = JsonRecord.from(jsonString, TestMessage.class);
				var anotherTestMessage = JsonRecord.from(jsonString, AnotherTestMessage.class);

				assertThat(testMessage, not(VALID_TEST_MESSAGE));
				assertThat(anotherTestMessage, not(VALID_ANOTHER_TEST_MESSAGE));
			});
		}

		@Test
		void shouldDecodeAValidPOJO() {
			var testMessage = JsonRecord.from(VALID_POJO, TestMessage.class);
			var anotherTestMessage = JsonRecord.from(VALID_POJO, AnotherTestMessage.class);

			assertThat(testMessage, is(VALID_TEST_MESSAGE));
			assertThat(anotherTestMessage, is(VALID_ANOTHER_TEST_MESSAGE));
		}

		@Test
		void shouldNotDecodeAnInvalidPOJO() {
			// capture both DecodeException and IllegalArgumentException
			assertThrows(RuntimeException.class, () -> {
				var testMessage = JsonRecord.from(INVALID_POJO, TestMessage.class);
				var anotherTestMessage = JsonRecord.from(INVALID_POJO, AnotherTestMessage.class);

				assertThat(testMessage, not(VALID_TEST_MESSAGE));
				assertThat(anotherTestMessage, not(VALID_ANOTHER_TEST_MESSAGE));
			});
		}

		@Test
		void shouldDecodeAValidJsonObject() {
			var testMessage = JsonRecord.from(VALID_JSON_OBJECT, TestMessage.class);
			var anotherTestMessage = JsonRecord.from(VALID_JSON_OBJECT, AnotherTestMessage.class);

			assertThat(testMessage, is(VALID_TEST_MESSAGE));
			assertThat(anotherTestMessage, is(VALID_ANOTHER_TEST_MESSAGE));
		}

		@Test
		void shouldNotDecodeAnInvalidJsonObject() {
			// capture both DecodeException and IllegalArgumentException
			assertThrows(RuntimeException.class, () -> {
				var testMessage = JsonRecord.from(INVALID_JSON_OBJECT, TestMessage.class);
				var anotherTestMessage = JsonRecord.from(INVALID_JSON_OBJECT, AnotherTestMessage.class);

				assertThat(testMessage, not(VALID_TEST_MESSAGE));
				assertThat(anotherTestMessage, not(VALID_ANOTHER_TEST_MESSAGE));
			});
		}

		@Test
		void shouldDecodeAValidEventBusMessage() {
			var testMessage = JsonRecord.from(VALID_EVENTBUS_MESSAGE, TestMessage.class);
			var anotherTestMessage = JsonRecord.from(VALID_EVENTBUS_MESSAGE, AnotherTestMessage.class);

			assertThat(testMessage, is(VALID_TEST_MESSAGE));
			assertThat(anotherTestMessage, is(VALID_ANOTHER_TEST_MESSAGE));
		}

		@Test
		void shouldNotDecodeAnInvalidEventBusMessage() {
			// capture both DecodeException and IllegalArgumentException
			assertThrows(RuntimeException.class, () -> {
				var testMessage = JsonRecord.from(INVALID_EVENTBUS_MESSAGE, TestMessage.class);
				var anotherTestMessage = JsonRecord.from(INVALID_EVENTBUS_MESSAGE, AnotherTestMessage.class);

				assertThat(testMessage, not(VALID_TEST_MESSAGE));
				assertThat(anotherTestMessage, not(VALID_ANOTHER_TEST_MESSAGE));
			});
		}
	}

	@Nested
	public class SynchronousEncodingTest {

		@Test
		@Deprecated(since = "0.9.0")
		void shouldEncodeToJson() {
			var testJsonObject = VALID_TEST_MESSAGE.json();
			var anotherTestJsonObject = VALID_ANOTHER_TEST_MESSAGE.json();

			assertThat(testJsonObject, is(VALID_JSON_OBJECT));
			assertThat(testJsonObject, not(INVALID_JSON_OBJECT));
			assertThat(anotherTestJsonObject, is(VALID_JSON_OBJECT));
			assertThat(anotherTestJsonObject, not(INVALID_JSON_OBJECT));
		}

		@Test
		void shouldEncodeToJsonObject() {
			var testJsonObject = VALID_TEST_MESSAGE.toJsonObject();
			var anotherTestJsonObject = VALID_ANOTHER_TEST_MESSAGE.toJsonObject();

			assertThat(testJsonObject, is(VALID_JSON_OBJECT));
			assertThat(testJsonObject, not(INVALID_JSON_OBJECT));
			assertThat(anotherTestJsonObject, is(VALID_JSON_OBJECT));
			assertThat(anotherTestJsonObject, not(INVALID_JSON_OBJECT));
		}

		@Test
		void shouldEncodeToPrettyJsonString() {
			var validJsonString = VALID_JSON_OBJECT.encodePrettily();
			var invalidJsonString = INVALID_JSON_OBJECT.encodePrettily();

			var testJsonString = VALID_TEST_MESSAGE.toJsonString(true);
			var anotherTestJsonString = VALID_ANOTHER_TEST_MESSAGE.toJsonString(true);

			assertThat(testJsonString, is(validJsonString));
			assertThat(testJsonString, not(invalidJsonString));
			assertThat(anotherTestJsonString, is(validJsonString));
			assertThat(anotherTestJsonString, not(invalidJsonString));
		}

		@Test
		void shouldEncodeToCompressedJsonString() {
			var validJsonString = VALID_JSON_OBJECT.encode();
			var invalidJsonString = INVALID_JSON_OBJECT.encode();

			var testJsonString = VALID_TEST_MESSAGE.toJsonString(false);
			var anotherTestJsonString = VALID_ANOTHER_TEST_MESSAGE.toJsonString(false);

			assertThat(testJsonString, is(validJsonString));
			assertThat(testJsonString, not(invalidJsonString));
			assertThat(anotherTestJsonString, is(validJsonString));
			assertThat(anotherTestJsonString, not(invalidJsonString));
		}

		@Test
		void shouldEncodeToCompressedJsonStringByDefault() {
			var validJsonString = VALID_JSON_OBJECT.encode();
			var invalidJsonString = INVALID_JSON_OBJECT.encode();

			var testJsonString = VALID_TEST_MESSAGE.toJsonString();
			var anotherTestJsonString = VALID_ANOTHER_TEST_MESSAGE.toJsonString();

			assertThat(testJsonString, is(validJsonString));
			assertThat(testJsonString, not(invalidJsonString));
			assertThat(anotherTestJsonString, is(validJsonString));
			assertThat(anotherTestJsonString, not(invalidJsonString));
		}

		@Test
		void shouldEncodeToCompressedJsonBuffer() {
			var validJsonBuffer = Json.CODEC.toBuffer(VALID_JSON_OBJECT, false);
			var invalidJsonBuffer = Json.CODEC.toBuffer(INVALID_JSON_OBJECT, false);

			var testJsonBuffer = VALID_TEST_MESSAGE.toJsonBuffer(false);
			var anotherTestJsonBuffer = VALID_ANOTHER_TEST_MESSAGE.toJsonBuffer(false);

			assertThat(testJsonBuffer, is(validJsonBuffer));
			assertThat(testJsonBuffer, not(invalidJsonBuffer));
			assertThat(anotherTestJsonBuffer, is(validJsonBuffer));
			assertThat(anotherTestJsonBuffer, not(invalidJsonBuffer));
		}

		@Test
		void shouldEncodeToPrettyJsonBuffer() {
			var validJsonBuffer = Json.CODEC.toBuffer(VALID_JSON_OBJECT, true);
			var invalidJsonBuffer = Json.CODEC.toBuffer(INVALID_JSON_OBJECT, true);

			var testJsonBuffer = VALID_TEST_MESSAGE.toJsonBuffer(true);
			var anotherTestJsonBuffer = VALID_ANOTHER_TEST_MESSAGE.toJsonBuffer(true);

			assertThat(testJsonBuffer, is(validJsonBuffer));
			assertThat(testJsonBuffer, not(invalidJsonBuffer));
			assertThat(anotherTestJsonBuffer, is(validJsonBuffer));
			assertThat(anotherTestJsonBuffer, not(invalidJsonBuffer));
		}

		@Test
		void shouldEncodeToCompressedJsonBufferByDefault() {
			var validJsonBuffer = Json.CODEC.toBuffer(VALID_JSON_OBJECT, false);
			var invalidJsonBuffer = Json.CODEC.toBuffer(INVALID_JSON_OBJECT, false);

			var testJsonBuffer = VALID_TEST_MESSAGE.toJsonBuffer();
			var anotherTestJsonBuffer = VALID_ANOTHER_TEST_MESSAGE.toJsonBuffer();

			assertThat(testJsonBuffer, is(validJsonBuffer));
			assertThat(testJsonBuffer, not(invalidJsonBuffer));
			assertThat(anotherTestJsonBuffer, is(validJsonBuffer));
			assertThat(anotherTestJsonBuffer, not(invalidJsonBuffer));
		}
	}

	@Test
	void shouldReturnTheClassNameOfAMessage() {
		assertThat(VALID_TEST_MESSAGE.className(), is(TestMessage.class.getName()));
		assertThat(VALID_ANOTHER_TEST_MESSAGE.className(), is(AnotherTestMessage.class.getName()));
	}

	@Test
	void shouldEncodeAndDecodeAMessageViaAJsonObject() {
		var encoded = VALID_TEST_MESSAGE.toJsonObject();
		assertThat(JsonRecord.from(encoded, TestMessage.class), is(VALID_TEST_MESSAGE));
		// should also decode to another test message type with the same parameters
		assertThat(JsonRecord.from(encoded, AnotherTestMessage.class), is(VALID_ANOTHER_TEST_MESSAGE));
	}

	@Test
	void shouldEncodeAndDecodeAMessageViaAJsonString() {
		var encoded = VALID_TEST_MESSAGE.toJsonString();
		assertThat(JsonRecord.from(encoded, TestMessage.class), is(VALID_TEST_MESSAGE));
		// should also decode to another test message type with the same parameters
		assertThat(JsonRecord.from(encoded, AnotherTestMessage.class), is(VALID_ANOTHER_TEST_MESSAGE));
	}

	@Test
	void shouldEncodeAndDecodeAMessageViaABuffer() {
		var encoded = VALID_TEST_MESSAGE.toJsonBuffer();
		assertThat(JsonRecord.from(encoded, TestMessage.class), is(VALID_TEST_MESSAGE));
		// should also decode to another test message type with the same parameters
		assertThat(JsonRecord.from(encoded, AnotherTestMessage.class), is(VALID_ANOTHER_TEST_MESSAGE));
	}

	// "Invalid" means the parsing should fail due to incompatible parameters types

	public static class ValidPojoMessage {
		public float param;

		public ValidPojoMessage(float param) {
			this.param = param;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ValidPojoMessage that)) return false;
			return Float.compare(that.param, param) == 0;
		}

		@Override
		public int hashCode() {
			return Objects.hash(param);
		}
	}

	public static class InvalidPojoMessage {
		public boolean param;

		public InvalidPojoMessage(boolean param) {
			this.param = param;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof InvalidPojoMessage that)) return false;
			return param == that.param;
		}

		@Override
		public int hashCode() {
			return Objects.hash(param);
		}
	}

	public record TestMessage(@JsonProperty float param) implements JsonRecord {
	}

	public record AnotherTestMessage(@JsonProperty float param) implements JsonRecord {
	}

	public final float VALID_PARAM = 0.0f;

	public final boolean INVALID_PARAM = true;

	public final ValidPojoMessage VALID_POJO = new ValidPojoMessage(VALID_PARAM);

	public final InvalidPojoMessage INVALID_POJO = new InvalidPojoMessage(INVALID_PARAM);

	public final JsonObject VALID_JSON_OBJECT = new JsonObject().put("param", VALID_PARAM);

	public final JsonObject INVALID_JSON_OBJECT = new JsonObject().put("binary", INVALID_PARAM);

	public final Message<Object> VALID_EVENTBUS_MESSAGE = new MockMessage<>(null, VALID_JSON_OBJECT);

	public final Message<Object> INVALID_EVENTBUS_MESSAGE = new MockMessage<>(null, INVALID_JSON_OBJECT);

	public final TestMessage VALID_TEST_MESSAGE = new TestMessage(VALID_PARAM);

	public final AnotherTestMessage VALID_ANOTHER_TEST_MESSAGE = new AnotherTestMessage(VALID_PARAM);
}
