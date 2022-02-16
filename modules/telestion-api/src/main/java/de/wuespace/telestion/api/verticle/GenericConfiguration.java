package de.wuespace.telestion.api.verticle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The type of configuration for a {@link TelestionVerticle} that you use
 * to indicate that the verticle does not have a strictly typed configuration.
 *
 * @author Pablo Klaschka (@pklaschka), Ludwig Richter (@fussel178)
 * @see TelestionVerticle#getConfig()
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GenericConfiguration() implements TelestionConfiguration {
}
