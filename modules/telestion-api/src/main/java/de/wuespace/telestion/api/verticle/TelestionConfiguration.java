package de.wuespace.telestion.api.verticle;

import de.wuespace.telestion.api.message.JsonMessage;

/**
 * The base class for all Telestion Verticle configurations.
 * It extends {@link JsonMessage} so all configurations are also valid json classes.
 *
 * @author Cedric Boes (cb0s), Pablo Klaschka (@pklaschka), Jan von Pichowski (@jvpichowski),
 * 			Ludwig Richter (@fussel178)
 */
public interface TelestionConfiguration extends JsonMessage {
}
