package com.estafet.training.iban;

import com.estafet.training.api.AccountServiceApi;
import com.estafet.training.iban.processor.EnrichProcessor;
import com.estafet.training.iban.route.ReportsFileBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.estafet.training.iban.route.ReportsFileBuilder.ROUTE_DIRECT_ENR;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Delcho Delov on 02/12/16.
 */
public class ReportsFileBuilderTest extends CamelTestSupport {
    final EnrichProcessor enrichProcessor = new EnrichProcessor();

    public static JSONObject unmarshalJsonString(String jsonContent) throws JAXBException {
        if (jsonContent == null) {
            return null;
        }
        JSONParser parser = new JSONParser();
        try {
            JSONObject result = (JSONObject) parser.parse(jsonContent);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

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
        return new ReportsFileBuilder();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        final AccountServiceApi dummyAccServiceImpl = new EnrichProcessorTest.ConstAccService();
        enrichProcessor.setAccountEnricherService(dummyAccServiceImpl);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        final AccountServiceApi dummyAccServiceImpl = new EnrichProcessorTest.ConstAccService();
        final EnrichProcessor enrichProcessor = new EnrichProcessor();
        enrichProcessor.setAccountEnricherService(dummyAccServiceImpl);

        registry.bind("enrichProcessor", enrichProcessor);

        return registry;
    }

    @Test
    public void testOne() throws Exception {
        assertNotNull(context);
        RouteDefinition route = context.getRouteDefinition(/*"directEnr"*/ ROUTE_DIRECT_ENR);

        route.adviceWith(context, new AdviceWithRouteBuilder() {
            public void configure() throws Exception {
//                replaceFromWith("direct:start");
                interceptSendToEndpoint("file:*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:muuEntry.test");
            }
        });

        // we must manually start when we are done with all the advice with
        context.start();
        final long aggregateIntervalInMillis = TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS);
        String IBANS[] = {"BG66ESTF06160000000021", "BG66ESTF06160000000022", "BG66ESTF06160000000023"};

        MockEndpoint mockEndpoint = getMockEndpoint("mock:muuEntry.test");
        mockEndpoint.expectedMessageCount(1);

        template.sendBody("direct:enr", IBANS[0]);
        Thread.sleep(aggregateIntervalInMillis);
        template.sendBody("direct:enr", IBANS[1]);
        Thread.sleep(aggregateIntervalInMillis);
        template.sendBody("direct:enr", IBANS[2]);
        Thread.sleep(aggregateIntervalInMillis * 2);

        assertMockEndpointsSatisfied();

        List<Exchange> receivedExchanges = mockEndpoint.getReceivedExchanges();
        Exchange exchange = receivedExchanges.get(0);

        assertNotNull(exchange);
        Message message = exchange.getIn();
        assertNotNull(message);
        assertNotNull(message.getBody(String.class));

        String payload = message.getBody(String.class);//payload should be json of AccountsWrapper<Account> with 3 accounts
        JSONObject jsonObject = unmarshalJsonString(payload);
        List<JSONObject> wrapper = (List<JSONObject>) jsonObject.get("accounts");
        assertNotNull(wrapper);
        assertThat(wrapper.size(), is(3));
        JSONObject first = wrapper.get(0);
        String fiban = (String) first.get("iban");
        Double balance = (Double) first.get("balance");
        String currency = (String) first.get("currency");
        String name = (String) first.get("name");
        assertThat(fiban, is(IBANS[0]));
        assertThat(balance, is(EnrichProcessorTest.BALANCE.doubleValue()));
        assertThat(currency, is(EnrichProcessorTest.CURRENCY));
        assertThat(name, is(EnrichProcessorTest.OWNER));

        context.stop();
    }
}