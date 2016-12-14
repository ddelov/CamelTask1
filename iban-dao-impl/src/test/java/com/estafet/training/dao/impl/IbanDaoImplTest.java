package com.estafet.training.dao.impl;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.junit.BeforeClass;
//import org.junit.Test;

/**
 * Created by Delcho Delov on 07/12/16.
 */
public class IbanDaoImplTest extends CamelTestSupport {
    static final String IBAN_ONE = "BG66 ESTF 0616 0000 0000 01";
    static final String OWNER = "D. Delov";
    static final BigDecimal BALANCE = new BigDecimal(7737.73).setScale(2, BigDecimal.ROUND_HALF_UP);
    static final String CURRENCY = "£";

    private static final Logger LOGGER = Logger.getLogger(IbanDaoImplTest.class.getName());

    protected static EntityManagerFactory entityManagerFactory;
    protected static EntityManager entityManager;
    protected static IDatabaseConnection connection;
    protected static IDataSet dataSet;

//    @BeforeClass
//    public static void initEntityManager() {
//        entityManagerFactory = Persistence.createEntityManagerFactory("iban-dao-impl-test");
//        entityManager = entityManagerFactory.createEntityManager();
//
//        connection = wrapDatabaseConnection(entityManager);
////        dataSet = loadBaseDataSet("chf_dao/base-dataset.xml");
//    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();

        // setup the properties component to use the production file
        PropertiesComponent prop = context.getComponent("properties", PropertiesComponent.class);
//        prop.setLocation("classpath:/etc/bankx.placeholders.properties");

        return context;
    }

//    @BeforeClass
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("iban-dao-impl-test");
        entityManager = entityManagerFactory.createEntityManager();
        connection = wrapDatabaseConnection(entityManager);
    }

//    @Test
    public void testInsertAccount() {
//        IbanDaoImpl service = new IbanDaoImpl();
//        service.setEntityManager(entityManager);
//        service.insertAccount(new Account(IBAN_ONE, OWNER, BALANCE.doubleValue(), CURRENCY));
//        assertTrue(res);
    }

    public static IDatabaseConnection wrapDatabaseConnection(EntityManager entityManager) {
        OpenJPAEntityManager kem = OpenJPAPersistence.cast(entityManager);
        IDatabaseConnection connection = null;
        try {
            connection = new DatabaseConnection((Connection)kem.getConnection());
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());

        } catch (DatabaseUnitException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return connection;

    }
//    public static IDataSet loadBaseDataSet(String baseDataSet) {
//        FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
//        flatXmlDataSetBuilder.setColumnSensing(true);
//        try {
//            IDataSet dataset = flatXmlDataSetBuilder.build(
//                    Thread.currentThread().getContextClassLoader().getResourceAsStream(baseDataSet));
//
//            return dataset;
//        } catch (DataSetException e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//        }
//
//        return null;
//    }

}