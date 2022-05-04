/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package creator;

import domain.Account;
import domain.Customer;

/**
 *
 * @author raymondhua
 */
public class CustomerCreator {
        public Customer add(Account account){
        Customer customer = new Customer();
        customer.setFirstName(account.getFirstName());
        customer.setLastName(account.getLastName());
        customer.setCustomerCode(account.getUsername());
        customer.setEmail(account.getEmail());
        customer.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");
        return customer;
    }
}
