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
package fr.recia.mediacentre.ws.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;
import fr.recia.mediacentre.ws.config.bean.HttpRequestTimeoutProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class HttpRequestConfiguration {

	private final HttpRequestTimeoutProperties httpRequestTimeoutConfiguration;

	@Bean
	public RequestConfig requestConfig() {
		RequestConfig result = buildRequestConfig();
		log.info("RequestConfig: {}", result);
		return result;
	}

	private RequestConfig buildRequestConfig() {
		return RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofMilliseconds(httpRequestTimeoutConfiguration.getConnectionRequestTimeout()))
				.setConnectTimeout(Timeout.ofMilliseconds(httpRequestTimeoutConfiguration.getConectTimeout()))
				.setResponseTimeout(Timeout.ofMilliseconds(httpRequestTimeoutConfiguration.getSocketTimeout()))
				.build();
	}
}
