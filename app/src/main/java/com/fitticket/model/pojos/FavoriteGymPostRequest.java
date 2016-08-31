
package com.fitticket.model.pojos;

import com.fitticket.model.pojos.base.WeBase;

public class FavoriteGymPostRequest extends WeBase {

    private int customerid;
    private String favstatus;
    private long gymid;
    
    
    public int getCustomerid() {
        return customerid;
    }
    public void setCustomerid(int customerid) {
        this.customerid = customerid;
    }
    public String getFavstatus() {
        return favstatus;
    }
    public void setFavstatus(String favstatus) {
        this.favstatus = favstatus;
    }
    public long getGymid() {
        return gymid;
    }
    public void setGymid(long gymid) {
        this.gymid = gymid;
    }
 
}
