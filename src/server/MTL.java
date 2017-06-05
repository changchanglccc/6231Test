package server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import client.Manager;
import csInterface.ClientCalls;
import records.*;

/**
 * store Records(use Hashmap)
 * @author chongli
 *
 */
public class MTL extends ServerConfig implements ClientCalls{
	static int LOCAL_PORT = 2047;
	static String SERVER_NAME = "SERVER_MTL";
	static int START = 10000;
	static String RECORD_ID = null;
	static int UDP_BUFFER_SIZE = 256;

	static String ManagerID = null;

	static String LOG_DIR = "/Users/chongli/logs/serverLog/";
//	static String LOG_DIR = "G:/workspace/Logs/serverLog/";

	//store some datas in this server
	static Map<Character, ArrayList<Record>> HASHMAP_MTL = new HashMap<Character, ArrayList<Record>>(){
		{
			put('A', new ArrayList<Record>(Arrays.asList(new Record("TR00001", new Teacher("Anna", "An", "Montreal", "438000111", "Professor", "mtl")))));
			put('B', new ArrayList<Record>(Arrays.asList(new Record("TR00002", new Teacher("Bob", "Bi", "Montreal", "514000222", "Professor", "mtl")))));
			put('C', new ArrayList<Record>(Arrays.asList(new Record("SR00003", new Student("Frank", "Cao", "COMP6231", "active", "2017/03/31")))));
			put('D', new ArrayList<Record>(Arrays.asList(new Record("SR00004", new Student("Dong", "Ding", "COMP6231", "active", "2017/01/28")))));
			put('H', new ArrayList<Record>(Arrays.asList(new Record("SR00005", new Student("Frank", "Huang", "COMP6651", "active", "2016/08/10")))));

		}
	};

	static ArrayList<String> MANAGERLIST = new ArrayList<String>(){
		{
			add("MTL1111");
			add("MTL1112");
			add("MTL1113");
		}
	};

	public MTL(){
		super();
	}

	public static void main(String[] args){
		initLogger(MTL.SERVER_NAME);
		registerServer();
		startListenByUDP();
	}

