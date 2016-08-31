
package com.fitticket.model.pojos;


import com.fitticket.model.pojos.base.WeBase;

public class EditUserProfile extends WeBase {
    String customerName;
    String contactNo;
    String password;
    String oldPassword;
    int isSocial;
    
    
    public String getContactNo() {
        return contactNo;
    }
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    public int getIsSocial() {
        return isSocial;
    }
    public void setIsSocial(int isSocial) {
        this.isSocial = isSocial;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
