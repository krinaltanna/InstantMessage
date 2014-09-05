package server;


import server.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class Server2
{
	
    public static void main(String args[])
    {
    	try
    	{
    		System.out.println("Enter the port number of Server");
        	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        	int port = Integer.parseInt(br.readLine());
        	Server2 server = new Server2( port );
        	server.startServer();
    		
    	}
    	catch(Exception e)
    	{
    		System.out.println(e);
    	}
	
    }

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int numberOfConnections = 0;
    int port;
	
    public Server2( int port )
    {
	this.port = port;
    }

    public void stopServer() {
	System.out.println( "Server cleaning up." );
	System.exit(0);
    }

    public void startServer()
    {
    	try 
    	{
        	serverSocket = new ServerSocket(port);
        }
        catch (IOException e) 
    	{
	    System.out.println(e);
        }   
	
	System.out.println( "Server is started and is waiting for connections." );
	System.out.println( "Any client can send -1 to stop the server." );

	
	while (true) 
	{
	    try 
	    {
	    	
	    	clientSocket = serverSocket.accept();
	    	numberOfConnections ++;
	    	
	    	
	    	Server2Connection connection = new Server2Connection(clientSocket, numberOfConnections, this);
	    	new Thread(connection).start();
	    }   
	    catch (Exception e)
	    {
	    	System.out.println(e);
	    }
	}
    }
}

class Server2Connection implements Runnable
{
	 static String userName;
	 static String password;
	static server.ClientToClient ctoc = new server.ClientToClient();
    static DataInputStream inputStream;
    static DataOutputStream outputStream;
    Socket clientSocket;
    int id;
    Server2 server;
    InetAddress Client_IP;
    int Client_port;
    public Server2Connection(Socket clientSocket, int id, Server2 server) {
	this.clientSocket = clientSocket;	
	this.id = id;
	this.server = server;	
	System.out.println( "Connection " + id + " established with: " + clientSocket );
	try
	{
		inputStream = new DataInputStream(clientSocket.getInputStream());
		outputStream = new DataOutputStream(clientSocket.getOutputStream());
	   // outputStream = new PrintStream(clientSocket.getOutputStream());
	} 
	catch (Exception e) 
	{
	    System.out.println(e);
	}
	
    }
    public void run() 
    {
        String line;
	try 
	{
	    boolean serverStop = false;

            while (true)
            {
            	final String LOGIN = "login";
                final String LIST = "list";
                final String SEND = "send";
            	//clientSocket.
            	Client_IP = clientSocket.getInetAddress();
            	Client_port = clientSocket.getPort();
                line = inputStream.readUTF();
                
                //String[] words = new String[16];
                String[] words = line.toString().split(" ");
               // System.out.print(words);
            	String first = words[0];
                //System.out.println(first);
            	System.out.println(first.equals(LOGIN));
               // System.out.println( "Received " + line + " from Connection " + id + ", IPAdreess" + Client_IP + "Port Number" + Client_port );
               // int n = Integer.parseInt(line);
                
               
                switch(first)
                {
                case("-1"):
                {
                	serverStop = true;
                	break;                	
                }
                	
                case("0"):
                	break;
                case(LOGIN):
                {
                	//outputStream.println(first);
                	//outputStream.println(first.equals(LOGIN));
                	String serverReply = Server2Connection.validateUser(id,line, clientSocket);
            		outputStream.write(serverReply.getBytes() ); 
            		//outputStream.flush();
            		break;
                }
                case(LIST):
                {
                	try
                	{
                		//outputStream.println(first);
                		//outputStream.println(first.equals(LIST));
                		ArrayList<String> serverReply = Server2Connection.returnList();
                		outputStream.writeUTF(serverReply.toString());
                		//outputStream.flush();
                		
                		break;
                	}
                	catch(NullPointerException e)
                	{
                		//outputStream.println(first);
                		outputStream.writeUTF("There are no other users online");
                		//outputStream.flush();
                		break;
                	}
                		
                }  
                
                	
               case(SEND):
                {
                
                	try
                	{
                		//String[] words = line.split(" ");
                        //String first = words[0];
                    	String userName = words[1];
                    	//have to append rest of the message
                    	//String message = words[2];
                		//outputStream.println(first);
                		//outputStream.println(first.equals(SEND));
                		Socket destinationSocket = Server2Connection.ctoc.returnUser(userName, clientSocket);  
                		outputStream.writeUTF(destinationSocket.getPort() + " " + destinationSocket.getInetAddress());
                		//outputStream.flush();
                		//outputStream.println(destinationSocket.getPort() + "\t" +destinationSocket.getInetAddress().toString());
                		break;
                		
                	}
                	catch(Exception e)
                	{
                		//outputStream.println(first);
                		//outputStream.println(first.equals(SEND));
                		outputStream.writeUTF("Unable to connect:" + userName);
                		outputStream.flush();
                		break;
                	}
                	
                	
                
                }
               default:
            	   outputStream.writeUTF("Invalid commannd");
            	   outputStream.flush();
            	   
                
                }
                
		if ( line.equals("-1 ") ) 
		{
		    serverStop = true;
		    break;
		}
		if ( line.equals("0") ) break;
		
            }

	    System.out.println( "Connection " + id + " closed." );
	    inputStream.close();
	    outputStream.close();
            clientSocket.close();
	    if ( serverStop ) server.stopServer();
	} catch (IOException e)
	{
	    System.out.println(e);	    
	}
    }
    @SuppressWarnings("deprecation")
	public static String validateUser(int id, String clientRequest, Socket clientSocket)
	{
    	Hashtable<String,String> ht = new Hashtable<String,String>();
    	ht.put("user1", "password1");
    	ht.put("user2", "password2");
    	ht.put("user3", "password3");
    	ht.put("user4", "password4");
    	ht.put("user5", "password5");
    	  	

    	try
    	{
    		
    		
        	//prevents one client to login twice
        	//System.out.println(Server2Connection.ctoc.clientInfo.containsKey(id));
        	if(Server2Connection.ctoc.activeClients.contains(clientSocket))
        		return "You are already logged in as:" + Server2Connection.ctoc.clientInfo.get(id) ;
        	else
        	{
        		outputStream.writeUTF("Enter your username");
            	userName = inputStream.readUTF().toString();
        		outputStream.writeUTF("Enter your password");
            	password = inputStream.readUTF().toString();
            	if (ht.containsKey(userName.toLowerCase()) && ht.get(userName).equals(password))
            	{
            		Server2Connection.ctoc.addClient(id,userName, clientSocket);
            		return "Login successful";        		
            	}
            	else
            	{
            		return "Invalid credentials";
            	}
        	}
        	
    	}
    	catch (Exception e)
    	{
    		return e.toString();
    	}   	
    	    	
	}
    public static ArrayList<String> returnList()
    {
    	//remove the own user name from active list
    	if(Server2Connection.ctoc.activeClients.containsKey(userName))
    	return Server2Connection.ctoc.returnList();
    	else
    		return null;
    }
}
