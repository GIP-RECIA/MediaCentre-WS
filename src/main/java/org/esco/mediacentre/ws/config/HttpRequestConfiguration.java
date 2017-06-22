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

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.esco.mediacentre.ws.config.bean.HttpRequestTimeoutProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HttpRequestConfiguration {

	@Autowired
	private HttpRequestTimeoutProperties httpRequestTimeoutConfiguration;

	@Bean
	public RequestConfig requestConfig() {
		RequestConfig result = RequestConfig.custom()
				.setConnectionRequestTimeout(httpRequestTimeoutConfiguration.getConnectionRequestTimeout())
				.setConnectTimeout(httpRequestTimeoutConfiguration.getConectTimeout())
				.setSocketTimeout(httpRequestTimeoutConfiguration.getSocketTimeout())
				.build();
		return result;
	}

	/**
	 * Just for debug
	 */
	@PostConstruct
	public void debug() {
		log.info("RequestConfig: {}", requestConfig().toString());
	}
}
