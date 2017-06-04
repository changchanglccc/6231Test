package server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import myInterface.MyInterface;
import records.*;

/**
 * store Records(use Hashmap)
 * @author chongli
 *
 */
public class MTL extends Server_Configuration implements MyInterface{
	static int LOCAL_PORT = 1001;
	static String SERVER_NAME = "SERVER_MTL";
	static int START = 10000;
	static String RECORD_ID = null;
	static int UDP_BUFFER_SIZE = 256;
	
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
			MANAGERLIST.add("MTL1111");
			MANAGERLIST.add("MTL1112");
			MANAGERLIST.add("MTL1113");
		}	
	};
	
	public MTL(){
		super();
	}
	
	public static void main(String[] args){
		
		
	}

	@Override
	public String createTRecord(String firstName, String lastName, String address, String phone, String specialization,
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
		//need to add log
		return " You have create a TRecord ：" + TRecord ;
	}
	
	public synchronized int getSTART(){
		START ++;
		return START;
	}

	@Override
	public String createSRecord(String firstName, String lastName, String courseRegistered, String status,
			String statusDate) throws RemoteException {
		//check format
		if(!checkLocation(courseRegistered)){
			return " Course is wrong! There are two courses to be registered: COMP6231 and COMP6651. ";
		}
		if(!checkLocation(status)){
			return " Status is wrong! There are two status : active and not_active. ";
		}
		if(checkLocation(statusDate)){   //has doubt about it---> when run the code and check!
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
		//need to add log
		return " You have create a SRecord ：" + SRecord ;
	}

	@Override
	public String getRecordCounts() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String editRecord(int recordID, String fieldName, String newValue) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static boolean checkLocation(String location){
		for(Server_Configuration.S_location slocation :Server_Configuration.S_location.values()){
			if(location.equalsIgnoreCase(slocation.toString())){
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkCourseRegistered(String courseRegistered){
		for(Server_Configuration.S_courseRegistered course: Server_Configuration.S_courseRegistered.values()){
			if(courseRegistered.equalsIgnoreCase(course.toString())){
				return true;
			}
		}		
		return false;
	}
	
	public static boolean checkStatus(String status){
		for(Server_Configuration.S_status sStatus: Server_Configuration.S_status.values()){
			if(status.equalsIgnoreCase(sStatus.toString())){
				return true;
			}
		}		
		return false;
	}
	
	public static int checkRecordSize() {
		int size = 0;
		for (Map.Entry<Character, ArrayList<Record>> entry : MTL.HASHMAP_MTL.entrySet()) {
			size += entry.getValue().size();
		}
		return size;
	}
	
	public static String getRecSzStat() {
		// TODO: do we need this?
		return null;
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
			res = getRecSzStat(); // TODO
			this.start();
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
	
	// send request to remote server
	public static String getRecSzFromRemoteServer(int port) {
		DatagramSocket connection = null;
		String ip = "127.0.0.1";
		int portNbr = port;
		
		try {
			connection = new DatagramSocket();
			
			// send request to remote server
			byte[] msg = (new String("recordSize")).getBytes();
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
