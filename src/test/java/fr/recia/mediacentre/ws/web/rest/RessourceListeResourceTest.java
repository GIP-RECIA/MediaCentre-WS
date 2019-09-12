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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import fr.recia.mediacentre.ws.service.GARRequestServiceImpl;
import fr.recia.mediacentre.ws.service.IRemoteRequestService;
import fr.recia.mediacentre.ws.service.MockedRequestServiceImpl;
import fr.recia.mediacentre.ws.web.rest.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;;
import fr.recia.mediacentre.ws.SystemPropertyIncludeProfileResolver;
import fr.recia.mediacentre.ws.config.GARClientConfiguration;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by jgribonvald on 12/06/17.
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test,WITHOUT_GAR,WITHOUT_STRUCT_REST,USER_MAPPING", resolver= SystemPropertyIncludeProfileResolver.class)
@Slf4j
@SpringBootTest
public class RessourceListeResourceTest {

    @Autowired
    List<IRemoteRequestService> remoteServices;

    @Autowired(required = false)
    private GARClientConfiguration garClientConfiguration;

//    private RestTemplate restTemplate;
//
//    private MockRestServiceServer mockGARServer;

    private MockMvc mockListRessourcesMvc;
    private MockMvc mockListRessourcesDiffMvc;

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

        log.info("====== Environment and configuration ======");
        log.info("Active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        final MutablePropertySources sources = ((AbstractEnvironment) environment).getPropertySources();
        StreamSupport.stream(sources.spliterator(), false).forEach(val -> log.info("source {}", val.getName()));
        StreamSupport.stream(sources.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
                .filter(prop-> prop.contains("ressources"))
                .forEach(prop -> log.info("{}: {}", prop, environment.getProperty(prop)));
        log.info("===========================================");

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
                log.debug("Using MockedRequestServiceImpl !");
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
                    .content(TestUtil.convertObjectToJsonBytes(userInfos))
                    .characterEncoding("UTF-8"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    //.andExpect(content().encoding("UTF-8"))
                    .andExpect(jsonPath("$.*", Matchers.hasSize(3)))
                    .andExpect(jsonPath("$.[0]", Matchers.hasKey("idRessource")))
                    .andExpect(jsonPath("$.[0]", Matchers.hasKey("idEtablissement")))
                    .andExpect(jsonPath("$.[0].idEtablissement", Matchers.hasSize(1)))
                    .andExpect(jsonPath("$.[0].idEtablissement.[0]", Matchers.hasKey("UAI")))
                    .andExpect(jsonPath("$.[0].idEtablissement.[0].UAI").value(IsIn.in(Lists.newArrayList("0450822X", "0377777U"))));
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
                    .content(TestUtil.convertObjectToJsonBytes(configUser))
                    .accept(TestUtil.APPLICATION_JSON_UTF8))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(content().encoding("UTF-8"))
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
                .content(TestUtil.convertObjectToJsonBytes(userInfos))
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().encoding("UTF-8"))
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
                .content(TestUtil.convertObjectToJsonBytes(userInfos))
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().encoding("UTF-8"))
                .andExpect(jsonPath("$", Matchers.hasKey("Erreur")))
                .andExpect(jsonPath("$.Erreur", Matchers.hasKey("Code")))
                .andExpect(jsonPath("$.Erreur.Code", Matchers.equalToIgnoringCase(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath("$.Erreur", Matchers.hasKey("Message")))
                .andExpect(jsonPath("$.Erreur.Message", Matchers.equalTo(GARRequestServiceImpl.UN_AUTHORIZED_MESSAGE)))
                .andExpect(jsonPath("$.Erreur", Matchers.hasKey("Resource")))
        ;
    }

    @Test
    public void testRessourcesDiffusables() throws Exception {

        if (isWithoutGAR) {
            mockListRessourcesMvc.perform(get("/api/ressourcesDiffusables")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .accept(TestUtil.APPLICATION_JSON_UTF8))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(content().encoding("UTF-8"))
                    .andExpect(jsonPath("$", Matchers.notNullValue()))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", Matchers.hasSize(1)))
                    .andExpect(jsonPath("$.[0]", Matchers.hasKey("dateGeneration")))
                    .andExpect(jsonPath("$.[0]", Matchers.hasKey("ressourceDiffusable")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable").isArray())
                    .andExpect(jsonPath("$.[0].ressourceDiffusable").isNotEmpty())
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("idRessource")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("nomRessource")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("idEditeur")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("nomEditeur")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("distributeursCom")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0].distributeursCom").isArray())
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0].distributeursCom").isNotEmpty())
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0].distributeursCom.[0]", Matchers.hasKey("distributeurCom")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0].distributeursCom.[0]", Matchers.hasKey("nomDistributeurCom")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("distributeurTech")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("nomDistributeurTech")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("affichable")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("diffusable")))
                    .andExpect(jsonPath("$..ressourceDiffusable.[?(@.mereFamille)]").isNotEmpty())
                    .andExpect(jsonPath("$..ressourceDiffusable.[?(@.membreFamille)]").isNotEmpty());
        } else {
            mockListRessourcesMvc.perform(get("/api/ressourcesDiffusables")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(new String())
                    .accept(TestUtil.APPLICATION_JSON_UTF8))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(content().encoding("UTF-8"))
                    .andExpect(jsonPath("$", Matchers.notNullValue()))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isNotEmpty())
                    .andExpect(jsonPath("$.[0]", Matchers.hasKey("dateGeneration")))
                    .andExpect(jsonPath("$.[0]", Matchers.hasKey("ressourceDiffusable")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable").isArray())
                    .andExpect(jsonPath("$.[0].ressourceDiffusable").isNotEmpty())
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("idRessource")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("nomRessource")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("idEditeur")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("nomEditeur")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("distributeursCom")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0].distributeursCom").isArray())
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0].distributeursCom").isNotEmpty())
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0].distributeursCom.[0]", Matchers.hasKey("distributeurCom")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0].distributeursCom.[0]", Matchers.hasKey("nomDistributeurCom")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("distributeurTech")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("nomDistributeurTech")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("affichable")))
                    .andExpect(jsonPath("$.[0].ressourceDiffusable.[0]", Matchers.hasKey("diffusable")))
                    .andExpect(jsonPath("$..ressourceDiffusable.[?(@.mereFamille)]").isNotEmpty())
                    .andExpect(jsonPath("$..ressourceDiffusable.[?(@.membreFamille)]").isNotEmpty());
        }
    }
}
