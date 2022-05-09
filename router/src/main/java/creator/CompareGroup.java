/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package creator;

import domain.Customer;
import domain.Summary;

/**
 *
 * @author raymondhua
 */
public class CompareGroup {
    public Customer compare(Summary summary){
        Customer customer = new Customer();
        if(summary.getGroup().equals("Regular Customers")){
            
            customer.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");
        }
        else{
            customer.setGroup("0afa8de1-147c-11e8-edec-201e0f00872c");
        }
        return customer;
    }
    public Customer compare(String id, String firstName, String lastName, String email, String userName, Summary summary){
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setCustomerCode(userName);
        if(summary.getGroup().equals("Regular Customers")){
            customer.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");
        }
        else{
            customer.setGroup("0afa8de1-147c-11e8-edec-201e0f00872c");
        }
        return customer;
    }
}

