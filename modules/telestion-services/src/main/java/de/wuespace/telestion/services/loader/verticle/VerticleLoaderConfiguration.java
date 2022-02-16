package de.wuespace.telestion.services.loader.verticle;

import de.wuespace.telestion.application.loader.LoaderConfiguration;

public interface VerticleLoaderConfiguration extends LoaderConfiguration {
	String verticlesProperty();
}
