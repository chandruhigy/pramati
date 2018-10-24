package org.chandru.assignement.datahandler;

import java.io.Serializable;


public class HotelsTO implements Serializable {

	private static final long serialVersionUID = 1L;
	// Fields
	private String hotelname;
	private String address;
	private String state;
	private String phone;
	private String fax;
	private String emailid;
	private String website;
	private String type;
	private String rooms;

	public String getHotelname() {
		return hotelname;
	}

	public void setHotelname(String hotelname) {
		this.hotelname = hotelname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmailid() {
		return emailid;
	}

	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRooms() {
		return rooms;
	}

	public void setRooms(String rooms) {
		this.rooms = rooms;
	}
	
	@Override
	public String toString() {
		return "hotelname :"+this.hotelname+" ,address :"+this.address+" ,state :"+this.state+" ,phone :"+this.phone+" ,fax :"
				+this.fax+" ,emailid :"+this.emailid+" ,website :"+this.website+" ,type :"+this.type+" ,rooms :"+this.rooms;
		
	}

}
