package de.wuespace.telestion.application.loader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Use this type of configuration to indicate that your {@link TelestionLoader}
 * doesn't have a strictly typed configuration.
 *
 * @author Pablo Klaschka (@pklaschka), Cedric Boes (@cb0s), Ludwig Richter (@fussel178)
 * @see TelestionLoader#getConfig()
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UntypedConfiguration() implements LoaderConfiguration {
}
