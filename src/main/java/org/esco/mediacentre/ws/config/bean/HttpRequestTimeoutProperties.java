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

import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "requestConfig")
@Data
@Slf4j
public class HttpRequestTimeoutProperties {

	private Integer connectionRequestTimeout;
	private Integer conectTimeout;
	private Integer socketTimeout;

	private static final int CONNECTION_TIMEOUT_DEFAULT = 2000;
	private static final int CONNECT_TIMEOUT_DEFAULT = 2000;
	private static final int SOCKET_TIMEOUT_DEFAULT = 2000;

	/**
	 * @return the connectionRequestTimeout
	 */
	public Integer getConnectionRequestTimeout() {
		return connectionRequestTimeout != null ? connectionRequestTimeout : CONNECTION_TIMEOUT_DEFAULT;
	}

	/**
	 * @param connectionRequestTimeout the connectionRequestTimeout to set
	 */
	public void setConnectionRequestTimeout(final Integer connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

	/**
	 * @return the conectTimeout
	 */
	public Integer getConectTimeout() {
		return conectTimeout != null ? conectTimeout : CONNECT_TIMEOUT_DEFAULT;
	}

	/**
	 * @param conectTimeout the conectTimeout to set
	 */
	public void setConectTimeout(final Integer conectTimeout) {
		this.conectTimeout = conectTimeout;
	}

	/**
	 * @return the socketTimeout
	 */
	public Integer getSocketTimeout() {
		return socketTimeout != null ? socketTimeout : SOCKET_TIMEOUT_DEFAULT;
	}

	/**
	 * @param socketTimeout the socketTimeout to set
	 */
	public void setSocketTimeout(final Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	/**
	 * Just for debug
	 */
	@PostConstruct
	public void debug() {
		log.info("HttpRequestTimeoutConfiguration: {}", this.toString());
	}

}
