package com.estafet.training.iban;

import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Delcho Delov on 30/11/16.
 */
public class Iban2CsvProcessorTest extends CamelTestSupport {
    private final Iban2CsvProcessor processor = new Iban2CsvProcessor();
    private final static String IBAN_ONE = "BG66 ESTF 0616 0000 0000 01";
    private final static String IBAN_ONE_COMPRESSED = "BG66ESTF06160000000001";

    @Test
    public void testTransformIban(){
        final Exchange exchange = createExchangeWithBody(IBAN_ONE);
        try {
            processor.process(exchange);
            Object body = exchange.getIn().getBody();
            assertThat(String.class.getName(), is(body.getClass().getName()));
            String transformedIban = (String)body;
            assertThat(transformedIban, is(IBAN_ONE_COMPRESSED));
            assertThat(transformedIban.contains(" "), is(false));
            assertNotNull(transformedIban);
            compareNonBlancCharacters(IBAN_ONE, IBAN_ONE_COMPRESSED);
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }
    @Test
    public void testTransformEmptyIban(){
        final Exchange exchange = createExchangeWithBody(null);
        try {
            processor.process(exchange);
            Object body = exchange.getIn().getBody();
            assertNull(body);
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }
    private static void compareNonBlancCharacters(String withSpaces, String woBlanks) throws Exception{
        //check all non-blank characters are on place
        int nbCount=0;
        for(int i=0, j=0; i<withSpaces.length(); ++i) {
            char ci = withSpaces.charAt(i);
            if(ci==' '){
                continue;
            }
            ++nbCount;//counts non blank characters from first string
            char cf = woBlanks.charAt(j++);
            if(ci!=cf){
                throw new Exception("Do not match");
            }
        }
        // check that woBlanks is no longer than withSpaces
        if(nbCount!=woBlanks.length()){
            throw new Exception("Do not match");
        }
    }

}