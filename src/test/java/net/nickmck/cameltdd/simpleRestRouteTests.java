package net.nickmck.cameltdd;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.model.language.SimpleExpression;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootTest
@CamelSpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("https://.*")
public class simpleRestRouteTests {

    static Logger logger = Logger.getLogger(simpleRestRouteTests.class.getName());

    @Autowired
    protected CamelContext camelContext;

    @Autowired
    protected ProducerTemplate producerTemplate;

    @EndpointInject("mock:https:en1gvb5qo7vg4.x.pipedream.net")
    private MockEndpoint restEndpoint;

    @Test
    public void EnsureThatEndpointIsCalled() throws Exception {

        AdviceWith.adviceWith(camelContext,
                "simple-rest",
                rb -> rb.replaceFromWith("direct:file:start"));

        camelContext.start();

        restEndpoint.expectedMessageCount(1);

        restEndpoint.whenAnyExchangeReceived( (Exchange exchange) -> {
            logger.log(Level.INFO, "Mock was HIT!!!");
        });

        producerTemplate.sendBody("direct:file:start", "Hello from Test!");

        restEndpoint.assertIsSatisfied();
    }

    @Test
    public void SimulateErrorCondition() throws Exception {

        AdviceWith.adviceWith(camelContext,
                "simple-rest",
                rb -> rb.replaceFromWith("direct:file:start"));

        var HTTPHeader = new SimpleExpression("200");
        HTTPHeader.setResultType(Integer.class);

        AdviceWith.adviceWith(camelContext,
                "simple-rest",
                rb -> rb.weaveByToUri("https://en1gvb5qo7vg4.x.pipedream.net")
                        .replace()
                        .setHeader("CamelHttpResponseCode", HTTPHeader)
                        .setBody(new ConstantExpression("Respomse")));

        camelContext.start();

        producerTemplate.sendBody("direct:file:start", "Hello");
    }

}
