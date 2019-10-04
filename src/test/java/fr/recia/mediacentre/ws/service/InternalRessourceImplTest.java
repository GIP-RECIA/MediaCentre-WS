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
import fr.recia.mediacentre.ws.config.bean.LocalRessourceProperties;
import fr.recia.mediacentre.ws.model.ressource.LocalRessource;
import fr.recia.mediacentre.ws.model.ressource.Ressource;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.hamcrest.text.IsEmptyString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jgribonvald on 12/06/17.
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"WITHOUT_STRUCT_REST","test"})
@Slf4j
@SpringBootTest
public class InternalRessourceImplTest {

    private InternalFileServiceImpl internalFileService;

    @Autowired
    private LocalRessourceLoader injectedLoader;

    @Autowired
    private LocalRessourceProperties injectedRessourcesConfiguration;

    @Autowired
    private IStructureInfoService structureInfoService;

    Map<String,List<String>> userInfos = new HashMap<>();
    Map<String,List<String>> userInfosWithoutRS = new HashMap<>();
    Map<String,List<String>> userInfosWithOneRS = new HashMap<>();

    @BeforeEach
    public void setUp() {
        this.internalFileService = new InternalFileServiceImpl();
        this.internalFileService.setLoader(injectedLoader);
        this.internalFileService.setLocalRSConfiguration(injectedRessourcesConfiguration);
        this.internalFileService.setStructureInfoService(structureInfoService);

        userInfos.put("uid",  Lists.newArrayList("F01000ugr"));
        userInfos.put("ESCOUAI", Lists.newArrayList("0450822X","0377777U"));
        userInfos.put("ESCOUAICourant", Lists.newArrayList("0450822X"));
        userInfos.put("ENTPersonProfils", Lists.newArrayList("National_ENS"));
        userInfos.put("isMemberOf", Lists.newArrayList(
                "esco:Etablissements:FICTIF_0450822X:Enseignements:ACCOMPAGNEMENT PERSONNALISE",
                "esco:Etablissements:FICTIF_0450822X:Enseignements:ETUDE DES CONSTRUCTIONS",
                "esco:Etablissements:FICTIF_0450822X:Enseignements:SCIENCES DE L INGENIEUR",
                "esco:Etablissements:FICTIF_0450822X:groupes_locaux:espaces_etablissement:mecanique",
                "esco:Etablissements:FICTIF_0450822X:groupes_locaux:MonStageEnLigne",
                "esco:Etablissements:FICTIF_0450822X:groupes_locaux:Univ",
                "esco:Etablissements:FICTIF_0450822X:Profs",
                "esco:Etablissements:FICTIF_0450822X:TERMINALE GENERALE et TECHNO YC BT:Profs_703",
                "esco:Etablissements:FICTIF_0450822X:TERMINALE GENERALE et TECHNO YC BT:Profs_704",
                "esco:Etablissements:FICTIF_0450822X:TERMINALE GENERALE et TECHNO YC BT:Profs_705",
                "esco:Etablissements:FICTIF_0450822X:TERMINALE GENERALE et TECHNO YC BT:Profs_706",
                "esco:Etablissements:FICTIF_0450822X:Tous_FICTIF",
                "clg37:Etablissements:FICTIF37_0377777U:Enseignements:SCIENCES",
                "clg37:Etablissements:FICTIF37_0377777U:Tous_FICTIF",
                "esco:Applications:GRR:FICTIF_0450822X",
                "esco:Applications:Cahier_de_texte:FICTIF_0450822X",
                "esco:Applications:Espace_Moodle:FICTIF_0450822X",
                "esco:Applications:Esidoc:FICTIF_0450822X",
                "esco:Applications:Espaces_stockage:SSO_samba:FICTIF_0450822X",
                "esco:Applications:Indicateurs:FICTIF_0450822X",
                "esco:Applications:Listes_Diffusion:FICTIF_0450822X",
                "esco:Applications:Pearltrees:FICTIF_0450822X",
                "esco:Applications:Pronote:FICTIF_0450822X",
                "esco:Applications:Publication_contenus:FICTIF_0450822X",
                "esco:Applications:Ressources_Editoriales:mondesk:FICTIF_0450822X",
                "esco:Applications:Ressources_Editoriales:generation_5:FICTIF_0450822X",
                "esco:Applications:SACoche:FICTIF_0450822X",
                "clg37:Applications:Indicateurs:FICTIF37_0377777U",
                "clg37:Applications:Espace_Moodle:FICTIF37_0377777U",
                "clg37:Applications:Listes_Diffusion:FICTIF37_0377777U",
                "clg37:Applications:Publication_contenus:FICTIF37_0377777U",
                "esco:admin:local:admin_FICTIF_0450822X",
                "esco:admin:GRR:local:FICTIF_0450822X",
                "esco:admin:Moodle:local:FICTIF_0450822X",
                "esco:admin:sarapis:local:sarapis_FICTIF_0450822X",
                "esco:admin:CAHIER_TEXTE:local:FICTIF_0450822X",
                "esco:admin:Listes_Diffusion:local:FICTIF_0450822X",
                "esco:admin:Publication_contenus:FICTIF_0450822X:MANAGER",
                "esco:admin:Publication_contenus:FICTIF_0450822X:LOOKOVER",
                "clg37:admin:Listes_Diffusion:local:FICTIF37_0377777U",
                "clg37:admin:local:admin_FICTIF37_0377777U",
                "clg37:admin:sarapis:local:sarapis_FICTIF37_0377777U",
                "clg37:admin:Publication_contenus:FICTIF37_0377777U:MANAGER",
                "esco:Inter_etablissements:Manuels_numeriques:Local"));

        userInfosWithOneRS.put("uid",  Lists.newArrayList("F01000ugr"));
        userInfosWithOneRS.put("ESCOUAI", Lists.newArrayList("0450822X","0377777U"));
        userInfosWithOneRS.put("ESCOUAICourant", Lists.newArrayList("0450822X"));
        userInfosWithOneRS.put("ENTPersonProfils", Lists.newArrayList("National_ENS"));
        userInfosWithOneRS.put("isMemberOf", Lists.newArrayList("esco:Etablissements:FICTIF_0450822X:Tous_FICTIF"));

        userInfosWithoutRS.put("uid",  Lists.newArrayList("F01000ugr"));
        userInfosWithoutRS.put("ESCOUAI", Lists.newArrayList("0450822X","0377777U"));
        userInfosWithoutRS.put("ESCOUAICourant", Lists.newArrayList("0450822X"));
        userInfosWithoutRS.put("ENTPersonProfils", Lists.newArrayList("National_ENS"));
        userInfosWithoutRS.put("isMemberOf", Lists.newArrayList("clg37:admin:local:admin_FICTIF37_0377777U"));
    }

