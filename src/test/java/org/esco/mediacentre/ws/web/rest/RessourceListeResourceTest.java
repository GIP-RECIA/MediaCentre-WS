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
package org.esco.mediacentre.ws.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.config.GARClientConfiguration;
import org.esco.mediacentre.ws.service.IRemoteRequestService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jgribonvald on 12/06/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class RessourceListeResourceTest {

    private Resource resources0377777U;
    private Resource resources0450822X;

    @Autowired
    List<IRemoteRequestService> remoteServices;

    @Autowired(required = false)
    private GARClientConfiguration garClientConfiguration;

    private RestTemplate restTemplate;

    private MockRestServiceServer mockGARServer;

    private MockMvc mockListRessourcesMvc;

    @Before
    public void setUp() throws IOException {
        resources0377777U = new ClassPathResource("json/gar_reponse_0377777U.json");
        resources0450822X = new ClassPathResource("json/gar_reponse_0450822X.json");
        String ressourceContent = Files.toString(new File(resources0377777U.getURI()), Charset.forName("UTF-8"));
        String ressourceContent2 = Files.toString(new File(resources0450822X.getURI()), Charset.forName("UTF-8"));
        assertThat(ressourceContent != null);
        assertThat(ressourceContent2 != null);
        assertThat(!ressourceContent.isEmpty());
        assertThat(!ressourceContent2.isEmpty());

        if (restTemplate != null) {
            this.mockGARServer = MockRestServiceServer.createServer(restTemplate);
        }

    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);

        if (garClientConfiguration != null && garClientConfiguration.GARRestTemplate() != null) {
            restTemplate = garClientConfiguration.GARRestTemplate();
        }

        RessourceListResource restListRessources = new RessourceListResource();

        ReflectionTestUtils.setField(restListRessources,
                "remoteServices", remoteServices);

        this.mockListRessourcesMvc = MockMvcBuilders.standaloneSetup(restListRessources).build();
    }

    @Test
    public void testRecuperation() throws Exception {
        if (garClientConfiguration != null) {
            URI serverUri1 = new URI(garClientConfiguration.getGARProperties().getHostConfig().getScheme(), garClientConfiguration.getGARProperties().getHostConfig().getHost(),
                    "/ressources/F0/0450822X/F08001ut", null);
            URI serverUri2 = new URI(garClientConfiguration.getGARProperties().getHostConfig().getScheme(), garClientConfiguration.getGARProperties().getHostConfig().getHost(),
                    "/ressources/F0/0377777U/F08001ut", null);

            if (mockGARServer != null) {
                mockGARServer.expect(MockRestRequestMatchers.requestTo(serverUri1))
                        .andRespond(MockRestResponseCreators.withSuccess(resources0450822X, MediaType.APPLICATION_JSON_UTF8));
                mockGARServer.expect(MockRestRequestMatchers.requestTo(serverUri2))
                        .andRespond(MockRestResponseCreators.withSuccess(resources0377777U, MediaType.APPLICATION_JSON_UTF8));
            }
        }

        Map<String,List<String>> userInfos = new HashMap<>();
            userInfos.put("uid",  Lists.newArrayList("F08001ut"));
            userInfos.put("ESCOUAI", Lists.newArrayList("0450822X","0377777U"));
            userInfos.put("ESCOUAICourant", Lists.newArrayList("0450822X"));
        mockListRessourcesMvc.perform(post("/api/ressources")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userInfos)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.[0]", Matchers.hasKey("idRessource")))
                .andExpect(jsonPath("$.[0]", Matchers.hasKey("idEtablissement")))
                .andExpect(jsonPath("$.[0].idEtablissement", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].idEtablissement.[0]", Matchers.hasKey("UAI")))
                .andExpect(jsonPath("$.[0].idEtablissement.[0].UAI").value(Matchers.isIn(Lists.newArrayList("0450822X", "0377777U"))));
    }
}
