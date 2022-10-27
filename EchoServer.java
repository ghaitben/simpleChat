// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }
  
  public EchoServer(int port, ChatIF serverUI) {
	  super(port);
	  this.serverUI = serverUI;
  }
  
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("Client connected !");
  }
  
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  System.out.println(client.getInfo("loginId") + " disconected!");
  }
  
  synchronized protected void clientException(ConnectionToClient client, Throwable e) {
	  try {
		  client.close();
	  }catch(Exception ee) {
		  
	  }
  }
  
  private void commandHandler(String message) {
	  if(message.equals("#quit")) {
		  try {
			close();
			System.exit(0);
		} catch (IOException e) {
		}
		  finally {
			  System.exit(0);
		  }
		 return;
	  }
	  if(message.equals("#stop")) {
		  stopListening();
		  return;
	  }
	  if(message.equals("#close")) {
		  try {
			close();
		  } catch (IOException e) {
		  }
		  finally {
		  }
		 return;
	  }
	  if(message.equals("#getport")) {
		  serverUI.display(String.valueOf(getPort()));
		  return;
	  }
	  if(message.equals("#start")) {
		  if(isListening()) {
			  serverUI.display("Server already listening");
			  return;
		  }
		  try {
			listen();
		  } catch (IOException e) {
		  }
		  return;
	  }
	  
	  String[] split = message.split(" ");
	  if(split.length != 2) {
		  serverUI.display("Unknown command");
		  return;
	  }
	  
	  if(split[0].equals("#setport")) {
		  if(isListening()) {
			  serverUI.display("You must stop listening to connections before changing the port number");
			  return;
		  }
		  try {
			  int new_port = Integer.parseInt(split[1]);
			  setPort(new_port);
		  }
		  catch(NumberFormatException e) {
			  serverUI.display("Port must be an integer");
		  }
		  return;
	  }
	  serverUI.display("Unknown command");
  }
  
  public void handleMessageFromUI(String message) {
	  if(message.charAt(0) == '#') {
		  commandHandler(message);
		  return;
	  }
	  serverUI.display(message);
	  this.sendToAllClients("SRV MESSAGE> " + message);
	  
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  System.out.println("Message received: " + msg + " from " + client.getInfo("loginId"));
	  
	  // this is very ugly, sorry :/.
	  if(!(msg instanceof String) || !(((String) msg).startsWith("#login"))) {
		  this.sendToAllClients("(id: " + client.getInfo("loginId") + ") " + msg);
		  return;
	  }
	  // message starting with #login
	  String[] split = ((String) msg).split(" ");
	  if(client.getInfo("loginId") != null) {
		  try {
			client.sendToClient("#login command is not allowed, disconnecting you now.");
		  	client.close();
		  }
		  catch(Exception ee) {}
		  return;
	  }
	  if(split[1].equals("-1")) {
		  try {
		  client.sendToClient("Error no loginId specified, Connection aborted");
		  client.close();
		  }
		  catch(Exception eee) {}
	  }
	  else {
		  int loginId = Integer.parseInt(split[1]);
		  client.setInfo("loginId", loginId);
		  System.out.println(loginId + " has logged on");
		  this.sendToAllClients(loginId + " has logged on");
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
