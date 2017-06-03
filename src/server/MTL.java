package server;
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
	
	//store some datas in this server
	static Map<Character, ArrayList<Record>> HASHMAP_MTL = new HashMap<Character, ArrayList<Record>>(){
		{
			put('A', new ArrayList<Record>(Arrays.asList(new Record("TR00001", new Teacher("Anna", "An", "Montreal", "438000111", "Professor", "mtl")))));
			put('B', new ArrayList<Record>(Arrays.asList(new Record("TR00002", new Teacher("Bob", "Bi", "Montreal", "514000222", "Professor", "mtl")))));
			put('C', new ArrayList<Record>(Arrays.asList(new Record("SR00001", new Student("Frank", "Cao", "COMP6231", "active", "2017/03/31")))));
			put('D', new ArrayList<Record>(Arrays.asList(new Record("SR00002", new Student("Dong", "Ding", "COMP6231", "active", "2017/01/28")))));
			put('H', new ArrayList<Record>(Arrays.asList(new Record("SR00003", new Student("Frank", "Huang", "COMP6651", "active", "2016/08/10")))));
			
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
		if(!checkLocation(location)){
			return " There are three locations: mtl, lvl and ddo, please choose one of them...";
		}
		
		ArrayList<String> eachRecord = null;
		Character key = lastName.charAt(0);
		if(HASHMAP_MTL.containsKey(key)){
//			eachRecord = HASHMAP_MTL.get(key);
		}
		
		
		
		return null;
	}

	@Override
	public String createSRecord(String firstName, String lastName, String courseRegistered, String status,
			Date statusData) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public int add(int x, int y) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
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
	
	
	
	
	
	
	
	
	
	
}
