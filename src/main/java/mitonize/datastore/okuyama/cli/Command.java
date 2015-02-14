package mitonize.datastore.okuyama.cli;

import java.io.IOException;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;

public abstract class Command {
	protected ConsoleReader console;
	protected OkuyamaClientCli cli;

	public Command(ConsoleReader console, OkuyamaClientCli cli) {
		this.console = console;
		this.cli = cli;
	}

	public abstract String command();
	public abstract void execute(String ... tokens) throws IOException;

	public Completer completer() {
		return null;
	};
}
