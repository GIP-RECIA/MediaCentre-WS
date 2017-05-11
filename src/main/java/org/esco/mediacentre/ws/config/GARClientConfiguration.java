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

import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.PrivateKeyDetails;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContexts;
import org.esco.mediacentre.ws.config.bean.DefaultHttpClientProperties;
import org.esco.mediacentre.ws.config.bean.ParamValueProperty;
import org.esco.mediacentre.ws.config.bean.RessourceProperties;
import org.esco.mediacentre.ws.config.bean.RessourceType;
import org.esco.mediacentre.ws.config.bean.RessourcesProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("!WITHOUT_GAR")
@Slf4j
public class GARClientConfiguration {

	@Autowired
	private DefaultHttpClientProperties defaultProperties;

	@Autowired
	private RessourcesProperties ressourcesConfiguration;

	@Autowired
	private RequestConfig requestConfig;

	@Bean(name = "GARRessourceProperties")
	public @NotNull RessourceProperties getGARProperties() {
		return ressourcesConfiguration.getConfigurations().get(RessourceType.GAR);
	}

	@Bean
	public PoolingHttpClientConnectionManager GARConnectionManager(Registry<ConnectionSocketFactory> registry) {

		PoolingHttpClientConnectionManager ccm = new PoolingHttpClientConnectionManager(registry);
		ccm.setMaxTotal(defaultProperties.getMaxTotal());
		// Default max per route is used in case it's not set for a specific route
		ccm.setDefaultMaxPerRoute(defaultProperties.getDefaultMaxPerRoute());

		final HttpHost host = new HttpHost(getGARProperties().getHostConfig().getHost(), getGARProperties()
				.getHostConfig().getPort(), getGARProperties().getHostConfig().getScheme());
		// Max per route for a specific host route
		ccm.setMaxPerRoute(new HttpRoute(host), getGARProperties().getHostConfig().getMaxPerRoute());

		//ccm.setValidateAfterInactivity(250000);
		return ccm;
	}

	@Bean
	public ClientHttpRequestFactory GARHttpRequestFactory() {
		return new HttpComponentsClientHttpRequestFactory(GARHttpClient());
	}

	@Bean(name = "GARRestTemplate")
	public RestTemplate GARRestTemplate() {
		RestTemplate restTemplate = new RestTemplate(GARHttpRequestFactory());
		return restTemplate;
	}

	@Bean
	public CloseableHttpClient GARHttpClient() {
		// For details see : https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/connmgmt.html#d5e371
		try {
			SSLContext sslContext;
			KeyStore clientKS = null;
			if (defaultProperties.getKeyStorePath() != null && defaultProperties.getKeyStorePassword() != null) {
				clientKS = KeyStore.getInstance(KeyStore.getDefaultType());
				clientKS.load(new FileInputStream(defaultProperties.getKeyStorePath()), defaultProperties
						.getKeyStorePassword().toCharArray());

				sslContext = SSLContexts
						.custom()
						.loadKeyMaterial(clientKS, defaultProperties.getKeyStorePassword().toCharArray(),
								new PrivateKeyStrategy() {
									@Override
									public String chooseAlias(Map<String, PrivateKeyDetails> aliases, Socket socket) {
										// TODO Auto-generated method stub
										return getGARProperties().getClientKeyAlias();
									}
								}).loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			} else {
				sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			}

			SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext,
					new DefaultHostnameVerifier());

			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("https", sslConnectionFactory).register("http", new PlainConnectionSocketFactory())
					.build();

			List<Header> headers = new ArrayList<Header>();
			for (ParamValueProperty prop : getGARProperties().getHeaders()) {
				headers.add(new BasicHeader(prop.getParam(), prop.getValue()));
			}

			return HttpClientBuilder.create().setConnectionManagerShared(true)
					.setConnectionManager(GARConnectionManager(registry)).setSSLSocketFactory(sslConnectionFactory)
					.setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Could not create HttpClient ! {}", e.getMessage(), e);
			return null;
		}
	}

	@PostConstruct
	public void debug() {
		log.debug("GAR Configuration {}", getGARProperties());
	}

}
