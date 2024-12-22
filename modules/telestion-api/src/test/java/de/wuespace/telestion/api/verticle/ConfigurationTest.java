package de.wuespace.telestion.api.verticle;

import de.wuespace.telestion.api.message.JsonMessage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>
 *     <h1>Configuration Tests</h1>
 *     Corresponding tests for the configuration classes provided by the Telestion Project.
 * </p>
 * <p>
 *     <h2>{@link TelestionConfiguration}</h2>
 *     This test checks if {@link TelestionConfiguration}:
 *     <ul>
 *         <li>
 *             Is an Interface:<br/>
 *             Records can only extend Interfaces - Messages or configurations primarily contain data which makes them a good
 *             match
 *         </li>
 *         <li>
 *             Extends {@link JsonMessage}:<br/>
 *             To be serializable and used by other convenience methods of the Telestion Project
 *         </li>
 *     </ul>
 * </p>
 * <p>
 *     <h2>Provided Configurations</h2>
 *     In contrast to the base interface {@link TelestionConfiguration} the other provided Configurations:
 *     <ul>
 *         <li>Must implement {@link TelestionConfiguration} (to use all the features of the Telestion-Project)</li>
 *         <li>
 *             Should be a Record:<br/>
 *             As these classes should only contain data, they are a prime example for records and therefore be
 *             implemented like this.
 *         </li>
 *         <li>Can be instantiated (is neither abstract, nor an interface)</li>
 *     </ul>
 * </p>
 *
 * @author Cedric Boes (cb0s)
 */
public final class ConfigurationTest {
	@Nested
	public class TelestionConfigTest {
		@Test
		public void shouldBeInterface() {
			// Needs to be an interface to be implemented by `Records`
			assertThat(TelestionConfiguration.class.isInterface(), is(true));
		}

		@Test
		public void shouldExtendJsonMessage() {
			assertThat(JsonMessage.class.isAssignableFrom(TelestionConfiguration.class), is(true));
		}
	}

	@Nested
	public class ProvidedConfigurationTest {
		@Test
		public void shouldImplementTelestionConfiguration() {
			assertThat(TelestionConfiguration.class.isAssignableFrom(NoConfiguration.class), is(true));
			assertThat(TelestionConfiguration.class.isAssignableFrom(UntypedConfiguration.class), is(true));
		}

		@Test
		public void shouldBeRecord() {
			assertThat(NoConfiguration.class.isRecord(), is(true));
			assertThat(UntypedConfiguration.class.isRecord(), is(true));
		}

		@Test
		public void shouldBeInstantiable() {
			// Should not throw exceptions
			new NoConfiguration();
			new UntypedConfiguration();
		}
	}
}
