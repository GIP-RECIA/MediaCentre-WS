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
package org.esco.mediacentre.ws.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.config.bean.ParamValueProperty;
import org.esco.mediacentre.ws.config.bean.RessourceProperties;
import org.esco.mediacentre.ws.model.ressource.IdEtablissement;
import org.esco.mediacentre.ws.model.ressource.ListeRessourcesWrapper;
import org.esco.mediacentre.ws.model.ressource.Ressource;
import org.esco.mediacentre.ws.model.structure.Structure;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor
@Slf4j
public class GARRequestServiceImpl implements IRemoteRequestService {

	@Setter
	private RessourceProperties garConfiguration;

	@Setter
	private RestTemplate remoteAccessTemplate;

	@Setter
	private StructureInfoRequestService structureInfoRequestService;

	public List<Ressource> getRessources(final Map<String, List<String>> userInfos) {
		String uri = garConfiguration.getRessourceUri();
		// in app param we don't have user multiple values
		for (ParamValueProperty param : garConfiguration.getAppParams()) {
			uri = uri.replaceAll("\\{" + param.getParam() + "\\}", param.getValue());
		}

		String userMappedAttributeToLoop = null;
		final String authorizedAttrLoop = garConfiguration.getAttributeOnLoop();
		String userMappedIdEtab = null;
		final String userParamIdEtab = garConfiguration.getAttributeIdEtab();
		final Map<String, Structure> mapStructure = structureInfoRequestService.getStructuresInfos(userInfos.getOrDefault(userParamIdEtab, Lists.newArrayList()));
		// in user attributes we should manage multiple values and corresponding to autorized loop
		for (ParamValueProperty param : garConfiguration.getUserParams()) {
			if (!StringUtils.isEmpty(authorizedAttrLoop) && authorizedAttrLoop.equalsIgnoreCase(param.getParam())) {
				userMappedAttributeToLoop = param.getValue();
				continue;
			}
			List<String> userVal = userInfos.getOrDefault(param.getValue(), Lists.newArrayList());
			if (!userVal.isEmpty()) {
				if (log.isDebugEnabled()) {
					log.debug("Replacing in uri {} the attribute {} with value {}", uri, param.getParam(), userVal.get(0));
				}
				uri = uri.replaceAll("\\{" + param.getParam() + "\\}", userVal.get(0));
				if (!StringUtils.isEmpty(userParamIdEtab) && userParamIdEtab.equalsIgnoreCase(param.getParam())) {
					userMappedIdEtab = userVal.get(0);
					if (log.isTraceEnabled()) {
						log.trace("setting userMappedIdEtab with {}", userVal.get(0));
					}
				}
			}
		}
		if (!StringUtils.isEmpty(userMappedAttributeToLoop)) {
			final List<String> userAttrVals = userInfos.getOrDefault(userMappedAttributeToLoop, Lists.newArrayList());
			if (!userAttrVals.isEmpty()) {
				List<Ressource> ressources = new ArrayList<Ressource>();
				for (String attr : userAttrVals) {
					if (userParamIdEtab.equalsIgnoreCase(authorizedAttrLoop)) {
						userMappedIdEtab = attr;
					}
					final String req = uri.replaceAll("\\{" + authorizedAttrLoop + "\\}", attr);
					if (log.isDebugEnabled()) {
						log.debug("Replacing in uri {} the attribute {} with value {}", uri, authorizedAttrLoop, attr);
					}
					try {
						if (log.isTraceEnabled()) {
							log.trace("userMappedIdEtab value is {} from {}", userMappedIdEtab, userParamIdEtab);
						}
						completeAndMergeRessourceInformations(ressources, runUriCall(req), mapStructure.get(userMappedIdEtab));
					} catch (CustomRestRequestException e) {
						log.error(
								"The user defined with theses attributes '{}' doesn't have attribute replacement possible on URI {}",
								userInfos, uri);
					}
				}
				return ressources;
			}
		}

		try {
			List<Ressource> ressources = new ArrayList<Ressource>();
			if (log.isTraceEnabled()) {
				log.trace("userMappedIdEtab value is {} from {}", userMappedIdEtab, userParamIdEtab);
			}
			completeAndMergeRessourceInformations(ressources, runUriCall(uri), mapStructure.get(userMappedIdEtab));
			return ressources;
		} catch (CustomRestRequestException e) {
			log.error(
					"The user defined with theses attributes '{}' doesn't have attribute replacement possible on URI {}",
					userInfos, uri);
			return null;
		}
	}

	private List<Ressource> runUriCall(final String uri) throws CustomRestRequestException {
		if (uri.matches(".*\\{.*\\}.*")) {
			throw new CustomRestRequestException();
		}
		ListeRessourcesWrapper ressourcesObj = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug("Requesting uri {}", uri);
			}
			ressourcesObj = remoteAccessTemplate.getForObject(
					new URI(garConfiguration.getHostConfig().getScheme(), garConfiguration.getHostConfig().getHost(), ":" + garConfiguration.getHostConfig().getPort(), uri, null), ListeRessourcesWrapper.class);
		} catch (URISyntaxException e) {
			log.error("Erreur to construct the URI {}", uri);
			throw new CustomRestRequestException(e);
		}
		//TODO manage Status ?
		return (ressourcesObj != null && ressourcesObj.getListeRessources() != null) ? ressourcesObj.getListeRessources().getRessource() : Lists.newArrayList();
	}

	protected void completeAndMergeRessourceInformations(@NotNull List<Ressource> initialRessources, @NotNull final List<Ressource> complementRessources, final Structure etablissement) {
		IdEtablissement idEtab = new IdEtablissement();
		if (etablissement != null) {
			idEtab = new IdEtablissement(etablissement.getCode(), etablissement.getDisplayName());
		}
		for (Ressource newRS : complementRessources) {
			boolean found = false;
			for (Ressource rs : initialRessources) {
				if (rs.getIdRessource().equals(newRS.getIdRessource())) {
					rs.getIdEtablissement().add(idEtab);
					found = true;
					break;
				}
			}
			if (!found) {
				newRS.setIdEtablissement(Lists.newArrayList(idEtab));
				initialRessources.add(newRS);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("completeAndMergeRessourceInformations returned {}", initialRessources);
		}
	}
}
