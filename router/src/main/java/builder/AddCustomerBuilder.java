/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package builder;

import creator.CustomerCreator;
import domain.Account;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 *
 * @author raymondhua
 */
public class AddCustomerBuilder extends RouteBuilder {
   @Override
   public void configure()  { 
    from("jetty:http://localhost:9000/addCustomer?enableCORS=true")
        .setExchangePattern(ExchangePattern.InOnly)
        .convertBodyTo(String.class)
        .unmarshal().json(JsonLibrary.Gson, Account.class)
        .bean(CustomerCreator.class, "add(${body})")
        .log("Customer: ${body}")
        .to("jms:queue:create-customer");
 
   }

}
