/**
 * Copyright (C) 2017 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2017 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.mediacentre.ws.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.recia.mediacentre.ws.SystemPropertyIncludeProfileResolver;
import fr.recia.mediacentre.ws.web.rest.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test,WITHOUT_GAR,WITHOUT_STRUCT_REST,USER_MAPPING", resolver= SystemPropertyIncludeProfileResolver.class)
@Slf4j
@SpringBootTest
public class HealthCheckTest {

    private MockMvc mockHealthCheckMvc;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);

        HealthCheck healthCheck = new HealthCheck();

        this.mockHealthCheckMvc = MockMvcBuilders.standaloneSetup(healthCheck)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testHealthCheck() throws Exception {

        mockHealthCheckMvc.perform(head("/health-check")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }


}