	public static void registerServer() {
		try {
			String server_name = MTL.SERVER_NAME;
			ClientCalls obj = new MTL();
			ClientCalls stub = (ClientCalls) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.createRegistry(2964);
	        registry.bind(server_name, stub);
	        System.out.println("MTL server is running");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initial Logger.
	 * @param server_name
	 * Reference of logger:
	 * https://stackoverflow.com/questions/2533227/how-can-i-disable-the-default-console-handler-while-using-the-java-logging-api
	 * https://stackoverflow.com/questions/194765/how-do-i-get-java-logging-output-to-appear-on-a-single-line
	 */
	public static void initLogger(String server_name){
		try {
			ServerConfig.LOGGER = Logger.getLogger(Manager.class.getName());
			ServerConfig.LOGGER.setUseParentHandlers(false);
			ServerConfig.file = new FileHandler(LOG_DIR + server_name+".log",true);
			ServerConfig.LOGGER.addHandler(ServerConfig.file);
			SimpleFormatter formatter = new SimpleFormatter();
			ServerConfig.file.setFormatter(formatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String createTRecord(
			String firstName, String lastName,
			String address, String phone,
			String specialization,
			String location) throws RemoteException {
		//check format
		if(!checkLocation(location)){
			return " There are three locations: mtl, lvl and ddo, please choose one of them...";
		}

		ArrayList<Record> recordList = null;
		Character key = lastName.charAt(0);

		if(HASHMAP_MTL.containsKey(key)){
			recordList = HASHMAP_MTL.get(key);
		}else{
			recordList = new ArrayList<Record>();
		}

		//create a TRecord
		Teacher Teacher = new Teacher(firstName, lastName, address, phone, specialization, location);
		RECORD_ID = "TR" + getSTART() ;
		Record TRecord = new Record(RECORD_ID, Teacher);
		recordList.add(TRecord);
		synchronized(this){
			HASHMAP_MTL.put(key, recordList);
		}
		ServerConfig.LOGGER.info("Manager: "+ MTL.ManagerID + " creats teacher record: "+ "\n" +TRecord.toString());
		return " You have create a TRecord ：" + TRecord ;
	}

	public synchronized int getSTART(){
		START ++;
		return START;
	}

	@Override
	public String createSRecord(
			String firstName,
			String lastName,
			String courseRegistered,
			String status,
			String statusDate)
					throws RemoteException {
		//check format
		if(!checkCourseRegistered(courseRegistered)){
			return " Course is wrong! There are two courses to be registered: COMP6231 and COMP6651. ";
		}
		if(!checkStatus(status)){
			return " Status is wrong! There are two status : active and not_active. ";
		}
		if(!checkStatusDate(statusDate)){   //has doubt about it---> when run the code and check!
			return " The format of statusDate is worong! The correct format is 'yyyy/mm/dd'. ";
		}

		ArrayList<Record> recordList = null;
		Character key = lastName.charAt(0);

		if(HASHMAP_MTL.containsKey(key)){
			recordList = HASHMAP_MTL.get(key);
		}else{
			recordList = new ArrayList<Record>();
		}

		//create a SRecord
		Student student = new Student(firstName, lastName, courseRegistered, status, statusDate);
		RECORD_ID = "SR" + getSTART() ;
		Record SRecord = new Record(RECORD_ID, student);
		recordList.add(SRecord);
		synchronized(this){
			HASHMAP_MTL.put(key, recordList);
		}
		ServerConfig.LOGGER.info("Manager: "+ MTL.ManagerID + " creats student record: "+ "\n" +SRecord.toString());
		return " You have create a SRecord ：" + SRecord ;
	}

	@Override
	public String getRecordCounts() throws RemoteException {
		String lvlSize = getRecSzFromRemoteServer(ServerConfig.getLVL_PORT());
		String ddoSize = getRecSzFromRemoteServer(ServerConfig.getDDO_PORT());
		int mtlSize = checkRecordSize();
		String result = "MTL: " + mtlSize + ", LVL: " + lvlSize + ", DDO: " + ddoSize ;
		ServerConfig.LOGGER.info("Manager: "+ MTL.ManagerID + " search RecordCounts: "+ "\n" + result);
		return result;
	}

	@Override
	public String editRecord(String recordID, String fieldName, String newValue) throws RemoteException {
		for(Map.Entry<Character, ArrayList<Record>> entry : MTL.HASHMAP_MTL.entrySet()){
			for(Record record: entry.getValue()){
				if(recordID.equals(record.getRecordID())){
					if(recordID.substring(0, 2).equalsIgnoreCase("TR")){
						if(fieldName.equalsIgnoreCase("address")){
							synchronized(this){
								record.getTRecord().setAddress(newValue);
							}
							ServerConfig.LOGGER.info("Manager: "+ MTL.ManagerID + " edit the address of teacher record: "+ "\n" + record.toString());
							return "Successfully edit : " + record.toString();
						}else if(fieldName.equalsIgnoreCase("phone")){
							synchronized(this){
								record.getTRecord().setPhone(newValue);
							}
							ServerConfig.LOGGER.info("Manager: "+ MTL.ManagerID + " edit the phone of teacher record: "+ "\n" + record.toString());
							return "Successfully edit : " + record.toString();
						}else if(fieldName.equalsIgnoreCase("location")){
							if(!checkLocation(newValue)){
								return "Location is wrong, there are three locations : mtl, lvl and ddo.  ";
							}
							synchronized(this){
								record.getTRecord().setLocation(newValue);
							}
							ServerConfig.LOGGER.info("Manager: "+ MTL.ManagerID + " edit the location of teacher record: "+ "\n" + record.toString());
							return "Successfully edit : " + record.toString();
						}

					}else if(recordID.substring(0, 2).equalsIgnoreCase("SR")){
						if(fieldName.equalsIgnoreCase("courseRegistered")){
							if(!checkCourseRegistered(newValue)){
								return "Registered Course is wrong, there are two courses: COMP6231 and COMP6651.  ";
							}
							synchronized(this){
								record.getSRecord().setCourseRegistered(newValue);
							}
							ServerConfig.LOGGER.info("Manager: "+ MTL.ManagerID + " edit the courseRegistered of teacher record: "+ "\n" + record.toString());
							return "Successfully edit : " + record.toString();
						}else if(fieldName.equalsIgnoreCase("status")){
							if(!checkStatus(newValue)){
								return "Status is wrong, there are two status: active and not_active.  ";
							}
							synchronized(this){
								record.getSRecord().setStatus(newValue);
							}
							ServerConfig.LOGGER.info("Manager: "+ MTL.ManagerID + " edit the status of student record: "+ "\n" + record.toString());
							return "Successfully edit : " + record.toString();
						}else if(fieldName.equalsIgnoreCase("statusDate")){
							if(!checkLocation(newValue)){
								return "statusDate is wrong, the format is 'yyyy/mm/dd'.  ";
							}
							synchronized(this){
								record.getSRecord().setStatusDate(newValue);
							}
							ServerConfig.LOGGER.info("Manager: "+ MTL.ManagerID + " edit the status date of student record: "+ "\n" + record.toString());
							return "Successfully edit : " + record.toString();
						}
					}
				}

			}
		}
		return null;
	}

	public static boolean checkLocation(String location){
		for(ServerConfig.S_location slocation :ServerConfig.S_location.values()){
			if(location.equalsIgnoreCase(slocation.toString())){
				return true;
			}
		}
		return false;
	}

	public static boolean checkCourseRegistered(String courseRegistered){
		for(ServerConfig.S_courseRegistered course: ServerConfig.S_courseRegistered.values()){
			if(courseRegistered.equalsIgnoreCase(course.toString())){
				return true;
			}
		}
		return false;
	}

	public static boolean checkStatus(String status){
		for(ServerConfig.S_status sStatus: ServerConfig.S_status.values()){
			if(status.equalsIgnoreCase(sStatus.toString())){
				return true;
			}
		}
		return false;
	}

	/**
	 * check the date format
	 * @param date
	 * @return
	 */
	public static boolean checkStatusDate(String date){
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/mm/dd");
		simpleDate.setLenient(false);
		try {
			simpleDate.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static int checkRecordSize() {
		int size = 0;
		for (Map.Entry<Character, ArrayList<Record>> entry : MTL.HASHMAP_MTL.entrySet()) {
			size += entry.getValue().size();
		}
		return size;
	}

	/*
	 * the part below deals with the thread for communications between servers (UDP)
	 * Ref: https://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html
	 */

	// reply to packets(requests) from other server comes in (to check the record count)
	public static void startListenByUDP() {
		DatagramSocket connection = null;
		try {
			connection = new DatagramSocket(LOCAL_PORT);
			while(true){
				byte[] buf = new byte[UDP_BUFFER_SIZE]; //a buffer used to create a DatagramPacket
				// packet is used to receive a datagram from the socket
				DatagramPacket packet = new DatagramPacket(buf, UDP_BUFFER_SIZE);
				connection.receive(packet); // waits forever until a packet is received
				new UDPListener(connection, packet);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static class UDPListener extends Thread {

		DatagramSocket connection = null;
		DatagramPacket packet = null;
		String res = null;

		public UDPListener() {
			this("no arguments");
		}

		public UDPListener(String s) {
			System.out.println(s);
		}

		public UDPListener(DatagramSocket connection, DatagramPacket packet) {
			this.connection = connection;
			this.packet = packet;
			String reqPrefix = new String(packet.getData()).trim().substring(0, 4);
			switch (reqPrefix) {
			case "7395":
				ServerConfig.LOGGER.info("Request code: " + reqPrefix + ", " + "Check ManagerID: " + (new String(packet.getData()).trim().substring(4)+ " valid or not."));
				res = checkManagerID(new String(packet.getData()).trim().substring(4));
				break;
			case "6354":
				ServerConfig.LOGGER.info("Request code: " + reqPrefix + ", " + "Search HashMap, SearchType: " + (new String(packet.getData()).trim().substring(4)));
				res = checkRecordSize() + "";
				break;
			}
//			res = getRecSzStat(); // TODO
			this.start();
		}

		/**
		 * Check ManagerID.
		 * @param managerID
		 * @return
		 */
		public static String checkManagerID(String managerID){
			for(String account: MTL.MANAGERLIST){
				if(account.equalsIgnoreCase(managerID)){
					MTL.ManagerID = managerID;
					return "valid";
				}
			}
			return "invalid";
		}

		public void run() {
			DatagramPacket reply = new DatagramPacket(
					res.getBytes(),
					res.getBytes().length,
					packet.getAddress(),
					packet.getPort()
					);
			try {
				connection.send(reply);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * send request to remote server, and get other server's hashmap size back
	 * @param port
	 * @return
	 */
	public static String getRecSzFromRemoteServer(int port) {
		DatagramSocket connection = null;
		String ip = "127.0.0.1";
		String reqPrefix = "6354"; // prefix code 6354 means asking the record size
		int portNbr = port;

		try {
			connection = new DatagramSocket();

			// send request to remote server
			byte[] msg = (new String(reqPrefix)).getBytes();
			InetAddress host = InetAddress.getByName(ip);
			DatagramPacket request = new DatagramPacket(
					msg,
					msg.length,
					host,
					portNbr);
			connection.send(request);

			// get result from the response from remote server
			byte[] buf = new byte[UDP_BUFFER_SIZE];
			DatagramPacket reply = new DatagramPacket(buf, UDP_BUFFER_SIZE);
			connection.receive(reply);
			String res = new String(reply.getData()).trim();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
		return null;
	}







}
