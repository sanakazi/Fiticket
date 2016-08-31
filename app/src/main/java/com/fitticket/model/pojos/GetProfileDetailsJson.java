package com.fitticket.model.pojos;

public class GetProfileDetailsJson {

 private GetProfileDetailsResultClass GetProfileDetailsResult;

public GetProfileDetailsResultClass getGetProfileDetailsResult() {
	return GetProfileDetailsResult;
}

public void setGetProfileDetailsResult(
		GetProfileDetailsResultClass getProfileDetailsResult) {
	GetProfileDetailsResult = getProfileDetailsResult;
}

public class GetProfileDetailsResultClass
 {
   private String contactNo;
   private String  emailId;
   private String firstName;
   private String lastName;
   private String packageEndDate;
   private String packageName;
   private String pincode;
public String getContactNo() {
	return contactNo;
}
public void setContactNo(String contactNo) {
	this.contactNo = contactNo;
}
public String getEmailId() {
	return emailId;
}
public void setEmailId(String emailId) {
	this.emailId = emailId;
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
public String getPackageEndDate() {
	return packageEndDate;
}
public void setPackageEndDate(String packageEndDate) {
	this.packageEndDate = packageEndDate;
}
public String getPackageName() {
	return packageName;
}
public void setPackageName(String packageName) {
	this.packageName = packageName;
}
public String getPincode() {
	return pincode;
}
public void setPincode(String pincode) {
	this.pincode = pincode;
}
   
	 
 }
}