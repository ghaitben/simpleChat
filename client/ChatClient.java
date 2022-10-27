// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private int loginId;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(int loginId, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginId = loginId;
    openConnection();
    sendToServer("#login " + String.valueOf(loginId));
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
	  clientUI.display(msg.toString());
  }
  
  private void commandHandler(String message) {
	  if(message.equals("#quit")) {
		  quit();
	  }
	  if(message.equals("#logoff")) {
		  if(!isConnected()) {
			  clientUI.display("You are already logged off");
			  return;
		  }
		  try {
			closeConnection();
		   } catch (IOException e) {
		   }
		  return;
	  }
	  if(message.equals("#login")) {
		  if(isConnected()) {
			  clientUI.display("You are already logged in");
			  return;
		  }
		  try {
			  openConnection();
		  } catch(Exception e) {
			  
		  }
		  return;
	  }
	  if(message.equals("#getport")) {
		 clientUI.display(String.valueOf(getPort()));
		 return;
	  }
	  if(message.equals("#gethost")) {
		  clientUI.display(getHost());
		  return;
	  }
	  
	  String[] split = message.split(" ");
	  if(split.length != 2) {
		  clientUI.display("Unknown command");
		  return;
	  }
	  
	  if(split[0].equals("#setport")) {
		  try {
			  int new_port = Integer.parseInt(split[1]);
			  setPort(new_port);
		  }
		  catch(NumberFormatException e) {
			  clientUI.display("Port must be an integer");
		  }
		  return;
	  }
	  if(split[0].equals("#sethost")) {
		  setHost(split[1]);
		  return;
	  }
	  clientUI.display("Unknown command");
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  // #login should pass but not #login .+
	  boolean server_login = message.startsWith("#login") && message.split(" ").length > 1;
	  if(message.charAt(0) == '#' && !server_login) {
		  commandHandler(message);
		  return;
	  }
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  protected void connectionClosed() {
	  clientUI.display("Exiting.");
  }
  
  protected void connectionException(Exception e) {
	  System.out.println("Server closed");
	  quit();
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
