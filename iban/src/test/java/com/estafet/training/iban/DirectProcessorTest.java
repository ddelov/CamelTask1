package com.estafet.training.iban;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Created by Delcho Delov on 01/12/16.
 */
public class DirectProcessorTest extends CamelTestSupport {

    private static final String ALA_BALA = "ALA BALA";
    private static final Mechka BABA_MECA = new Mechka("Baba Meca", false);

    @Test
    public void processString() throws Exception {
        DirectProcessor<String> processor = new DirectProcessor<>();
        processor.process(createExchangeWithBody(ALA_BALA));
        assertNotNull(processor);
        assertIsInstanceOf(String.class, processor.getEntry());
        String entry = processor.getEntry();
        assertSame(entry, ALA_BALA);
    }
    @Test
    public void processMechka() throws Exception {
        DirectProcessor<Mechka> processor = new DirectProcessor<>();
        processor.process(createExchangeWithBody(BABA_MECA));
        assertNotNull(processor);
        assertIsInstanceOf(Mechka.class, processor.getEntry());
        Mechka entry = processor.getEntry();
        assertSame(entry, BABA_MECA);
    }

}