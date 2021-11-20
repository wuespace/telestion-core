package de.wuespace.telestion.api;

import de.wuespace.telestion.api.message.JsonMessage;

/**
 * The base class for all Telestion Verticle configurations.
 * It extends {@link JsonMessage} so all configurations are also valid json classes.
 *
 * @author Cedric Bös, Pablo Klaschka, Jan von Pichowski, Ludwig Richter
 */
public interface TelestionConfiguration extends JsonMessage {
}
