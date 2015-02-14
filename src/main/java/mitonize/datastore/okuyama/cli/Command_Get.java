package mitonize.datastore.okuyama.cli;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import jline.console.ConsoleReader;
import mitonize.datastore.OperationFailedException;
import mitonize.datastore.okuyama.OkuyamaClient;
import mitonize.datastore.okuyama.OkuyamaClientFactory;

public class Command_Get extends Command {


	public Command_Get(ConsoleReader console, OkuyamaClientCli cli) {
		super(console, cli);
	}

	@Override
	public String command() {
		return "get";
	}

	@Override
	public void execute(String ... tokens) throws IOException {
		if (tokens.length < 2) {
			console.println("usage: get <key>");
			return;
		}

		OkuyamaClientFactory clientFactory = cli.getClientFactory();
		if (clientFactory == null) {
			console.println("not connected. use 'connect'");
		}
		try {
			OkuyamaClient client = clientFactory.createClient();
			Object value = client.getObjectValue(tokens[1]);
			if (value == null) {
				console.println("[NOT FOUND]");
				return;
			}
			if (value instanceof String) {
				String str = (String) value;
				console.println(str);							
			}
			else {
				try {
					@SuppressWarnings("unused")
					Method methodToString = value.getClass().getDeclaredMethod("toString");
					// toString() がオーバライド実装されているので toString() で文字列化する。
					console.println(value.toString());
				} catch (NoSuchMethodException|SecurityException e) {
					console.println("[Object]");
					console.println(new ReflectionToStringBuilder(value).toString());
				}
			}
		} catch (OperationFailedException|IllegalArgumentException e) {
			console.print(e.getMessage());
		}
	}
}
