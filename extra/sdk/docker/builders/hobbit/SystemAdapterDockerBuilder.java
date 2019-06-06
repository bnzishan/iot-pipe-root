package org.hobbit.sdk.docker.builders.hobbit;

import org.hobbit.sdk.docker.builders.AbstractDockersBuilder;
import org.hobbit.sdk.docker.builders.BothTypesDockersBuilder;

import static org.hobbit.core.Constants.*;
import static org.hobbit.sdk.Constants.HOBBIT_NETWORKS;


/**
 * @author Pavel Smirnov
 */

public class SystemAdapterDockerBuilder extends BothTypesDockersBuilder {
    private static final String name = "system-adapter";
//    private String parameters = new JenaKeyValue().encodeToString();

    public SystemAdapterDockerBuilder(AbstractDockersBuilder builder) {
        super(builder);

    }

//    public SystemAdapterDockerBuilder parameters(String parameters){
//        this.parameters = parameters;
//        return this;
//    }
//
//    public SystemAdapterDockerBuilder parameters(JenaKeyValue parameters){
//        this.parameters = parameters.encodeToString();
//        return this;
//    }

    @Override
    public void addEnvVars(AbstractDockersBuilder ret) {
        ret.addEnvironmentVariable(RABBIT_MQ_HOST_NAME_KEY, (String)System.getenv().get(RABBIT_MQ_HOST_NAME_KEY));
        ret.addEnvironmentVariable(HOBBIT_SESSION_ID_KEY, (String)System.getenv().get(HOBBIT_SESSION_ID_KEY));
        ret.addNetworks(HOBBIT_NETWORKS);

        ret.addEnvironmentVariable(SYSTEM_PARAMETERS_MODEL_KEY, (String)System.getenv().get(SYSTEM_PARAMETERS_MODEL_KEY));
        //ret.addEnvironmentVariable(CONTAINER_NAME_KEY, ret.getContainerName());
    }

    @Override
    public String getName() {
        return name;
    }
}
