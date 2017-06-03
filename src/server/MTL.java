package server;
import java.util.*;
import records.*;

/**
 * store Records(use Hashmap)
 * @author chongli
 *
 */
public class MTL extends Server_Configuration{
	static int LOCAL_PORT = 1001;
			
			
	//store some datas in this server
	static Map<Character, ArrayList<Record>> HashMap_MTL = new HashMap<Character, ArrayList<Record>>(){
		{
			put('A', new ArrayList<Record>(Arrays.asList(new Record("TR00001", new Teacher("Anna", "An", "Montreal", "438000111", "Professor", "mtl")))));
			put('B', new ArrayList<Record>(Arrays.asList(new Record("TR00002", new Teacher("Bob", "Bi", "Montreal", "514000222", "Professor", "mtl")))));
			put('C', new ArrayList<Record>(Arrays.asList(new Record("SR00001", new Student("Frank", "Cao", "COMP6231", "active", "2017/03/31")))));
			put('D', new ArrayList<Record>(Arrays.asList(new Record("SR00002", new Student("Dong", "Ding", "COMP6231", "active", "2017/01/28")))));
			put('H', new ArrayList<Record>(Arrays.asList(new Record("SR00003", new Student("Frank", "Huang", "COMP6651", "active", "2016/08/10")))));
			
		}
	};
	public MTL(){
		super();
	}
	
	public static void main(String[] args){
		
		
	}
}
