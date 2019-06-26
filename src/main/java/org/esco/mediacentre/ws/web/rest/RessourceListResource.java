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
package org.esco.mediacentre.ws.web.rest;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.model.ressource.Ressource;
import org.esco.mediacentre.ws.service.IRemoteRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class RessourceListResource {

	@Autowired
	List<IRemoteRequestService> remoteServices;

	@RequestMapping(value = "/ressources", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Ressource> getRessources(@RequestBody Map<String, List<String>> userInfos) {
		log.debug("Requesting ressources for user {}", userInfos);
		List<Ressource> ressources = Lists.newArrayList();
		for (IRemoteRequestService remote : remoteServices) {
			ressources.addAll(remote.getRessources(userInfos));
		}
		log.trace("Returning for user {} these ressources {}", userInfos, ressources);
		return ressources;
	}

	@RequestMapping(value = "/ressourcesDiffusables", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Ressource> getRessourcesDiffusablesGAR(@RequestBody Map<String, List<String>> userInfos) {
		log.debug("Requesting ressources Diffusables GAR {}", userInfos);
		List<Ressource> ressources = Lists.newArrayList();
		for (IRemoteRequestService remote : remoteServices) {
			ressources.addAll(remote.getRessources(userInfos));
		}
		log.trace("Returning these ressources {}", ressources);
		return ressources;
	}
}
