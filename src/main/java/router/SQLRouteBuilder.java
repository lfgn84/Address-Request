package router;

import org.apache.camel.builder.RouteBuilder;
import service.ResultHandler;

public class SQLRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:start").to("jdbc:dataSource").bean(new ResultHandler(), "printResult");
    }


}
