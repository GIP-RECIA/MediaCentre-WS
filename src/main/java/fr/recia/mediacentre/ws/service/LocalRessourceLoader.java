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

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.mediacentre.ws.config.bean.LocalRessourceProperties;
import fr.recia.mediacentre.ws.model.ressource.ListeLocaleRessources;
import fr.recia.mediacentre.ws.model.ressource.ListeLocaleRessourcesWrapper;
import fr.recia.mediacentre.ws.model.ressource.LocalRessource;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Slf4j
public class LocalRessourceLoader implements InitializingBean {

    @Setter
    private LocalRessourceProperties localRSConfiguration;

    private List<LocalRessource> lastLoadedRessources = new ArrayList<>();

    private LocalDateTime nextRefresh;

    public List<LocalRessource> getLocalRessources() {
        log.debug("Loader properties: nextRefresh {}, actual date {}", nextRefresh, LocalDateTime.now());
        if (nextRefresh == null || LocalDateTime.now().isAfter(nextRefresh)) {
            refreshRessources();
        }
        return lastLoadedRessources;
    }

    private void refreshRessources() {
        List<LocalRessource> ressourceList = new ArrayList<>();
        try {
            Files.newDirectoryStream(Paths.get(localRSConfiguration.getRessourcePathDirectory()),
                    path -> path.toString().endsWith(".json"))
                    .forEach(filePath -> ressourceList.addAll(loadLocalRessourceFile(filePath)));
        } catch (IOException e) {
            log.error("Impossible de lire le contenu du répertoire {}", localRSConfiguration.getRessourcePathDirectory(), e);
        }
        lastLoadedRessources = ressourceList;
        nextRefresh = LocalDateTime.now().plus(localRSConfiguration.getRefreshTimeOfPathDirectory(), ChronoUnit.MILLIS);
        log.debug("Refresh of resources happened after {} and provided the result \n {}", nextRefresh, lastLoadedRessources);
    }

    private List<LocalRessource> loadLocalRessourceFile(Path filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            final ListeLocaleRessourcesWrapper ressources = objectMapper.readValue(filePath.toFile(), ListeLocaleRessourcesWrapper.class);
            return ressources != null && ressources.getListeRessources() != null ? ressources.getListeRessources().getRessource() : new ArrayList<LocalRessource>() ;
        } catch (IOException e) {
            log.error("Le fichier '{}' ne respecte pas le format json prévu !", filePath.toString(), e);
        }
        return new ArrayList<LocalRessource>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.localRSConfiguration, "The local resource configuration bean wasn't set !");
    }
}
