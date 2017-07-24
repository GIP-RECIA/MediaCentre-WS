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
package org.esco.mediacentre.ws.service;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.model.structure.Structure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jgribonvald on 12/06/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"WITHOUT_STRUCT_REST", "test"})
@Slf4j
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//        classes = WsRessourcesApplication.class)
public class StructureRestClientTest {

    @Mock
    private StructureInfoRequestService structureInfoRequestService;

    private static final String ids = "0450822X,0377777U";

    private HashMap<String, Structure> fileResult;

    @Before
    public void setUp() throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        Resource jsonFile = new ClassPathResource("response-etabs.json");

        String ressourceContent = null;
        try {
            ressourceContent = Files.toString(new File(jsonFile.getURI()), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue("file in classPath 'response-etabs.json' doesn't exist!", ressourceContent != null);
        Assert.assertTrue("file in classPath 'response-etabs.json' is Empty!", !ressourceContent.isEmpty());
        log.debug("File content is : {}", Files.toString(jsonFile.getFile(), Charset.forName("UTF-8")));

        TypeReference<HashMap<String, Structure>> typeRef = new TypeReference<HashMap<String,Structure>>(){};

        fileResult = jsonMapper.readValue(jsonFile.getFile(), typeRef);

        log.debug("Json File to object gives : keys {}, objects {}", fileResult.keySet(), fileResult.values());
    }

    @Test
    public void testRecuperation() throws Exception {
        Set<String> idList = Sets.newHashSet(ids.split(","));

        ResponseEntity<HashMap<String, Structure>> response = new ResponseEntity<HashMap<String, Structure>>(fileResult, HttpStatus.ACCEPTED);

        Mockito.when(this.structureInfoRequestService.getStructuresInfos(
                Matchers.anySet())).thenReturn(fileResult);


        Map<String, Structure> mapStructs = this.structureInfoRequestService.getStructuresInfos(idList);

        Assert.assertEquals(mapStructs.keySet(), idList);
        log.debug("Test debug object of mapStruct {}", Lists.newArrayList(mapStructs.values()).get(1));
        assertThat(Lists.newArrayList(mapStructs.values()).get(1), org.hamcrest.Matchers.hasProperty("id"));
        assertThat(Lists.newArrayList(mapStructs.values()).get(1), org.hamcrest.Matchers.hasProperty("displayName"));
        assertThat(Lists.newArrayList(mapStructs.values()).get(1), org.hamcrest.Matchers.hasProperty("code"));
    }
}
