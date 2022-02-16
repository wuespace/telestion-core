package de.wuespace.telestion.application.loader;

import de.wuespace.telestion.api.message.JsonMessage;

/**
 * The base class for all {@link Loader Loader} configurations.
 * It extends {@link JsonMessage} so all configurations are also valid json classes.
 *
 * @author Ludwig Richter (@fussel178)
 */
public interface LoaderConfiguration extends JsonMessage {
}
