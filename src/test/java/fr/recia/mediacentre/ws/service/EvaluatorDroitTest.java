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


import com.google.common.collect.Lists;
import fr.recia.mediacentre.ws.model.ressource.FiltreDroit;
import fr.recia.mediacentre.ws.model.ressource.FiltreDroitOnPattern;
import fr.recia.mediacentre.ws.model.ressource.OperatorDroit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by jgribonvald on 12/06/17.
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
@Slf4j
@SpringBootTest
public class EvaluatorDroitTest {

    private EvaluatorDroit evaluatorDroit = new EvaluatorDroit();

    private static Map<String,List<String>> userInfos = new HashMap<>();

    @BeforeAll
    public static void setUp() {
        userInfos.put("uid", Lists.newArrayList("F01000ugr"));
        userInfos.put("ESCOUAI", Lists.newArrayList("0450822X", "0377777U"));
        userInfos.put("ESCOUAICourant", Lists.newArrayList("0450822X"));
        userInfos.put("ENTPersonProfils", Lists.newArrayList("National_ENS"));
        userInfos.put("isMemberOf", Lists.newArrayList("esco:Etablissements:FICTIF_0450822X:Tous_FICTIF"));
    }


    @Test
    public void testOR() {
        FiltreDroit droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.OR);
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0281444Z"));
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0450822X"));
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0377777U"));
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(true));

        droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.OR);
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0450822X"));
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(true));

        droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.OR);
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0377777U"));
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(true));

        droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.OR);
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0281444Z"));
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(false));

        droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.OR);
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0281444Z"));
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0411111A"));
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0366666Z"));
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(false));
    }

    @Test
    public void testEmpty() throws InterruptedException {
        FiltreDroit droit = new FiltreDroit();
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(true));

        droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.OR);
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(true));

        droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.AND);
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(true));

    }

    @Test
    public void testAND() throws InterruptedException {
        FiltreDroit droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.AND);
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0450822X"));
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(true));

        droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.AND);
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0450822X"));
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0377777U"));
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(true));

        droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.AND);
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0450822X"));
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0411111A"));
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(false));

        droit = new FiltreDroit();
        droit.setOperator(OperatorDroit.AND);
        droit.getFiltreDroitType().add(new FiltreDroitOnPattern("ESCOUAI", "0411111A"));
        assertThat(evaluatorDroit.evaluate(droit, userInfos), is(false));

    }
}
