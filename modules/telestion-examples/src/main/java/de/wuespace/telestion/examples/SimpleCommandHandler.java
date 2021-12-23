package de.wuespace.telestion.examples;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.examples.messages.SimpleCommand;
import io.vertx.core.eventbus.Message;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author Pablo Klaschka, Ludwig Richter
 */
public class SimpleCommandHandler
		extends TelestionVerticle<SimpleCommandHandler.Configuration> implements WithEventBus {
	public record Configuration(
			@JsonProperty String inAddress,
			@JsonProperty String pingAddress
	) implements TelestionConfiguration {
	}

	@Override
	public void onStart() {
		// with "controller":
		register(getConfig().inAddress(), this::handleCommand, SimpleCommand.class);

		// inline:
		register(getConfig().pingAddress(),
				(SimpleCommand body, Message<Object> message) ->
						message.reply(
								new SimpleCommand("pong", new String[]{ /* Empty feeling */})
						)
				, SimpleCommand.class
		);
	}

	private void handleCommand(SimpleCommand body, Message<Object> raw) {
		switch (body.command().toLowerCase(Locale.ROOT)) {
			case "print" -> logger.info("Printed arguments: {}", Arrays.toString(body.args()));
			case "exit" -> vertx.close();
			default -> raw.reply(
					new SimpleCommand("invalid_message", new String[]{body.command()})
			);
		}
	}
}
