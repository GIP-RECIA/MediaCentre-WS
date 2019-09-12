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
package fr.recia.mediacentre.ws.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import fr.recia.mediacentre.ws.model.ressource.diffusion.ListeRessourcesDiffusables;
import fr.recia.mediacentre.ws.model.ressource.diffusion.RessourceDiffusable;
import fr.recia.mediacentre.ws.model.structure.Structure;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jgribonvald on 12/06/17.
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"WITHOUT_GAR", "test"})
@Slf4j
@SpringBootTest
public class GarRestClientTest {

    @Mock
    private GARRequestServiceImpl garRequestService;

    private static ListeRessourcesDiffusables fileResult;

    @BeforeAll
    public static void setUp() throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        Resource xmlFile = new ClassPathResource("/xml/gar_ressourcesDiffusables_response.xml");

        String ressourceContent = null;
        try {
            ressourceContent = Files.asCharSource(new File(xmlFile.getURI()), Charset.forName("UTF-8")).read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(ressourceContent != null, "file in classPath 'gar_ressourcesDiffusables_response.xml' doesn't exist!");
        Assertions.assertTrue(!ressourceContent.isEmpty(), "file in classPath 'gar_ressourcesDiffusables_response.xml' is Empty!");
        log.debug("File content is : {}", Files.asCharSource(xmlFile.getFile(), Charset.forName("UTF-8")).read());

        TypeReference<ListeRessourcesDiffusables> typeRef = new TypeReference<ListeRessourcesDiffusables>(){};

        fileResult = xmlMapper.readValue(xmlFile.getFile(), typeRef);

        log.debug("XML File to object gives {}", fileResult);
    }

    @Test
    public void testRecuperation() throws Exception {
        ResponseEntity<List<RessourceDiffusable>> response = new ResponseEntity<List<RessourceDiffusable>>(fileResult.getRessourceDiffusable(), HttpStatus.ACCEPTED);

        Mockito.when(this.garRequestService.getRessourcesDiffusables()).thenReturn(fileResult);


        ListeRessourcesDiffusables listRs = this.garRequestService.getRessourcesDiffusables();

        Assertions.assertEquals(listRs, fileResult);
        log.debug("Test debug object of listRs {}", listRs.getRessourceDiffusable().get(1));
        assertThat(listRs.getRessourceDiffusable().get(1), org.hamcrest.Matchers.hasProperty("idRessource"));
        assertThat(listRs.getRessourceDiffusable().get(1), org.hamcrest.Matchers.hasProperty("nomRessource"));
        assertThat(listRs.getRessourceDiffusable().get(1), org.hamcrest.Matchers.hasProperty("idEditeur"));
        assertThat(listRs.getRessourceDiffusable().get(1), org.hamcrest.Matchers.hasProperty("nomEditeur"));
        assertThat(listRs.getRessourceDiffusable().get(1), org.hamcrest.Matchers.hasProperty("distributeursCom"));
        assertThat(listRs.getRessourceDiffusable().get(1), org.hamcrest.Matchers.hasProperty("distributeurTech"));
        assertThat(listRs.getRessourceDiffusable().get(1), org.hamcrest.Matchers.hasProperty("nomDistributeurTech"));
        assertThat(listRs.getRessourceDiffusable().get(1), org.hamcrest.Matchers.hasProperty("affichable"));
        assertThat(listRs.getRessourceDiffusable().get(1), org.hamcrest.Matchers.hasProperty("diffusable"));
    }
}
