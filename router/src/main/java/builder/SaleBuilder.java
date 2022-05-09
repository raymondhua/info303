/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package builder;

import domain.Customer;
import domain.Sale;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 *
 * @author raymondhua
 */
public class SaleBuilder extends RouteBuilder {
   @Override
   public void configure()  { 
       from("jms:queue:new-sale")
        .unmarshal().json(JsonLibrary.Gson, Sale.class)
        .multicast()
        .to("jms:queue:send-to-sale");
       
       from("jms:queue:send-to-sale")
        .removeHeaders("*") 
        .setProperty("customerID").simple("${body.customer.id}")
        .setProperty("customerFirstName").simple("${body.customer.firstName}")
        .setProperty("customerLastName").simple("${body.customer.lastName}") 
        .setProperty("customerGroup").simple("${body.customer.group}") 
        .setProperty("customerUserName").simple("${body.customer.customerCode}") 
        .marshal().json(JsonLibrary.Gson)
        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .to("http://localhost:8083/api/sales")
        .to("jms:queue:get-customer-summary");
       

   }

}
