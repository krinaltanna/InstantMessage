import java.io.*;
import java.net.*;
public class Client
{
	public void startClient()
	{
			Socket socket = null;
			PrintWriter printWriter = null;
			BufferedReader bufferedReader = null; 
			BufferedReader userDataReader = null;
			InetAddress IP = null;
			// getting localhost
			try
			{
				IP = InetAddress.getLocalHost();
				socket = new Socket(IP.getHostName(), 10006);
				printWriter = new PrintWriter(socket.getOutputStream(), true);
				bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				userDataReader = new BufferedReader(new InputStreamReader(System.in));
				String serverData;
				String userData;
			
				//Communicaton with server
			
				while ((serverData = bufferedReader.readLine()) != null)
				{
					System.out.println(serverData);
					userData = userDataReader.readLine();
					System.out.println(userData);
				}
			}
			catch (Exception e)

			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					socket.close();
					printWriter.close();
					bufferedReader.close();
					userDataReader.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
			
		}	
	public static void main(String args[])
	{
		try
		{
			new Client().startClient();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
			
		
		
	


