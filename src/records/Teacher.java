package records;

import java.util.Date;

public class Teacher {
	private String firstName;
	private String lastName;
    private String address;
    private String phone;
    private String specialization;
    private String location;


    public Teacher(String firstName, String lastName, String address, String phone, String specialization, String location){
    	this.firstName = firstName;
    	this.lastName = lastName;
    	this.setAddress(address);
        this.setPhone(phone);
        this.setSpecialization(specialization);
        this.setLocation(location);
    }
    
    @Override
    public String toString(){
        String str = "First Name: "+ getFirstName() + "\n" 
				+ "Last Name: " + getLastName() + "\n"
				+ "Address: " + getAddress() + "\n"
				+ "Phone: " + getPhone() + "\n"
				+ "Specialization: " + getSpecialization() + "\n"
				+ "Location: " + getLocation() + "\n";
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
