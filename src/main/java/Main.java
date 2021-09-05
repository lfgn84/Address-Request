import dataBase.DataBase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import router.SQLRouteBuilder;
import router.requestRouteBuilder;

public class Main {
    public static void main(String[] args) throws Exception {
        DataBase dataBase = new DataBase();

        SimpleRegistry registry = new SimpleRegistry();
        registry.put("dataSource", dataBase.dataSource());

        CamelContext requestParams = new DefaultCamelContext();
        CamelContext paramsQuery = new DefaultCamelContext(registry);

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setTrustAllPackages(true);

        requestParams.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        requestParams.addRoutes(new requestRouteBuilder());

        paramsQuery.addRoutes(new SQLRouteBuilder());


        while(true){
            requestParams.start();
            paramsQuery.start();

            ConsumerTemplate paramsConsumerTemplate = requestParams.createConsumerTemplate();
            String params = paramsConsumerTemplate.receiveBody("seda:receivedParams", String.class);
            System.out.println(params);

            String [] parsedRequest = params.split(",");

            ProducerTemplate paramsProducerTemplate = paramsQuery.createProducerTemplate();
            System.out.println("select * from address where first_name = \"" + parsedRequest[0].trim() + "\" and last_name = \"" + parsedRequest[1].trim() + "\"");
            paramsProducerTemplate.sendBody("direct:start", "select * from address where first_name = \"" + parsedRequest[0].trim() + "\" and last_name = \"" + parsedRequest[1].trim() + "\";");

             }
    }
}