    @Test
    public void testWithAllRS() {
        List<Ressource> ressources =  this.internalFileService.getRessources(userInfos);

        log.info("ressource 0: {}", ((LocalRessource)ressources.get(0)).getFiltreDroit());
        log.info("ressource 1: {}", ((LocalRessource)ressources.get(1)).getFiltreDroit());

        assertThat(ressources, Matchers.hasSize(2));
        
        assertThat(ressources.get(1), Matchers.hasProperty("idRessource"));
        assertThat(ressources.get(1).getIdRessource(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("nomRessource"));
        assertThat(ressources.get(1).getNomRessource(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("idEditeur"));
        assertThat(ressources.get(1).getIdEditeur(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("nomEditeur"));
        assertThat(ressources.get(1).getNomEditeur(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("distributeurTech"));
        assertThat(ressources.get(1).getDistributeurTech(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("idType"));
        assertThat(ressources.get(1).getIdType(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("urlAccesRessource"));
        assertThat(ressources.get(1).getUrlAccesRessource(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("urlSourceEtiquette"));
        assertThat(ressources.get(1).getUrlSourceEtiquette(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("urlVignette"));
        assertThat(ressources.get(1).getUrlVignette(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("description"));
        assertThat(ressources.get(1).getDescription(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1), Matchers.hasProperty("idEtablissement"));
        assertThat(ressources.get(1).getIdEtablissement(), Matchers.hasSize(2));
        assertThat(ressources.get(1).getIdEtablissement().get(0), Matchers.hasProperty("UAI"));
        assertThat(ressources.get(1).getIdEtablissement().get(0).getUAI(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(ressources.get(1).getIdEtablissement().get(0), Matchers.hasProperty("nom"));
        assertThat(ressources.get(1).getIdEtablissement().get(0).getNom(), Matchers.not(IsEmptyString.emptyOrNullString()));

        assertThat((LocalRessource)ressources.get(0), Matchers.hasProperty("filtreDroit"));
        assertThat(((LocalRessource)ressources.get(0)).getFiltreDroit(), Matchers.hasProperty("operator"));
        assertThat(((LocalRessource)ressources.get(0)).getFiltreDroit().getOperator(),  Matchers.not(Matchers.nullValue()));
        assertThat(((LocalRessource)ressources.get(0)).getFiltreDroit(), Matchers.hasProperty("filtreDroitType"));
        assertThat(((LocalRessource)ressources.get(0)).getFiltreDroit().getFiltreDroitType(), Matchers.hasSize(1));
        assertThat(((LocalRessource)ressources.get(0)).getFiltreDroit().getFiltreDroitType().get(0), Matchers.hasProperty("attribut"));
        assertThat(((LocalRessource)ressources.get(0)).getFiltreDroit().getFiltreDroitType().get(0).getAttribut(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(((LocalRessource)ressources.get(0)).getFiltreDroit().getFiltreDroitType().get(0), Matchers.hasProperty("pattern"));
        assertThat(((LocalRessource)ressources.get(0)).getFiltreDroit().getFiltreDroitType().get(0).getPattern(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat((LocalRessource)ressources.get(0), Matchers.hasProperty("extractEtablissement"));
        assertThat(((LocalRessource)ressources.get(0)).getExtractEtablissement(), Matchers.hasSize(1));
        assertThat(((LocalRessource)ressources.get(0)).getExtractEtablissement().get(0), Matchers.hasProperty("attribut"));
        assertThat(((LocalRessource)ressources.get(0)).getExtractEtablissement().get(0).getAttribut(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(((LocalRessource)ressources.get(0)).getExtractEtablissement().get(0), Matchers.hasProperty("matcher"));
        assertThat(((LocalRessource)ressources.get(0)).getExtractEtablissement().get(0).getAttribut(), Matchers.not(IsEmptyString.emptyOrNullString()));
        assertThat(((LocalRessource)ressources.get(0)).getExtractEtablissement().get(0), Matchers.hasProperty("group"));
        assertThat(((LocalRessource)ressources.get(0)).getExtractEtablissement().get(0).getAttribut(), Matchers.not(IsEmptyString.emptyOrNullString()));
    }

    @Test
    public void testWithOneRS() {
        List<Ressource> ressources =  this.internalFileService.getRessources(userInfosWithOneRS);

        assertThat(ressources, Matchers.hasSize(1));
    }

    @Test
    public void testWithoutRS() {
        List<Ressource> ressources =  this.internalFileService.getRessources(userInfosWithoutRS);

        assertThat(ressources, Matchers.hasSize(0));
    }
}
