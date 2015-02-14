package mitonize.datastore.okuyama.cli;

import java.io.IOException;

import jline.console.ConsoleReader;
import mitonize.datastore.OperationFailedException;
import mitonize.datastore.okuyama.OkuyamaClient;
import mitonize.datastore.okuyama.OkuyamaClientFactory;

public class Command_Set extends Command {

	public Command_Set(ConsoleReader console, OkuyamaClientCli cli) {
		super(console, cli);
	}

	@Override
	public String command() {
		return "set";
	}

	@Override
	public void execute(String... tokens) throws IOException {
		if (tokens.length < 3) {
			console.println("usage: set <key> <value>");
			return;
		}
		
		OkuyamaClientFactory clientFactory = cli.getClientFactory();
		if (clientFactory == null) {
			console.println("not connected. use 'connect'");
		}
		try {
			OkuyamaClient client = clientFactory.createClient();
			boolean resutl = client.setObjectValue(tokens[1], tokens[2], null, 0);
			console.println(resutl ? "OK": "NG");
		} catch (OperationFailedException|IllegalArgumentException e) {
			console.println(e.getMessage());
		}
	}

}
