package router;

import org.apache.camel.builder.RouteBuilder;

public class requestRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("activemq:queue:addressParams")
                .to("seda:receivedParams");
    }
}
