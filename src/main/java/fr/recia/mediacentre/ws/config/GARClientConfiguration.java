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

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Date;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.validation.constraints.NotNull;

import fr.recia.mediacentre.ws.config.bean.DefaultHttpClientProperties;
import fr.recia.mediacentre.ws.config.bean.ParamValueProperty;
import fr.recia.mediacentre.ws.config.bean.RessourceProperties;
import fr.recia.mediacentre.ws.config.bean.RessourceType;
import fr.recia.mediacentre.ws.config.bean.RessourcesProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
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

	@Bean
	public RestTemplate GARRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate(GARHttpRequestFactory());
		return restTemplate;
	}

    @Bean
    public HttpHeaders GARHttpRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        for (ParamValueProperty prop : getGARProperties().getHeaders()) {
            headers.add(prop.getParam(), prop.getValue());
        }
        headers.add("Date", new Date().toString());
        return headers;
    }

	@Bean
	public CloseableHttpClient GARHttpClient() {
		// For details see : https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/connmgmt.html#d5e371
		try {
			SSLContext sslContext;
			KeyStore clientKS;
			if (defaultProperties.getKeyStorePath() != null && defaultProperties.getKeyStorePassword() != null) {
				if (defaultProperties.getKeyStoreType() != null) {
					clientKS = KeyStore.getInstance(defaultProperties.getKeyStoreType());
				} else {
					clientKS = KeyStore.getInstance(KeyStore.getDefaultType());
				}
				clientKS.load(new FileInputStream(defaultProperties.getKeyStorePath()), defaultProperties
						.getKeyStorePassword().toCharArray());
				if (log.isDebugEnabled()) {
					log.debug("Loaded defined keystore file {} with size of {}", defaultProperties.getKeyStorePath(), clientKS.size());
					if (clientKS.size() > 1) {
						Enumeration<String> enumeration = clientKS.aliases();
						while (enumeration.hasMoreElements()) {
							final String alias = enumeration.nextElement();
							log.debug("Showing certificates with alias {} and \ncertificate {} and \nkey {}", alias, clientKS.getCertificate(alias),
									clientKS.getKey(alias, defaultProperties.getKeyStorePassword().toCharArray()));
						}
					}
				}

				PrivateKeyStrategy privateKeyStrategy = null;
				if (getGARProperties().getClientKeyAlias() != null) {
					// Unconditionally return our configured alias allowing the consumer
					// to throw an appropriate exception rather than trying to generate our
					// own here if our configured alias is not a key in the aliases map.
					privateKeyStrategy = (aliases, socket) -> getGARProperties().getClientKeyAlias();
				}

				sslContext = SSLContexts
						.custom()
						.loadKeyMaterial(new File(defaultProperties.getKeyStorePath()), defaultProperties.getKeyStorePassword().toCharArray(),
								defaultProperties.getKeyStorePassword().toCharArray(), privateKeyStrategy)
//						.loadTrustMaterial(new File(defaultProperties.getKeyStorePath()), defaultProperties
//								.getKeyStorePassword().toCharArray(), new TrustSelfSignedStrategy())
						.build();
			} else {
				sslContext = SSLContexts.createSystemDefault();
			}

			SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext,
					new DefaultHostnameVerifier());

			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("https", sslConnectionFactory)
					.build();

			return HttpClientBuilder.create().setConnectionManagerShared(true)
					.setConnectionManager(GARConnectionManager(registry)).setSSLSocketFactory(sslConnectionFactory)
					.setDefaultRequestConfig(requestConfig).build();
		} catch (Exception e) {
			log.error("Could not create HttpClient ! {}", e.getMessage(), e);
			return null;
		}
	}

	@PostConstruct
	public void debug() {
		log.debug("GAR Configuration {}", getGARProperties());
	}

}
