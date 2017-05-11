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
package org.esco.mediacentre.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class WsRessourcesApplication extends SpringBootServletInitializer {

	private static final Logger log = LoggerFactory.getLogger(WsRessourcesApplication.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.profiles(addDefaultProfile()).sources(WsRessourcesApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WsRessourcesApplication.class);

		SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);

		addDefaultProfile(app, source);
		Environment env = app.run(args).getEnvironment();

		log.info("App config run with :\n\t" + "args: {}\n\t" + "env: {}\n\t", args, env.toString());
	}

	/**
	 * Set a default profile if it has not been set
	 */
	private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
		if (!source.containsProperty("spring.profiles.active")
				&& !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {
			log.debug("Adding additional profile {}", "dev");
			app.setAdditionalProfiles("dev");
		}
	}

	private String addDefaultProfile() {
		String profile = System.getProperty("spring.profiles.active");
		if (profile != null) {
			log.info("Running with Spring profile(s) : {}", profile);
			return profile;
		}

		log.warn("No Spring profile configured, running with default configuration");
		return "dev";
	}

}
