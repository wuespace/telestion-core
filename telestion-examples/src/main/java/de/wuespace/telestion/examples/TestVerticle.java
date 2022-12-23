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
        verticleConfigStrategy.setDefaultConfig(new Configuration(null, "out-here", 42), type);

		// normal type-safe config usage
		int test = verticleConfigStrategy.getConfig().a + 10;
		logger.debug(verticleConfigStrategy.getConfig().inAddress);

		// dynamic typed config usage
		int test2 = verticleConfigStrategy.getUntypedConfig().getInteger("a");
		logger.debug(verticleConfigStrategy.getUntypedConfig().getString("inAddress"));

		// want to use default config instead? No problem
		int test3 = verticleConfigStrategy.getDefaultConfig().a;
		logger.debug(verticleConfigStrategy.getDefaultConfig().inAddress);

		// or in a generic format?
		int test4 = verticleConfigStrategy.getUntypedDefaultConfig().getInteger("a");
		logger.debug(verticleConfigStrategy.getUntypedDefaultConfig().getString("inAddress"));

		// the default config can be updated at any time
        verticleConfigStrategy.setDefaultConfig(new Configuration("hey", "out-here", 84), type);

		register(verticleConfigStrategy.getConfig().inAddress, body -> {
			logger.info("Received message: {}", body);
			publish(verticleConfigStrategy.getConfig().outAddress, body);
		}, SimpleMessage.class);

		register(verticleConfigStrategy.getConfig().inAddress, this::handleStuff, SimpleMessage.class);

		localMap("bla").put("piep", "piep");

		remoteMap("blub").onComplete(res -> {
			res.result().put("piep", "piep");
		});

		defaultLocalMap().put("my-property", "my-value");
	}

	private void handleStuff(SimpleMessage body, Message<Object> raw) {
		logger.info("Received message: {}", body.title() + verticleConfigStrategy.getConfig().a);

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
