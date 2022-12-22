package de.wuespace.telestion.examples;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.api.verticle.trait.WithSharedData;
import de.wuespace.telestion.examples.messages.SimpleMessage;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.Message;

/**
 * @author Pablo Klaschka (@pklaschka), Ludwig Richter (@fussel178)
 */
public class TestVerticle extends TelestionVerticle<TestVerticle.Configuration>
		implements WithEventBus, WithSharedData {

	public record Configuration(
			@JsonProperty String inAddress,
			@JsonProperty String outAddress,
			@JsonProperty Integer a
	) implements TelestionConfiguration {}

	@Override
	public void onStart() {
		// set default config
		setDefaultConfig(new Configuration(null, "out-here", 42));

		// normal type-safe config usage
		int test = getConfig().a + 10;
		logger.debug(getConfig().inAddress);

		// dynamic typed config usage
		int test2 = getGenericConfig().getInteger("a");
		logger.debug(getGenericConfig().getString("inAddress"));

		// want to use default config instead? No problem
		int test3 = getDefaultConfig().a;
		logger.debug(getDefaultConfig().inAddress);

		// or in a generic format?
		int test4 = getGenericDefaultConfig().getInteger("a");
		logger.debug(getGenericDefaultConfig().getString("inAddress"));

		// the default config can be updated at any time
		setDefaultConfig(new Configuration("hey", "out-here", 84));

		register(getConfig().inAddress, body -> {
			logger.info("Received message: {}", body);
			publish(getConfig().outAddress, body);
		}, SimpleMessage.class);

		register(getConfig().inAddress, this::handleStuff, SimpleMessage.class);

		localMap("bla").put("piep", "piep");

		remoteMap("blub").onComplete(res -> {
			res.result().put("piep", "piep");
		});

		defaultLocalMap().put("my-property", "my-value");
	}

	private void handleStuff(SimpleMessage body, Message<Object> raw) {
		logger.info("Received message: {}", body.title() + getConfig().a);

		setData("hello world");
	}

	public void setData(String newData) {
		remoteMap(REMOTE_MAP).onSuccess(map -> map.put("here", newData)).onSuccess(res -> {
			vertx.undeploy(deploymentID());
			vertx.deployVerticle(TestVerticle.class, new DeploymentOptions())
					.onSuccess(id -> logger.debug("My deployment id: {}", id));
		});
	}

	private static final String REMOTE_MAP = "remote-map";
}