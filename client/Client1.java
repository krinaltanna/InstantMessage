package client;
import java.io.*;
import java.net.*;

import client.*;
public class Client1 {
	 String hostname = null;
	 int port;
	public Client1(String hostName, int port)
	{
		this.hostname = hostName;
		this.port = port;
	}
	
    public static void main(String[] args) {
  
    	Client1 client = new Client1("localhost", 6790);
    	client.createClient();
    }
    public  void createClient()
    {
    	{
    		
    		

    		// declaration section:
    		// clientSocket: our client socket
    		// os: output stream
    		// is: input stream
    		
    	        Socket clientSocket = null;  
    	       
    		
    		// Initialization section:
    		// Try to open a socket on the given port
    		// Try to open input and output streams
    		
    	        try {
    	            clientSocket = new Socket(hostname, port);
    	            ClientTalking client = new ClientTalking(clientSocket);
    	            new Thread(client).start();
    	            
    	           
    	        } catch (Exception e) {
    	            System.err.println(e);
    	        }
    
	
	// If everything has been initialized then we want to write some data
	// to the socket we have opened a connection to on the given port
	         
}
}
}
class ClientTalking implements Runnable
{
	Socket selfSocket;
	DataOutputStream os;
    DataInputStream is;
    //ObjectInputStream oi;

	public ClientTalking(Socket selfSocket) {
		this.selfSocket = selfSocket;
	 
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		try
		{
			System.out.println(selfSocket);
			os = new DataOutputStream(selfSocket.getOutputStream());
	      is = new DataInputStream(selfSocket.getInputStream());
	        //oi = new ObjectInputStream(selfSocket.getInputStream());
	        while ( true ) {
	    		//System.out.print( "Enter an integer (0 to stop connection, -1 to stop server): " );
	    		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    		String keyboardInput = br.readLine();
	    		System.out.println(keyboardInput);
	    		os.writeUTF( keyboardInput + "\n" );
	            String n = keyboardInput.toString();
	            String responseLine = is.readUTF();
	           
	            	
	            
	    		//int n = Integer.parseInt( keyboardInput );
	    		if ( n.equals("0") || n.equals("0") ) {
	    		    break;
	    		}
	    		if(n.contains("send"))
	    		{
	    			//ClientTalking ct1 = new ClientTalking((Socket)responseLine);
	    			System.out.println("Connected to:" + responseLine);
	    			//break;
	    		}
	    		
	    		
	    		
	    		
	    		System.out.println(responseLine);
	    	    }
	    	    
	    	    // clean up:
	    	    // close the output stream
	    	    // close the input stream
	    	    // close the socket
	    	    
	    	    os.close();
	    	    is.close();
	    	    selfSocket.close(); 
	        
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		 
	}
	
}

