package mitonize.datastore.okuyama.cli;

import java.io.IOException;

import jline.console.ConsoleReader;
import mitonize.datastore.OperationFailedException;
import mitonize.datastore.okuyama.OkuyamaClient;
import mitonize.datastore.okuyama.OkuyamaClientFactory;

public class Command_Remove extends Command {

	public Command_Remove(ConsoleReader console, OkuyamaClientCli cli) {
		super(console, cli);
	}

	@Override
	public String command() {
		return "remove";
	}

	@Override
	public void execute(String... tokens) throws IOException {
		if (tokens.length < 2) {
			console.println("usage: remove <key>");
			return;
		}
		
		OkuyamaClientFactory clientFactory = cli.getClientFactory();
		if (clientFactory == null) {
			console.println("not connected. use 'connect'");
		}
		try {
			OkuyamaClient client = clientFactory.createClient();
			Object value = client.removeObjectValue(tokens[1]);
			if (value == null) {
				console.println("[NOT FOUND]");
				return;
			}
			else {
				console.println("OK");							
			}
		} catch (OperationFailedException|IllegalArgumentException e) {
			console.println(e.getMessage());
		}
	}

}
