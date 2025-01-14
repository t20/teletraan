/**
 * Copyright 2016 Pinterest, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinterest.teletraan;

import com.pinterest.teletraan.health.GenericHealthCheck;
import com.pinterest.teletraan.resource.Pings;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.health.conf.HealthConfiguration;
import io.dropwizard.health.core.HealthCheckBundle;
import io.dropwizard.setup.Bootstrap;

public class TeletraanAgentService extends Application<TeletraanServiceConfiguration> {
    @Override
    public String getName() {
        return "teletraan-agent-service";
    }

    @Override
    public void initialize(Bootstrap<TeletraanServiceConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        bootstrap.addBundle(new HealthCheckBundle<TeletraanServiceConfiguration>() {
            @Override
            protected HealthConfiguration getHealthConfiguration(final TeletraanServiceConfiguration configuration) {
                return configuration.getHealthConfiguration();
            }
        });
        }

    @Override
    public void run(TeletraanServiceConfiguration configuration, Environment environment) throws Exception {
        TeletraanServiceContext context = ConfigHelper.setupContext(configuration);
        environment.jersey().register(configuration.getAuthenticationFactory().create(context));

        Pings pings = new Pings(context);
        environment.jersey().register(pings);

        environment.healthChecks().register("generic", new GenericHealthCheck(context));
    }

    public static void main(String[] args) throws Exception {
        new TeletraanAgentService().run(args);
    }
}
