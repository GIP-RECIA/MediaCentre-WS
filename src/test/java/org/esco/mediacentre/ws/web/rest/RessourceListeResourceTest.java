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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.config.GARClientConfiguration;
import org.esco.mediacentre.ws.service.GARRequestServiceImpl;
import org.esco.mediacentre.ws.service.IRemoteRequestService;
import org.esco.mediacentre.ws.service.MockedRequestServiceImpl;
import org.esco.mediacentre.ws.web.rest.exception.GlobalExceptionHandler;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by jgribonvald on 12/06/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class RessourceListeResourceTest {

    @Autowired
    List<IRemoteRequestService> remoteServices;

    @Autowired(required = false)
    private GARClientConfiguration garClientConfiguration;

//    private RestTemplate restTemplate;
//
//    private MockRestServiceServer mockGARServer;

    private MockMvc mockListRessourcesMvc;

    @Autowired
    private Environment environment;

    private boolean isWithoutGAR = false;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);

        for(String profile : environment.getActiveProfiles()){
            if ("WITHOUT_GAR".equals(profile)){
                this.isWithoutGAR = true;
                log.debug("Runing in Without GAR mode !");
            }
        }

        RessourceListResource restListRessources = new RessourceListResource();

        ReflectionTestUtils.setField(restListRessources,
                "remoteServices", remoteServices);

        this.mockListRessourcesMvc = MockMvcBuilders.standaloneSetup(restListRessources)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private MockedRequestServiceImpl getMockedService() {
        for (IRemoteRequestService rrs : remoteServices) {
            if (rrs instanceof MockedRequestServiceImpl ) {
                log.debug("Using MockedReequestServiceImpl !");
                return (MockedRequestServiceImpl) rrs;
            }
        }
        return null;
    }

    @Test
    public void testRecuperation() throws Exception {
        Map<String,List<String>> userInfos = new HashMap<>();
        if (isWithoutGAR) {
            userInfos.put("uid",  Lists.newArrayList("F01000ugr"));
            userInfos.put("ESCOUAI", Lists.newArrayList("0450822X","0377777U"));
            userInfos.put("ESCOUAICourant", Lists.newArrayList("0450822X"));
            userInfos.put("ENTPersonProfils", Lists.newArrayList("National_ENS"));

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
        } else {
//            userinfos are setted from configuration file
//            userInfos.put("uid",  Lists.newArrayList("F1600m19"));
//            userInfos.put("ESCOUAI", Lists.newArrayList("0291595B"));
//            userInfos.put("ESCOUAICourant", Lists.newArrayList("0291595B"));
//            userInfos.put("ENTPersonProfils", Lists.newArrayList("National_ELV"));

            Map<String,List<String>> configUser =  garClientConfiguration.getGARProperties().getTestUser();

            assertThat(configUser, notNullValue());
            assertThat(configUser.keySet(), hasItem("uid"));
            assertThat(configUser.keySet(), hasItem("ESCOUAI"));
            assertThat(configUser.keySet(), hasItem("ESCOUAICourant"));
            assertThat(configUser.keySet(), hasItem("ENTPersonProfils"));
            assertThat(configUser.keySet(), hasItem("ENTPersonGARIdentifiant"));

            mockListRessourcesMvc.perform(post("/api/ressources")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(configUser)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.*", Matchers.notNullValue()))
                    .andExpect(jsonPath("$.[0]", Matchers.hasKey("idRessource")))
                    .andExpect(jsonPath("$.[0]", Matchers.hasKey("idEtablissement")))
                    .andExpect(jsonPath("$.[0].idEtablissement", Matchers.hasSize(1)))
                    .andExpect(jsonPath("$.[0].idEtablissement.[0]", Matchers.hasKey("UAI")));
        }
    }

    @Test
    public void testErreurRecup() throws Exception {
        Map<String,List<String>> userInfos = new HashMap<>();
        userInfos.put("uid",  Lists.newArrayList("erreur"));
        userInfos.put("ESCOUAI", Lists.newArrayList("NOTEXIST"));
        userInfos.put("ESCOUAICourant", Lists.newArrayList("NOTEXIST"));
        userInfos.put("ENTPersonProfils", Lists.newArrayList("National_ENS"));
        userInfos.put("ENTPersonGARIdentifiant", Lists.newArrayList("NOTEXIST"));
        log.debug("User tested {}", userInfos);

        mockListRessourcesMvc.perform(post("/api/ressources")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userInfos)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", Matchers.hasKey("Erreur")))
                .andExpect(jsonPath("$.Erreur", Matchers.hasKey("Code")))
                .andExpect(jsonPath("$.Erreur.Code", Matchers.equalToIgnoringCase(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath("$.Erreur", Matchers.hasKey("Message")))
                .andExpect(jsonPath("$.Erreur.Message", Matchers.notNullValue()))
                .andExpect(jsonPath("$.Erreur", Matchers.hasKey("Resource")))
                ;
    }

    @Test
    public void testErreurUnAuthorized() throws Exception {
        Map<String,List<String>> userInfos = new HashMap<>();
        userInfos.put("uid",  Lists.newArrayList("F01000ugr"));
        userInfos.put("ESCOUAI", Lists.newArrayList("0450822X","0377777U"));
        userInfos.put("ESCOUAICourant", Lists.newArrayList("0450822X"));
        userInfos.put("ENTPersonProfils", Lists.newArrayList("National_EVS"));
        userInfos.put("ENTPersonGARIdentifiant", Lists.newArrayList("NOTEXIST"));
        log.debug("User tested {}", userInfos);

        mockListRessourcesMvc.perform(post("/api/ressources")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userInfos)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", Matchers.hasKey("Erreur")))
                .andExpect(jsonPath("$.Erreur", Matchers.hasKey("Code")))
                .andExpect(jsonPath("$.Erreur.Code", Matchers.equalToIgnoringCase(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath("$.Erreur", Matchers.hasKey("Message")))
                .andExpect(jsonPath("$.Erreur.Message", Matchers.equalTo(GARRequestServiceImpl.UN_AUTHORIZED_MESSAGE)))
                .andExpect(jsonPath("$.Erreur", Matchers.hasKey("Resource")))
        ;
    }
}
