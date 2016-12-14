package com.estafet.training.iban;

import com.estafet.training.iban.route.EntryRouteBuilder;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.estafet.training.iban.route.EntryRouteBuilder.ROUTE_ENTRY;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Delcho Delov on 01/12/16.
 */
public class EntryRouteBuilderTest extends CamelTestSupport {
    private static final Logger LOGGER = Logger.getLogger(EntryRouteBuilderTest.class.getName());
    private final EntryRouteBuilder routeBuilder = new EntryRouteBuilder();

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();

        // setup the properties component to use the production file
        PropertiesComponent prop = context.getComponent("properties", PropertiesComponent.class);
        prop.setLocation("classpath:/etc/bankx.placeholders.properties");

        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return routeBuilder;
    }

    @Test
    public void testOne() throws Exception {
        assertNotNull(context);
        RouteDefinition route = context.getRouteDefinition(ROUTE_ENTRY);

        route.adviceWith(context, new AdviceWithRouteBuilder() {
            public void configure() throws Exception {
                replaceFromWith("direct:start");
                interceptSendToEndpoint("activemq:*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:muuEntry.test");
            }
        });

        // we must manually start when we are done with all the advice with
        context.start();

        final String request = readPayload("entryRoute");
        template.sendBody("direct:start", request);

        MockEndpoint mockEndpoint = getMockEndpoint("mock:muuEntry.test");
        mockEndpoint.expectedMessageCount(3);
        List<Exchange> receivedExchanges = mockEndpoint.getReceivedExchanges();
        String IBANS[] = {"BG66 ESTF 0616 0000 0000 21", "BG66 ESTF 0616 0000 0000 22", "BG66 ESTF 0616 0000 0000 23"};
        for (int i = 0; i < receivedExchanges.size(); i++) {
            Exchange exchange = receivedExchanges.get(i);
            assertNotNull(exchange);
            Message message = exchange.getIn();
            assertNotNull(message);
            assertIsInstanceOf(String.class, message.getBody());
            String iban = message.getBody(String.class);
            assertThat(iban, is(IBANS[i]));
        }

        assertMockEndpointsSatisfied();
        context.stop();
    }

    @Test
    public void testValidRequest(){
        RestAssured.baseURI = "http://localhost:20616";
        final String request = readPayload("entryRoute");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                    .post("/estafet/iban/report")
                .then()
                    .assertThat().statusCode(200)
                .extract().body().toString();
    }
    @Test
    public void testNotSoValidRequest(){
        RestAssured.baseURI = "http://localhost:20616";
        final String wrongRequest = readPayload("stadoMe4ki");
        given()
                .contentType(ContentType.JSON)
                .body(wrongRequest)
                .when()
                .post("/estafet/iban/report")
                .then()
                .assertThat().statusCode(500)
                .extract().body().toString();
    }

    private String readPayload(String name) {
        String content = null;

        URL url = this.getClass().getResource("/json/" + name + ".json");
        try {
            System.out.println("Resources Path : " + url.getFile());
            content = new String(Files.readAllBytes(Paths.get(url.getFile())));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return content;
    }

}