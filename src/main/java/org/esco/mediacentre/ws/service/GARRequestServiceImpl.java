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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor
@Slf4j
public class GARRequestServiceImpl implements IRemoteRequestService, InitializingBean {

	@Setter
	private RessourceProperties garConfiguration;

	@Setter
	private RestTemplate remoteAccessTemplate;

	@Setter
	private HttpHeaders httpHeaders;

	@Setter
	private IStructureInfoService structureInfoService;

	public List<Ressource> getRessources(@NotNull final Map<String, List<String>> userInfos) {
		String uri = garConfiguration.getRessourceUri();
		// in app param we don't have user multiple values
		for (ParamValueProperty param : garConfiguration.getAppParams()) {
			uri = uri.replaceAll("\\{" + param.getParam() + "\\}", param.getValue());
		}

		String userMappedAttributeToLoop = null;
		final String authorizedAttrLoop = garConfiguration.getAttributeOnLoop();
		String userMappedIdEtab = null;
		final String userParamIdEtab = garConfiguration.getAttributeIdEtab();
		String userParamIdEtabMapped = null;

		// in user attributes we should manage multiple values and corresponding to autorized loop
		for (ParamValueProperty param : garConfiguration.getUserParams()) {
			if (!StringUtils.isEmpty(authorizedAttrLoop) && authorizedAttrLoop.equalsIgnoreCase(param.getParam())) {
				userMappedAttributeToLoop = param.getValue();
				if (authorizedAttrLoop.equals(userParamIdEtab)){
					userParamIdEtabMapped = param.getValue();
				}
				continue;// go to next loop
			}
			List<String> userVal = userInfos.getOrDefault(param.getValue(), Lists.newArrayList());
			if (!userVal.isEmpty()) {
				if (log.isDebugEnabled()) {
					log.debug("Replacing in uri {} the attribute {} with value {}", uri, param.getParam(), userVal.get(0));
				}
				uri = uri.replaceAll("\\{" + param.getParam() + "\\}", userVal.get(0));
				if (!StringUtils.isEmpty(userParamIdEtab) && userParamIdEtab.equalsIgnoreCase(param.getParam())) {
					userMappedIdEtab = userVal.get(0);
					userParamIdEtabMapped = param.getValue();
					if (log.isDebugEnabled()) {
						log.debug("setting userMappedIdEtab with {} and userParamIdEtabMapped with {}", userMappedIdEtab, userParamIdEtabMapped);
					}
				}
			}
		}

		final Map<String, Structure> mapStructure = structureInfoService.getStructuresInfosList(userInfos.getOrDefault(userParamIdEtabMapped, Lists.newArrayList()));

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
						if (log.isDebugEnabled()) {
							log.debug("userMappedIdEtab value is {} from {}", userMappedIdEtab, userParamIdEtab);
						}
						completeAndMergeRessourceInformations(ressources, runUriCall(req), getStructureWithFallBack(mapStructure, userMappedIdEtab));
					} catch (CustomRestRequestException e) {
						log.error(
								"The user defined with theses attributes '{}' doesn't have attribute replacement possible on URI {}",
								userInfos, uri);
                        throw new RestClientException("Mauvaise configuration de l'application.", e);
					}
				}
				return ressources;
			}
		}

		try {
			List<Ressource> ressources = new ArrayList<Ressource>();
			if (log.isDebugEnabled()) {
				log.debug("userMappedIdEtab value is {} from {}", userMappedIdEtab, userParamIdEtab);
			}

			completeAndMergeRessourceInformations(ressources, runUriCall(uri), getStructureWithFallBack(mapStructure, userMappedIdEtab));
			return ressources;
		} catch (CustomRestRequestException e) {
			log.error(
					"The user defined with theses attributes '{}' doesn't have attribute replacement possible on URI {}",
					userInfos, uri);
            throw new RestClientException("Mauvaise configuration de l'application.", e);
		}
	}

	private List<Ressource> runUriCall(final String uri) throws CustomRestRequestException, RestClientException {
		if (uri.matches(".*\\{.*\\}.*")) {
			throw new CustomRestRequestException();
		}
		ListeRessourcesWrapper ressourcesObj = null;
		final String rootURL = garConfiguration.getHostConfig().getScheme() + "://" + garConfiguration.getHostConfig().getHost() +
				(garConfiguration.getHostConfig().getPort() > 0 ? ":" + garConfiguration.getHostConfig().getPort() : "");
		try {
			final URI uriConstructed =  new URI(rootURL + uri);

			if (log.isDebugEnabled()) {
				log.debug("Requesting uri {}", uriConstructed.toString());
			}
			HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);

			final ResponseEntity<ListeRessourcesWrapper> response = remoteAccessTemplate.exchange(uriConstructed, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<ListeRessourcesWrapper>(){});
			if (log.isDebugEnabled()) {
				log.debug("Requesting GAR ressources on {} returned a response with status {} and \n" +
								"\nresponse{}\n",
						uriConstructed, response.getStatusCode(), response);
			}

			if (log.isTraceEnabled()) {
				final ResponseEntity<String> responseTrace = remoteAccessTemplate.exchange(uriConstructed, HttpMethod.GET, httpEntity, String.class);
				log.trace("Requesting GAR ressources on {} returned a response with status {} and \n" +
								"\nresponse{}\n",
						uriConstructed, responseTrace.getStatusCode(), responseTrace.getBody());
			}

			ressourcesObj = response.getBody();
		} catch (URISyntaxException e) {
            log.error("Error to construct the URI {}", rootURL + uri, e);
            throw new CustomRestRequestException(e);
		} catch (HttpClientErrorException e) {
			// provinding the error stacktrace only on debug as the custom logged error should be suffisant.
			if (log.isDebugEnabled()) {
				log.error("Error client request on URL {}, returned status {}, with response {}", rootURL + uri, e.getStatusCode(), e.getResponseBodyAsString(),e);
			} else {
				log.error("Error client request on URL {}, returned status {}, with response {}", rootURL + uri, e.getStatusCode(), e.getResponseBodyAsString());
			}
            throw e;

        } catch (RestClientException e) {
            if (log.isDebugEnabled()) {
                log.error("Error client request on URL {}, with cause {}", rootURL + uri, e.getLocalizedMessage(),e);
            } else {
                log.error("Error client request on URL {}, with cause {}", rootURL + uri, e.getLocalizedMessage());
            }
            throw e;
        }
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

	private Structure getStructureWithFallBack(final Map<String, Structure> mapStructure, final String id) {
		Structure structure = mapStructure.get(id);
		if (structure == null) {
			log.warn("An error occured ! it can't get the structure informations with the identifier {}, so we make a limited fallback !", id);
			structure = new Structure();
			structure.setCode(id);
			structure.setDisplayName(id);
		}
		return structure;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.garConfiguration, "The GAR configuration bean wasn't setted !");
		Assert.notNull(this.remoteAccessTemplate, "The RestTemplate bean wasn't setted !");
		Assert.notNull(this.structureInfoService, "The StructureInfoService bean wasn't setted !");
		Assert.notNull(this.httpHeaders, "The httpHeaders bean wasn't setted !");
	}
}
