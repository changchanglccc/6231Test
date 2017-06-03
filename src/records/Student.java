package records;

import java.util.Date;

public class Student {
	private String firstName;
	private String lastName;
	private String courseRegisted;
    private String status;
    private String statusDate;
       
    public Student(String firstName, String lastName, String courseRegisted, String status, String statusDate){
    	this.firstName = firstName;
    	this.lastName = lastName;
    	this.courseRegisted = courseRegisted;
        this.status = status;
        this.statusDate = statusDate;
    }
    
    @Override
    public String toString(){
        String str = "First Name: "+ getFirstName() + "\n" 
				+ "Last Name: " + getLastName() + "\n"
				+ "Course Registed: " + getCourseRegisted() + "\n"
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

	public String getCourseRegisted() {
		return courseRegisted;
	}

	public void setCourseRegisted(String courseRegisted) {
		this.courseRegisted = courseRegisted;
	}

	public String getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

}
