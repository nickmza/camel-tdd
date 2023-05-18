package net.nickmck.cameltdd;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.springframework.stereotype.Component;

@Component
public class simpleRestRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer://example?repeatCount=1")
                .routeId("simple-rest")
                .process(this::createMessage)
                .marshal().json()
                .setHeader(Exchange.CONTENT_TYPE, constant("text/xml; charset=utf-8"))
                .to("https://en1gvb5qo7vg4.x.pipedream.net");
    }

    private void createMessage(Exchange exchange) {
        RestMessage rest = new RestMessage();
        rest.setMessage("Hello From Camel.");

        Message message = new DefaultMessage(exchange);
        message.setBody(rest);
        exchange.setMessage(message);
    }
}
