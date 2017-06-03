package records;

public class Record {
	private String recordID;
	private Teacher TRecord;
	private Student SRecord;
	
	public Record(String recordID, Teacher TRecord){
		this.recordID = recordID;
		this.TRecord = TRecord;		
	}
	
	public Record(String recordID, Student SRecord){
		this.recordID = recordID;
		this.SRecord = SRecord;		
	}
	
	@Override
	public String toString(){
		String str = recordID.substring(0, 2);
		if(str.equalsIgnoreCase("TR")){
			str = "TeacherRecord: " + getRecordID() + "\n" + TRecord.toString();
		} else if(str.equalsIgnoreCase("SR")){
			str = "StudentRecord: " + getRecordID() + "\n" + SRecord.toString();
		} else{
			str = null;
			System.out.println(" Attention: There is a wrong record.");
		}		
		return str;	
	}
	
	public String getRecordID() {
		return recordID;
	}

	public void setRecordID(String recordID) {
		this.recordID = recordID;
	}

	public Teacher getTRecord() {
		return TRecord;
	}

	public void setTRecord(Teacher tRecord) {
		TRecord = tRecord;
	}

	public Student getSRecord() {
		return SRecord;
	}

	public void setSRecord(Student sRecord) {
		SRecord = sRecord;
	}
	
}
