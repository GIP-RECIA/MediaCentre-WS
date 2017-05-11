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

import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.config.bean.AppSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private AppSecurityProperties securityProperties;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		String hasIpAddress = "hasIpAddress('127.0.0.1') or hasIpAddress('::1')";
		for (String ip: securityProperties.getAuthorizedIPAccess()) {
			hasIpAddress += " or hasIpAddress('" + ip + "')";
		}

		log.debug("WebSecurity configuration: autorize access on '/api/**' for {}", hasIpAddress);

		http.csrf().disable().exceptionHandling().and().authorizeRequests().antMatchers("/**").access(hasIpAddress)
				.and().httpBasic().disable();

	}
}
