package mitonize.datastore.okuyama.cli;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import jline.console.history.FileHistory;
import mitonize.datastore.okuyama.OkuyamaClientFactory;
import mitonize.datastore.okuyama.OkuyamaClientFactoryImpl;

public class OkuyamaClientCli {
	public static void main(String[] args) throws IOException {
		String host = "localhost:8888";

		if (args.length >= 1) {
			host = args[0];
		}

		OkuyamaClientCli cli = new OkuyamaClientCli(host);
		try {
			cli.loop();
		} finally {
			try {
				TerminalFactory.get().restore();
			} catch (Exception e) {
			}
		}
	}
	
	Map<String, Command> commands;
	OkuyamaClientFactory clientFactory;
    ConsoleReader console;
	private String host;

    public OkuyamaClientCli(String host) throws IOException {
    	setHost(host);
		this.commands = new HashMap<String, Command>();

		try {
			String[] masternodes = new String[] { getHost() };
			this.clientFactory = new OkuyamaClientFactoryImpl(masternodes, 1, false, false, null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		FileHistory history = new FileHistory(new File(System.getProperty("user.home"), ".okuyama-cli_history"));
		
		console = new ConsoleReader();
		console.setHistory(history);
		console.setHistoryEnabled(true);

		addCommand(new Command_Connect(console, this));
		addCommand(new Command_Get(console, this));
		addCommand(new Command_Set(console, this));
		addCommand(new Command_Remove(console, this));

		List<String> commandList = new ArrayList<>();
		for (Command c: commands.values()) {
			commandList.add(c.command());			
		}
		commandList.add("exit");
		console.addCompleter(new StringsCompleter(commandList));

		for (Command c: commands.values()) {
			Completer completer = c.completer();
			if (completer != null) {
				console.addCompleter(completer);
			}
		}
	}
    
    private void addCommand(Command command) {
    	commands.put(command.command(), command);
    }

	public OkuyamaClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(OkuyamaClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	void loop() {
		boolean exitFlag = false;
		while (!exitFlag) {
			try {
				String[] line = nextCommand();
				if (line.length == 0 || line[0].length() == 0) {
					continue;
				}
				if (line[0].equals("exit")) {
					exitFlag = true;
				} else {
					Command c = commands.get(line[0]);
					if (c != null) {
						c.execute(line);
					}
				}
			} catch (IllegalArgumentException | IOException e) {
				try {
					console.println(e.getMessage());
				} catch (IOException e1) {
				}
			}
		}
		console.shutdown();
		if (clientFactory != null) {
			clientFactory.destroy();
		}
	}
	
	String[] nextCommand() throws IOException {
		console.setPrompt(getHost() + "> ");
		try {
			String line = console.readLine();
			if (line == null) {
				return new String[]{ "exit" };
			}
			return line.split("\\s+");
		} catch (NoSuchElementException e) {
			return new String[]{ "exit" };
		}
	}
}
