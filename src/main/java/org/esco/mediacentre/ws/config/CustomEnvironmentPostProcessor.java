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
package org.esco.mediacentre.ws.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor, ApplicationListener<ApplicationEvent> {

    private static final DeferredLog log = new DeferredLog();

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        log.info("Try to load custom yaml configuration file from environment property 'MEDIACENTRE_WS_CONFIG_FILE'");
        String confFile;
        if (Lists.newArrayList(environment.getActiveProfiles()).contains("test")) {
            confFile = environment.getProperty("MEDIACENTRE_WS_CONFIG_FILE", String.class);
        } else {
            confFile = environment.getRequiredProperty("MEDIACENTRE_WS_CONFIG_FILE", String.class);
        }
        if (confFile != null) {
            log.info("Loading custom yaml configuration file '" + confFile + "'");
            Resource path = new FileSystemResource(confFile);
            PropertySource<?> propertySource = loadYaml(path);
            environment.getPropertySources().addFirst(propertySource);
        }
    }

    private PropertySource<?> loadYaml(Resource path) {
        if (!path.exists()) {
            throw new IllegalArgumentException("Resource " + path + " does not exist");
        }
        try {
            return this.loader.load("custom-resource: [" + path.getDescription() + "]", path).get(0);
        }
        catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed to load yaml configuration from " + path, ex);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.replayTo(CustomEnvironmentPostProcessor.class);
    }

}