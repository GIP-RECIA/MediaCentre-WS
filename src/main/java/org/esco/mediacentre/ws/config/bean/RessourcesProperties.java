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
package org.esco.mediacentre.ws.config.bean;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "ressources")
@Validated
@Data
@Slf4j
public class RessourcesProperties {

	@NotNull
	private Map<RessourceType, RessourceProperties> configurations = new HashMap<RessourceType, RessourceProperties>();

	@PostConstruct
	public void debug() {
		log.debug("Liste des ressources configurées {}", configurations);
	}
}
