package de.wuespace.telestion.examples;

import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.UntypedConfiguration;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.examples.messages.SimpleCommand;
import io.vertx.core.eventbus.Message;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author Pablo Klaschka (@pklaschka), Ludwig Richter (@fussel178)
 */
public class GenericSimpleCommandHandler extends TelestionVerticle<UntypedConfiguration> implements WithEventBus {
	@Override
	public void onStart() {
		// with "controller":
		register(getGenericConfig().getString("inAddress"), this::handleCommand, SimpleCommand.class);

		// inline:
		register(getGenericConfig().getString("pingAddress"),
				(SimpleCommand body, Message<Object> message) -> message.reply(
						new SimpleCommand("pong", new String[]{ /* Pure nothingness */ })
				),
				SimpleCommand.class
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
