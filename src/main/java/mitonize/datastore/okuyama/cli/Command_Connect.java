package mitonize.datastore.okuyama.cli;

import java.io.IOException;
import java.net.UnknownHostException;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import mitonize.datastore.OperationFailedException;
import mitonize.datastore.okuyama.OkuyamaClient;
import mitonize.datastore.okuyama.OkuyamaClientFactory;
import mitonize.datastore.okuyama.OkuyamaClientFactoryImpl;

public class Command_Connect extends Command {

	public Command_Connect(ConsoleReader console, OkuyamaClientCli cli) {
		super(console, cli);
	}

	@Override
	public String command() {
		return "connect";
	}

	@Override
	public void execute(String... tokens) throws IOException {
		if (tokens.length < 2) {
			console.println("usage: connect <hostname>:<port>");
			return;
		}

		OkuyamaClientFactory clientFactory = cli.getClientFactory();
		if (clientFactory != null) {
			clientFactory.destroy();
			console.println("disconnected fron " + cli.getHost());
		}
		String host = tokens[1];
		clientFactory = new OkuyamaClientFactoryImpl(new String[]{host}, 1, false, false, null);
		cli.setClientFactory(clientFactory);
		cli.setHost(host);
		try {
			OkuyamaClient client = clientFactory.createClient();
			String versionStr = client.getMasterNodeVersion();
			console.println(versionStr);
		} catch (OperationFailedException | UnknownHostException e) {
			console.println(e.getMessage());
		}
	}

	@Override
	public Completer completer() {
		return null;
//		return new AggregateCompleter( //
//                  new ArgumentCompleter(new StringsCompleter("connect"), new StringsCompleter("aaa", "access-expression", "access-lists"), new NullCompleter()), //
//                  new ArgumentCompleter(new StringsCompleter("connect"), new StringsCompleter("ip"), new StringsCompleter("access-lists", "accounting", "admission", "aliases", "arp"), new NullCompleter()), //
//                  new ArgumentCompleter(new StringsCompleter("connect"), new StringsCompleter("ip"), new StringsCompleter("interface"), new StringsCompleter("eth0", "eth1"), new NullCompleter())
//                  );
	}
}
