package de.wuespace.telestion.api.verticle;

/**
 * The Configuration class which should be used if no config is needed to infer the generics of a
 * {@link TelestionVerticle}.
 *
 * @author Cedric Boes (cb0s), Ludwig Richter (@fussel178)
 */
public record GenericConfiguration() implements TelestionConfiguration {
}
