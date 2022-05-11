/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package builder;

import creator.GroupComparison;
import domain.Sale;
import domain.Summary;
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
        .log("New sale from Vend: ${body.id}")
        .setProperty("customerID").simple("${body.customer.id}")
        .setProperty("customerFirstName").simple("${body.customer.firstName}")
        .setProperty("customerLastName").simple("${body.customer.lastName}") 
        .setProperty("customerGroup").simple("${body.customer.group}") 
        .setProperty("customerEmail").simple("${body.customer.email}") 
        .setProperty("customerUserName").simple("${body.customer.customerCode}")
        .to("jms:queue:add-to-sale-service");

    from("jms:queue:add-to-sale-service")
        .removeHeaders("*") 
        .marshal().json(JsonLibrary.Gson)
        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .to("http://localhost:8083/api/sales")
        .log("Sale added into the local service")
        .to("jms:queue:customer-summary");

    from("jms:queue:customer-summary")
        .removeHeaders("*") // remove headers to stop them being sent to the service
        .setBody(constant((Object) null)) // doesn't usually make sense to pass a body in a GET request
        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
        .toD("http://localhost:8083/api/sales/customer/${exchangeProperty.customerID}/summary")
        .convertBodyTo(String.class)
        .log("Summary of customer requested")
        .to("jms:queue:summary-response");

    from("jms:queue:summary-response")
        .unmarshal().json(JsonLibrary.Gson, Summary.class)
        .log("Account type from summary: ${body.group}")
        .bean(GroupComparison.class, "compare(${exchangeProperty.customerID}, ${exchangeProperty.customerFirstName}, "
                + "${exchangeProperty.customerLastName}, ${exchangeProperty.customerEmail}, "
                + "${exchangeProperty.customerUserName}, ${body})")
        .log("Extracted from summary: ${body.group}")
        .log("Current group from Vend: ${exchangeProperty.customerGroup}")
        .choice()
            .when().simple("${body.group} != ${exchangeProperty.customerGroup}")
                .log("Group needs to be updated")
                .to("jms:queue:update-group")
            .otherwise()
                .log("Group is not updated, therefore no action is required")
                .to("jms:queue:group-not-updated"); 

    from("jms:queue:update-group")
        .multicast()
        .to("jms:queue:graphql-update-group", "jms:queue:vend-update-group"); 

    from("jms:queue:graphql-update-group")
        .log("Extracted: ${body}")
        .toD("graphql://http://localhost:8082/graphql?query=mutation{changeGroup(id: \"${body.id}\", newGroup: \"${body.group}\") "
                + "{id, email, username, firstName, lastName, group}}")
        .log("Group updated in the Accounts service")
        .to("jms:queue:graphql-update-group-response");
       
    from("jms:queue:vend-update-group")
        .removeHeaders("*")
        .setHeader("Authorization", constant("Bearer KiQSsELLtocyS2WDN5w5s_jYaBpXa0h2ex1mep1a"))
        .marshal().json(JsonLibrary.Gson)
        .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
        .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
        .toD("https://info303otago.vendhq.com/api/2.0/customers/${exchangeProperty.customerID}?throwExceptionOnFailure=false")
        .choice()
            .when().simple("${header.CamelHttpResponseCode} == '200'")
                .convertBodyTo(String.class)
                .log("Group updated in the Vend")
                .to("jms:queue:vend-update-group-response")
            .otherwise()
                .convertBodyTo(String.class)
                .log("Error updating the group in Vend")
                .to("jms:queue:vend-update-group-error");
   }

}
