package de.wuespace.telestion.application.loader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Use this {@link LoaderConfiguration} if you don't use any strictly typed Loader configuration.
 *
 * @author Ludwig Richter (@fussel178)
 * @see LoaderConfiguration
 * @see Loader
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GenericLoaderConfiguration() implements LoaderConfiguration {
}
