/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package builder;

import creator.CustomerCreator;
import domain.Account;
import domain.Customer;
import org.apache.camel.Exchange;
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
    from("jetty:http://localhost:9000/createCustomer?enableCORS=true")
        .setExchangePattern(ExchangePattern.InOnly)
        .convertBodyTo(String.class)
        .unmarshal().json(JsonLibrary.Gson, Account.class)
        .bean(CustomerCreator.class, "add(${body})")
        .log("Customer: ${body}")
        .to("jms:queue:send-to-vend");
 
    from("jms:queue:send-to-vend")
        .removeHeaders("*")
        .setHeader("Authorization", constant("Bearer KiQSsELLtocyS2WDN5w5s_jYaBpXa0h2ex1mep1a"))
        .marshal().json(JsonLibrary.Gson)
        .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
        .to("https://info303otago.vendhq.com/api/2.0/customers?throwExceptionOnFailure=false")
        .choice()
          .when().simple("${header.CamelHttpResponseCode} == '201'")
             .convertBodyTo(String.class)
             .to("jms:queue:vend-response")
          .otherwise()
            .convertBodyTo(String.class)
            .to("jms:queue:vend-error")
          .endChoice();
    
    from("jms:queue:vend-response")
        .setBody().jsonpath("$.data")
        .marshal().json(JsonLibrary.Gson)
        .unmarshal().json(JsonLibrary.Gson, Customer.class)
        .log("Customer: ${body}")
        .to("jms:queue:create-customer-graphql");
    
    from("jms:queue:create-customer-graphql")
	.toD("graphql://http://localhost:8082/graphql?query=mutation{addAccount(account: {id: \"${body.id}\", email: \"${body.email}\", username: \"${body.customerCode}\", firstName: \"${body.firstName}\", lastName: \"${body.lastName}\", group: \"${body.group}\"}) {id,email,username,firstName,lastName,group}}")
        .log("GraphQL service called");
        
   }

}
