/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package creator;

import domain.Customer;
import domain.Account;

/**
 *
 * @author raymondhua
 */
public class Converter {
        public Account convertToAccount(Customer customerFromVend){
        Account account = new Account();
        account.setFirstName(customerFromVend.getFirstName());
        account.setLastName(customerFromVend.getLastName());
        account.setUsername(customerFromVend.getCustomerCode());
        account.setEmail(customerFromVend.getEmail());
        account.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");
        account.setId(customerFromVend.getId());
        return account;
}
}
