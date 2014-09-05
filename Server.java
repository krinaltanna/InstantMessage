import java.net.*;
import java.io.*;
public class Server {
	
		//Generate random port number
		private static int serverPortNumber = 10000;
	
	
	public void startServer() throws Exception
	{
		ServerSocket serverSocket = null;
		boolean listening = true;
		try 
		{
			//Generating scoket for server
			serverSocket = new ServerSocket(10006);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Sever port busy");
			System.exit(-1);
		}
		while(listening)
		{
			try
			{
				// Handeling client request
				new ClientConnectionRequestHandler(serverSocket.accept()).run();
			}
			catch(Exception e)
			{
				System.out.println("Client Connection failed");
				e.printStackTrace();
			}
		}
	}
	public class ClientConnectionRequestHandler implements Runnable
	{
		private Socket socket = null;
		

		public ClientConnectionRequestHandler(Socket socket) 
		{
			this.socket = socket;
			// TODO Auto-generated constructor stub
		}
		
		public void run()
		{	
			try{
		
			System.out.println("Client now connected to server:" + socket.toString());
			PrintWriter outputWiter = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			try {
				
				AuthenticateUser user = new AuthenticateUser();
				while((bufferedReader.readLine()) != null)
				{
					String serverReply = user.validateUser(bufferedReader.readLine());
					if(serverReply != null)
					{
						outputWiter.println(serverReply);
						
					}
					else
					{
						System.out.println("No output invoked from Server");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				outputWiter.close();
				bufferedReader.close();
				socket.close();
				
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		public  class AuthenticateUser
		{
			final String LoginUserName = "LoginUserName" ;
			final String LoginPassword = "LoginPassword";
			final String AuthenticateUser = "AuthenticateUser";
			final String AuthSuccess = "AuthSuccess";
			String state = LoginUserName;   
			String reply = null;
			String userName = null;
			String password = null;
			public String validateUser(String clientRequest)
			{
				
				if(clientRequest.equalsIgnoreCase("login"))
				{
					
					state = LoginUserName;
				}
				
				switch(state){
				case LoginUserName:
					reply = "Enter your username";
					state = LoginPassword;
					break;
				case LoginPassword:
					userName = clientRequest;
					state = AuthenticateUser;
					reply = "Enter your password";
				case AuthenticateUser:
					password = clientRequest;
					if(userName.equalsIgnoreCase("krinal") && password.equals("ashapuri"))
					{
						state = AuthSuccess;
					}
					else
					{
						System.out.println("Invalid credentials");
						state = LoginUserName;
					}
				case AuthSuccess:
					System.out.println("Login Successful");
					
					
				}
				return reply;
			}
			
			
			
		}
	}

	public static void main(String[] args)
	{
		try
		{
			Server ss = new Server();
			ss.startServer();
			// TODO Auto-generated method stub
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
