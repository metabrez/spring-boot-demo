/*
 * Copyright  2016 Sebastian Gil.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.create.controller;

import com.create.application.configuration.ServiceConfiguration;
import com.create.application.configuration.TestConfiguration;
import com.create.application.configuration.ValidatorConfiguration;
import com.create.application.configuration.WebConfiguration;
import com.create.junit4.EmbeddedTomcatShutdown;
import com.create.model.Ticket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.io.IOException;

import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@RunWith(Parameterized.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {
        TestConfiguration.class,
        ValidatorConfiguration.class,
        WebConfiguration.class,
        ServiceConfiguration.class}
)
@TestExecutionListeners(value = EmbeddedTomcatShutdown.class, mergeMode = MERGE_WITH_DEFAULTS)
public class BatchTicketValidatorIT {
    private static final String VALIDATOR_TICKET_URL = "/validator/tickets/batch";

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Parameters
    public static String[] getTestNames() throws IOException {
        return new RestTestNameProvider()
                .getTestNames(VALIDATOR_TICKET_URL);
    }

    @Autowired
    private TestRestTemplate authenticatedUserTestRestTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private final String testName;

    public BatchTicketValidatorIT(String testName) {
        this.testName = testName;
    }

    @Test
    public void shouldTestBatchTicketValidatorRequest() throws Exception {
        final ValidationResultRestTestExecutor validationResultRestTestExecutor = new ValidationResultRestTestExecutor(
                authenticatedUserTestRestTemplate, objectMapper);
        validationResultRestTestExecutor.executeListTestRestRequest(VALIDATOR_TICKET_URL, testName, Ticket.class);
    }
}