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

import javax.net.ssl.SSLContext;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.ssl.SSLContexts;
import fr.recia.mediacentre.ws.config.bean.DefaultHttpClientProperties;
import fr.recia.mediacentre.ws.config.bean.StructureInfosRestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("!WITHOUT_STRUCT_REST")
@Slf4j
public class StructureRestClientConfiguration {

	@Autowired
	private DefaultHttpClientProperties defaultProperties;

	@Autowired
	private RequestConfig requestConfig;

	@Autowired
	@Qualifier(value = "StructureRestTemplate")
	@Lazy
	@Getter
	private RestTemplate restTemplate;

	@Autowired
	@Getter
	private StructureInfosRestProperties restProperties;

	@Bean
	public PoolingHttpClientConnectionManager StructureConnectionManager(Registry<ConnectionSocketFactory> registry) {

		PoolingHttpClientConnectionManager ccm = new PoolingHttpClientConnectionManager(registry);
		ccm.setMaxTotal(defaultProperties.getMaxTotal());
		// Default max per route is used in case it's not set for a specific route
		ccm.setDefaultMaxPerRoute(defaultProperties.getDefaultMaxPerRoute());

		final HttpHost host = new HttpHost(restProperties.getHostConfig().getScheme(), restProperties
				.getHostConfig().getHost(), restProperties.getHostConfig().getPort());
		// Max per route for a specific host route
		ccm.setMaxPerRoute(new HttpRoute(host), restProperties.getHostConfig().getMaxPerRoute());
		//ccm.setValidateAfterInactivity(250000);
		return ccm;
	}

	@Bean
	public ClientHttpRequestFactory StructureHttpRequestFactory() {
		return new HttpComponentsClientHttpRequestFactory(StructureHttpClient());
	}

	@Bean
	public CloseableHttpClient StructureHttpClient() {
		// For details see : https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/connmgmt.html#d5e371
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();


			SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext,
					new NoopHostnameVerifier());

			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("https", sslConnectionFactory).register("http", new PlainConnectionSocketFactory())
					.build();

			return HttpClientBuilder.create().setConnectionManagerShared(true)
					.setConnectionManager(StructureConnectionManager(registry))
					.setDefaultRequestConfig(requestConfig).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Could not create HttpClient ! {}", e.getMessage(), e);
			return null;
		}
	}

	@Bean(name = "StructureRestTemplate")
	public RestTemplate StructureRestTemplate() {
		return new RestTemplate(StructureHttpRequestFactory());
	}
}
