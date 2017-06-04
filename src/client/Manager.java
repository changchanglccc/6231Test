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
	static String LOG_DIR = "/Users/m.ding/logs/clientLog/"; 
	
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
	
	public static void validateManager() {
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
	    	
	    	byte[] msg = (new String(reqPrefix + managerID)).getBytes();
	    	InetAddress Host = InetAddress.getByName(hostname);
	    	DatagramPacket request = new DatagramPacket(msg, msg.length, Host, serverPort);
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
	    	e.printStackTrace();
	    } finally {
			if(connection != null)
				connection.close();
		}
	    
		return null; 
	}
	
	public static void createLogger(String managerID){
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
	
	// TODO: modify
	public static void main(String[] args){
		validateManager();
		while(!checkServerInfo(ManagerID)){
			System.err.println("Wrong ManagerID!\n");
			validateManager();
		};
		createLogger(ManagerID);
		LOGGER.info("ManagerID: "+ ManagerID + " logs in.");
		
		try {
			STUB = fetchServerInfo(ManagerID);
			int userInput=0;
			Scanner keyboard = new Scanner(System.in);
			showMenu(ManagerID);
			
			while(true)
			{
				Boolean valid = false;
				while(!valid)
				{
					try{
						userInput=keyboard.nextInt();
						valid=true;
					}
					catch(Exception e)
					{
						System.out.println("Invalid Input, please enter an Integer");
						valid=false;
						keyboard.nextLine();
					}
				}
				// Manage user selection.
				switch(userInput){
				case 1:
					System.out.println("First Name: ");
					String firstName = keyboard.next();
					System.out.println("Last Name: ");
					String lastName = keyboard.next();
					System.out.println("Address: ");
					String address = keyboard.next();
					System.out.println("Phone Number: ");
					String phoneNumber = keyboard.next();
					System.out.println("Specialization: ");
					String specialization = keyboard.next();
					System.out.println("Location(MTL/LVL/DDO): ");
					String location= keyboard.next();
					
					String D_result = STUB.createTRecord(firstName, lastName, address, phoneNumber, specialization, location);
					LOGGER.info("Manager Creat Doctor Record Succeed!" + "\n" + D_result);
					System.out.println(D_result);
					
					showMenu(ManagerID);
				break;
				
				case 2:
					System.out.println("Please input the FirstName");
					String firstname = keyboard.next();
					System.out.println("Please input the LastName");
					String lastname = keyboard.next();
					System.out.println("Please input the Designation(junior/senior)");
					String designation = keyboard.next();
					System.out.println("Please input the Status(active/terminated)");
					String status = keyboard.next();
					System.out.println("Please input the Status Date(yyyy/mm/dd/)");
					String statusDate = keyboard.next();
					
					String N_result = STUB.createSRecord(firstname, lastname, designation, status, statusDate);
					LOGGER.info("Record created successfully for Nurse. "+ lastname + "/n" + N_result);
					System.out.println(N_result);
					
					showMenu(ManagerID);
				break;
				
				case 3:
					System.out.println("Please input search type");
					System.out.println("Type in 'DR' for getting doctor record of each clinic");
					System.out.println("Type in 'NR' for getting nurse record of each clinic");
					System.out.println("Type in 'ALL' for getting total record of each clinic");
					String searchtype = keyboard.next();
					String S_result = STUB.getRecordCounts();
					
					LOGGER.info("Get Record Counts: " + "\n" + S_result);
					System.out.println(S_result);
					
					showMenu(ManagerID);
				break;
				
				case 4:
					System.out.println("Please input the RecordID");
					String recordID = keyboard.next();
					System.out.println("Please input the FieldName");
					String fieldname = keyboard.next();
					System.out.println("Please input the New Value");
					String newvalue = keyboard.next();
					String E_result = STUB.editRecord(recordID, fieldname, newvalue);
					
					LOGGER.info("Manager Edit Record Succeed!" + "\n" + E_result);
					System.out.println(E_result);
					
					showMenu(ManagerID);
				break;
				case 5:
					System.out.println("Exits.");
					LOGGER.info("Manager exits");
					keyboard.close();
					System.exit(0);
				default:
					System.out.println("Invalid, please try again.");
				}
			}
			
		}catch(Exception err) {
			err.printStackTrace();
		}
	}
}
