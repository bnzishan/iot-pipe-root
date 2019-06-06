package org.hobbit.sdk.iotpipeline_bm.benchmark;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.hobbit.core.components.AbstractEvaluationModule;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.sdk.iotpipeline_bm.Constants;
import org.hobbit.vocab.HOBBIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EvalModule extends AbstractEvaluationModule {

    private Property EXECUTION_TIME = null;
    private Property NUM_OF_TASK_FAILURES = null;

    private Model model = ModelFactory.createDefaultModel();


    private static final Logger logger = LoggerFactory.getLogger(EvalModule.class);


    @Override
    protected void evaluateResponse(byte[] expectedData, byte[] receivedData, long taskSentTimestamp, long responseReceivedTimestamp) throws Exception {
        // evaluate the given response and store the result, e.g., increment internal counters
        logger.trace("evaluateResponse()");

        final String TRUE_RESPONSE = "/correct/";
        final String FALSE_RESPONSE = "/wrong/";

        Long expectedExecutionTime = RabbitMQUtils.readLong(expectedData);
        Long receivedExecutionTime = RabbitMQUtils.readLong(receivedData);
/*
        if (receivedScore >= FACTCHECK_THRESHOLD)
            receivedResponse = TRUE_RESPONSE;
        else
            receivedResponse = FALSE_RESPONSE;
*/

    }

    @Override
    public void init() throws Exception {
        super.init();

        Map<String, String> env = System.getenv();

        if (!env.containsKey(Constants.EXECUTION_TIME)) {
            throw new IllegalArgumentException("Couldn't get \"" + Constants.EXECUTION_TIME
                    + "\" from the environment. Aborting.");
        }
        EXECUTION_TIME = this.model.createProperty(env.get(Constants.EXECUTION_TIME));


        if (!env.containsKey(Constants.NUM_OF_TASK_FAILURES)) {
            throw new IllegalArgumentException("Couldn't get \"" + Constants.NUM_OF_TASK_FAILURES
                    + "\" from the environment. Aborting.");
        }
        NUM_OF_TASK_FAILURES = this.model.createProperty(env.get(Constants.NUM_OF_TASK_FAILURES));
    }



    @Override
    protected Model summarizeEvaluation() throws Exception {
        logger.debug("summarizeEvaluation()");
        // All tasks/responsens have been evaluated. Summarize the results,
        // write them into a Jena model and send it to the benchmark controller.
        Model model = createDefaultModel();
        Resource experimentResource = model.getResource(experimentUri);
        model.add(experimentResource , RDF.type, HOBBIT.Experiment);

        logger.debug("Sending result model: {}", RabbitMQUtils.writeModel2String(model));

        return model;
    }

    @Override
    public void close(){
        // Free the resources you requested here
        logger.debug("close()");
        // Always close the super class after yours!
        try {
            super.close();
        }
        catch (Exception e){

        }
    }

}
