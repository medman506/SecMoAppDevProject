package ims.fhj.at.emergencyalerter.model;


/**
 * Created by mayerhfl13 on 05.01.2017.
 */

public class Contact {

    private int id;
    private String name;
    private String telephoneNumber;

    public Contact(int id, String name, String telephoneNumber) {
        this.id = id;
        this.name = name;
        this.telephoneNumber = telephoneNumber;
    }

    public Contact(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
