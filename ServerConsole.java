import common.ChatIF;
import ocsf.server.AbstractServer;
import java.util.Scanner;

public class ServerConsole implements ChatIF {
	
	private EchoServer server;
	private Scanner scanner;
	
	public ServerConsole(int port) {
		server = new EchoServer(port, this);
		scanner = new Scanner(System.in);
	}
	
	public void display(String message) {
		System.out.println("> " + message);
	}
	
	  public void accept() 
	  {
		try {
			server.listen();
		}
		catch(Exception e) {
			System.out.println("Another process is listening to the port");
			return;
		}
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = scanner.nextLine();
	        server.handleMessageFromUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }

	public static void main(String[] args) {
		ServerConsole console = new ServerConsole(EchoServer.DEFAULT_PORT);
		console.accept();
	}

}
