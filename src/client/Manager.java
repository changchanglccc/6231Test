package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csInterface.*;

public class Manager {
	
	static String ManagerID;
	static ClientCalls STUB;
	
	static int SERVER_PORT_MTL = 2047;
	static int SERVER_PORT_LVL = 2048;
	static int SERVER_PORT_DDO = 2049;
	
	static int BUFFER_SIZE = 256;
	
	static Logger LOGGER = null;
	static FileHandler FILE = null;
	static String LOG_DIR = "D:/logs/clientLog/"; 
	
	public static void showMenu(String managerID){

		System.out.println("------  Welcome to COMP6231-RMI Clinet side-------");
		System.out.println("--- Now the Manager is :" + managerID + "------");
		System.out.println();
		System.out.println("------ Here is 5 options for you ------");
		System.out.println("1. Create a Teacher Record.  ");
		System.out.println("2. Create a Student Record.  ");
		System.out.println("3. Get the count of Records.  ");
		System.out.println("4. Edit Record.  ");
		System.out.println("5. Exit.  ");	
	}
	
	public static ClientCalls fetchServerInfo(String managerID){
		try {
			if(managerID.substring(0, 3).equalsIgnoreCase("MTL")){
				Registry registry = LocateRegistry.getRegistry("127.0.0.1", 2964);
				return (ClientCalls) registry.lookup("server_mtl");
			}else if(managerID.substring(0, 3).equalsIgnoreCase("LVL")){
				Registry registry = LocateRegistry.getRegistry("127.0.0.1", 2964);
				return (ClientCalls) registry.lookup("server_lvl");
			}else if(managerID.substring(0, 3).equalsIgnoreCase("DDO")){
				Registry registry = LocateRegistry.getRegistry("127.0.0.1", 2964);
				return (ClientCalls) registry.lookup("server_ddo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public static void validManager() {
		Boolean valid = false;
		while (!valid) {
			Scanner in = new Scanner(System.in);
			do {
				System.out.println("****Please input the manager ID****");
				ManagerID = in.next();
			} while (!checkManagerIDFormat(ManagerID));
			valid = true;
		}
	}
	
	public static Boolean checkManagerIDFormat(String managerID){
		String pattern = "^(MTL|LVL|DDO)(\\d{4})$";
		Pattern re = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
		Matcher matcher = re.matcher(managerID);
		if(matcher.find()){
			return true;
		}else{
			System.err.println("Wrong format. Example: MTL1111\n");
			return false;
		}
	}
	
	public static Boolean checkServerInfo(String managerID){
		DatagramSocket connection = null;
		String hostname = "127.0.0.1";
		String reqPrefix = "7395"; // prefix 7395 means asking server to validate manager ID
		int serverPort = 0;
		
		if(ManagerID.substring(0, 3).equalsIgnoreCase("MTL")){
			serverPort = SERVER_PORT_MTL;
		}else if(ManagerID.substring(0, 3).equalsIgnoreCase("LVL")){
			serverPort = SERVER_PORT_LVL;
		}else if(ManagerID.substring(0, 3).equalsIgnoreCase("DDO")){
			serverPort = SERVER_PORT_DDO;
		}
		
	    try {
	    	connection = new DatagramSocket();
	    	
	    	byte[] message = (new String(reqPrefix + managerID)).getBytes();
	    	InetAddress Host = InetAddress.getByName(hostname);
	    	DatagramPacket request = new DatagramPacket(message, message.length, Host, serverPort);
	    	connection.send(request);
	    	
	    	byte[] buf = new byte[BUFFER_SIZE];
	    	DatagramPacket reply = new DatagramPacket(buf, BUFFER_SIZE); 
	    	connection.receive(reply);
	    	
	    	String result = new String(reply.getData()).trim();
	    	if(result.equals("valid")){
	    		System.out.println("Valid Account");
	    		return true;
	    	}else{
	    		System.out.println("Invalid Account");
	    		return false;
	    	}
	    } catch(Exception e) {
	    	System.out.println("Socket: " + e.getMessage()); 
	    } finally {
			if(connection != null)
				connection.close();
		}
	    
		return null; 
	}
	
	public static void initLogger(String managerID){
		try {
			LOGGER = Logger.getLogger(Manager.class.getName());
			LOGGER.setUseParentHandlers(false);
			FILE = new FileHandler(LOG_DIR + managerID + ".log", true);
			LOGGER.addHandler(FILE);
			SimpleFormatter formatter = new SimpleFormatter();
			FILE.setFormatter(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		
		
	}
}
