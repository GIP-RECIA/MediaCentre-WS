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


import fr.recia.mediacentre.ws.config.bean.LocalRessourceProperties;
import fr.recia.mediacentre.ws.model.ressource.LocalRessource;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by jgribonvald on 12/06/17.
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
@Slf4j
@SpringBootTest
public class InternalRessourceLoaderTest {

    private static LocalRessourceLoader loader;
    private static LocalRessourceProperties ressourceProperties;
    private static final long refreshTime = 1000;

    @Autowired
    private LocalRessourceLoader injectedLoader;

    @Autowired
    private LocalRessourceProperties injectedRessourcesConfiguration;

    @BeforeEach
    public void setUp(){
        loader = new LocalRessourceLoader();
        ressourceProperties = new LocalRessourceProperties();
        ressourceProperties.setRefreshTimeOfPathDirectory(refreshTime);
        ressourceProperties.setRessourcePathDirectory(InternalRessourceLoaderTest.class.getResource("").getPath());
        loader.setLocalRSConfiguration(ressourceProperties);
    }

    @Test
    public void testRecuperation() {
        List<LocalRessource> ressources = injectedLoader.getLocalRessources();

        log.debug("Test debug object of ressources {}", ressources);
        assertThat(ressources, anything());
        assertThat(ressources, hasSize(2));
    }

    @Test
    public void testEmptyRecuperation() throws InterruptedException {
        List<LocalRessource> ressources = loader.getLocalRessources();

        log.debug("Test debug object of ressources {}", ressources);
        assertThat(ressources, anything());
        assertThat(ressources, hasSize(0));
    }

    @Test
    public void testEmptyLoaderChangeRecuperation() throws InterruptedException {
        List<LocalRessource> ressources = loader.getLocalRessources();
        assertThat(ressources, anything());
        assertThat(ressources, hasSize(0));
        TimeUnit.MILLISECONDS.sleep(refreshTime + 1);
        loader.setLocalRSConfiguration(this.injectedRessourcesConfiguration);
        ressources = loader.getLocalRessources();
        log.debug("Test debug object of ressources {}", ressources);
        assertThat(ressources, anything());
        assertThat(ressources, hasSize(2));

        log.debug("Test on getting empty list of resources !");
        loader.setLocalRSConfiguration(ressourceProperties);
        TimeUnit.MILLISECONDS.sleep(this.injectedRessourcesConfiguration.getRefreshTimeOfPathDirectory() + 1);
        ressources = loader.getLocalRessources();
        log.debug("Test debug object of ressources {}", ressources);
        assertThat(ressources, anything());
        assertThat(ressources, hasSize(0));
    }
}
