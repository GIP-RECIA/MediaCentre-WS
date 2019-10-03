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

import java.util.List;

import javax.annotation.PostConstruct;

import fr.recia.mediacentre.ws.config.bean.LocalRessourceProperties;
import fr.recia.mediacentre.ws.service.IRemoteRequestService;
import fr.recia.mediacentre.ws.service.IStructureInfoService;
import fr.recia.mediacentre.ws.service.InternalFileServiceImpl;
import fr.recia.mediacentre.ws.service.LocalRessourceLoader;
import fr.recia.mediacentre.ws.service.MockedRequestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import fr.recia.mediacentre.ws.service.GARRequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RemoteRequestServiceConfiguration {

	@Autowired(required = false)
	private GARClientConfiguration garClientConfiguration;

	@Autowired(required = false)
	private LocalRessourceProperties localRessourceProperties;

	@Autowired
	private IStructureInfoService structureInfoService;

	@Bean
	@ConditionalOnBean(name = "GARClientConfiguration")
	public IRemoteRequestService getGARRequestService() {
		GARRequestServiceImpl rs = new GARRequestServiceImpl();
		rs.setGarConfiguration(garClientConfiguration.getGARProperties());
		rs.setRemoteAccessTemplate(garClientConfiguration.GARRestTemplate());
		rs.setStructureInfoService(structureInfoService);
		rs.setHttpHeaders(garClientConfiguration.GARHttpRequestHeaders());
		return rs;
	}

	@Bean
	@ConditionalOnMissingBean(name="GARClientConfiguration")
	public IRemoteRequestService mockedWithStructRestRequestService() {
		MockedRequestServiceImpl mockedService = new MockedRequestServiceImpl();
		mockedService.setStructureInfoService(structureInfoService);
		return mockedService;
	}

	@Bean
	public LocalRessourceLoader getLocalRessourceLoader() {
		LocalRessourceLoader loader = new LocalRessourceLoader();
		loader.setLocalRSConfiguration(localRessourceProperties);
		return loader;
	}

	@Bean
	public IRemoteRequestService getLocalRessourceService() {
		InternalFileServiceImpl rs = new InternalFileServiceImpl();
		rs.setLocalRSConfiguration(localRessourceProperties);
		rs.setStructureInfoService(structureInfoService);
		rs.setLoader(getLocalRessourceLoader());
		return rs;
	}

	@Autowired
	List<IRemoteRequestService> remoteServices;

	@PostConstruct
	public void debug() {
		log.debug("Liste des Remote Service configurées {}", remoteServices);
	}

}
