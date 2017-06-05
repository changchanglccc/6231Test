package records;

import java.util.Date;

public class Student {
	private String firstName;
	private String lastName;
	private String courseRegistered;
    private String status;
    private String statusDate;
       
    public Student(String firstName, String lastName, String courseRegistered, String status, String statusDate){
    	this.firstName = firstName;
    	this.lastName = lastName;
    	this.courseRegistered = courseRegistered;
        this.status = status;
        this.statusDate = statusDate;
    }
    
    @Override
    public String toString(){
        String str = "First Name: "+ getFirstName() + "\n" 
				+ "Last Name: " + getLastName() + "\n"
				+ "Course Registed: " + getCourseRegistered() + "\n"
				+ "Status: " + getStatus() + "\n"
				+ "StatusDate: " + getStatusDate() + "\n";
		return str;
    }
    
    public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
    
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCourseRegistered() {
		return courseRegistered;
	}

	public void setCourseRegistered(String courseRegistered) {
		this.courseRegistered = courseRegistered;
	}

	public String getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

}
