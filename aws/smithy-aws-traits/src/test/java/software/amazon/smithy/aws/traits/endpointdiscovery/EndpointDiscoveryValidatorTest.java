/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.aws.traits.endpointdiscovery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

import java.util.List;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.validation.Severity;
import software.amazon.smithy.model.validation.ValidatedResult;
import software.amazon.smithy.model.validation.ValidationEvent;

public class EndpointDiscoveryValidatorTest {
    @Test
    public void validatesEndpointOperationBound() {
        ValidatedResult<Model> result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(getClass().getResource("endpoint-operation-unbound.json"))
                .assemble();

        List<ValidationEvent> events =  result.getValidationEvents(Severity.ERROR);
        assertThat(events, not(empty()));
        assertThat(events.get(0).getMessage(), containsString("must be bound"));
    }

    @Test
    public void warnsOnConfiguredServicesWithoutBoundConfiguredOperations() {
        ValidatedResult<Model> result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(getClass().getResource("no-configured-operations.json"))
                .assemble();

        List<ValidationEvent> events =  result.getValidationEvents(Severity.WARNING);
        assertThat(events, not(empty()));
        assertThat(events.get(0).getMessage(), containsString("no operations bound"));
    }

    @Test
    public void validatesOperationBoundToConfiguredService() {
        ValidatedResult<Model> result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(getClass().getResource("service-not-configured.json"))
                .assemble();

        List<ValidationEvent> events =  result.getValidationEvents(Severity.ERROR);
        assertThat(events, not(empty()));
        assertThat(events.get(0).getMessage(), containsString("not attached to a service"));
    }
}
