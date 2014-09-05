package server;
import server.*;

import java.io.*;
import java.net.*;
import java.util.*;


public class ClientToClient {
	//server.Server2 s = new server.Server2(6970);
	Hashtable<String, Socket> activeClients = new Hashtable<String,Socket>();
	Hashtable<Integer, String> clientInfo = new Hashtable<Integer, String>();
	
	public void addClient (int id, String userName, Socket clientSocket)
	{
		clientInfo.put(id, userName);
		activeClients.put(userName, clientSocket);
		System.out.println(activeClients != null);
	}
	public ArrayList<String> returnList()
	{
		ArrayList<String> listUserNames = new ArrayList<String>();
		Enumeration<String> userNames = activeClients.keys();
		while (userNames.hasMoreElements())
		{
			listUserNames.add(userNames.nextElement());
	      }
		return listUserNames;
		
	}
	public Socket returnUser(String destinationuserName, Socket initiator)
	{
		Socket destinationSocket = null;
		try
		{
			
		
		if (activeClients.containsValue(initiator) & activeClients.containsKey(destinationuserName))
		{
			destinationSocket = activeClients.get(destinationuserName);
				
								
			}
		}
		
		finally
		{
			return destinationSocket;
		}
	}
        
	}
