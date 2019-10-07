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
package fr.recia.mediacentre.ws.service;

import com.google.common.collect.Lists;
import fr.recia.mediacentre.ws.config.bean.ParamValueProperty;
import fr.recia.mediacentre.ws.config.bean.RessourceProperties;
import fr.recia.mediacentre.ws.model.ressource.FiltreDroit;
import fr.recia.mediacentre.ws.model.ressource.IdEtablissement;
import fr.recia.mediacentre.ws.model.ressource.ListeRessourcesWrapper;
import fr.recia.mediacentre.ws.model.ressource.Ressource;
import fr.recia.mediacentre.ws.model.ressource.diffusion.ListeRessourcesDiffusables;
import fr.recia.mediacentre.ws.model.structure.Structure;
import fr.recia.mediacentre.ws.service.exception.AuthorizedResourceException;
import fr.recia.mediacentre.ws.service.exception.CustomRestRequestException;
import fr.recia.mediacentre.ws.service.exception.ListRequestErrorException;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Slf4j
public class GARRequestServiceImpl implements IRemoteRequestService, InitializingBean {

	public static final String UN_AUTHORIZED_MESSAGE = "L'utilisateur n'est pas autorisé à utiliser les ressources au travers la plateforme GAR.";

	@Setter
	private RessourceProperties garConfiguration;

	@Setter
	private RestTemplate remoteAccessTemplate;

	@Setter
	private HttpHeaders httpHeaders;

	@Setter
	private IStructureInfoService structureInfoService;

	private EvaluatorDroit evaluatorDroit = new EvaluatorDroit();

	public ListeRessourcesDiffusables getRessourcesDiffusables() {
		String uri = garConfiguration.getRessourceDiffusableUri();
		try {
			return getRessourcesDiffusables(uri);
		} catch (CustomRestRequestException e) {
		log.error(
				"Error on constructing the URL to request", uri);
		throw new RestClientException("Mauvaise configuration de l'application.", e);
	}
	}

	public List<Ressource> getRessources(@NotNull final Map<String, List<String>> userInfos) throws ListRequestErrorException {
        if (!this.isUserAuthorized(userInfos)) {
            log.warn("The user isn't allowed to obtain resources from the GAR !");
			ListRequestErrorException lree = new ListRequestErrorException();
			lree.addException(new AuthorizedResourceException(UN_AUTHORIZED_MESSAGE));
            throw lree;
        }
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
				List<HttpClientErrorException> exceptions = new ArrayList<>();
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
						completeAndMergeRessourceInformations(ressources, getRessources(req), getStructureWithFallBack(mapStructure, userMappedIdEtab));
					} catch (CustomRestRequestException e) {
						log.error(
								"The user defined with theses attributes '{}' doesn't have attribute replacement possible on URI {}",
								userInfos, uri);
                        throw new RestClientException("Mauvaise configuration de l'application.", e);
					} catch (HttpClientErrorException e) {
						// provinding the error stacktrace only on debug as the custom logged error should be suffisant.
						exceptions.add(e);
					}
				}
				if (!exceptions.isEmpty()) {
					if (ressources.isEmpty()) {
						ListRequestErrorException ex = new ListRequestErrorException();
						ex.addAllException(exceptions);
						log.error("The user gets only errors and no ressources:", ex.toString());
						throw ex;
					}
					log.warn("The user get errors but some ressources will be shown !");
				}
				return ressources;
			}
		}

		try {
			List<Ressource> ressources = new ArrayList<Ressource>();
			if (log.isDebugEnabled()) {
				log.debug("userMappedIdEtab value is {} from {}", userMappedIdEtab, userParamIdEtab);
			}

			completeAndMergeRessourceInformations(ressources, getRessources(uri), getStructureWithFallBack(mapStructure, userMappedIdEtab));
			return ressources;
		} catch (CustomRestRequestException e) {
			log.error(
					"The user defined with theses attributes '{}' doesn't have attribute replacement possible on URI {}",
					userInfos, uri);
            throw new RestClientException("Mauvaise configuration de l'application.", e);
		}
	}

	private List<Ressource> getRessources(final String uri) throws CustomRestRequestException, RestClientException  {
		this.httpHeaders.put("Accept", Lists.newArrayList(MediaType.APPLICATION_JSON_VALUE));
		ListeRessourcesWrapper ressourcesObj = runUriCall(uri, new ParameterizedTypeReference<ListeRessourcesWrapper>(){}, this.httpHeaders);
		return (ressourcesObj != null && ressourcesObj.getListeRessources() != null) ? ressourcesObj.getListeRessources().getRessource() : Lists.newArrayList();
	}

	private ListeRessourcesDiffusables getRessourcesDiffusables(final String uri) throws CustomRestRequestException, RestClientException  {
		this.httpHeaders.put("Accept", Lists.newArrayList(MediaType.APPLICATION_XML_VALUE));
		return runUriCall(uri, new ParameterizedTypeReference<ListeRessourcesDiffusables>(){}, this.httpHeaders);
	}

	private <T> T runUriCall(final String uri, final ParameterizedTypeReference<T> responseType, final HttpHeaders headers) throws CustomRestRequestException, RestClientException {
		if (uri.matches(".*\\{.*\\}.*")) {
			throw new CustomRestRequestException();
		}
		final String rootURL = getWSRootURL();
		try {
			final URI uriConstructed =  new URI( rootURL + uri);

			if (log.isDebugEnabled()) {
				log.debug("Requesting uri {}", uriConstructed.toString());
			}

			HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);

			final ResponseEntity<T> response = remoteAccessTemplate.exchange(uriConstructed, HttpMethod.GET, httpEntity, responseType);

			if (log.isTraceEnabled()) {
				final ResponseEntity<String> responseTrace = remoteAccessTemplate.exchange(uriConstructed, HttpMethod.GET, httpEntity, String.class);
				String resp = responseTrace.getBody() != null && responseTrace.getBody().toString().length() > 10000 ? responseTrace.getBody().substring(1,10000) : responseTrace.getBody();
				log.trace("Requesting GAR ressources on {} returned a response with status {} and \n" +
								"\nresponse{}\n",
						uriConstructed, responseTrace.getStatusCode(), resp);
			}
			if (log.isDebugEnabled()) {
				log.debug("Requesting GAR ressources on {} returned a response with status {} and \n" +
								"\nresponse{}\n",
						uriConstructed, response.getStatusCode(), response.getHeaders());
			}

			return (T)response.getBody();
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

	}

	private String getWSRootURL() {
		return garConfiguration.getHostConfig().getScheme() + "://" + garConfiguration.getHostConfig().getHost() +
				(garConfiguration.getHostConfig().getPort() > 0 ? ":" + garConfiguration.getHostConfig().getPort() : "");
	}

	protected void completeAndMergeRessourceInformations(@NotNull List<Ressource> initialRessources, @NotNull final List<Ressource> complementRessources, final Structure etablissement) {
		IdEtablissement idEtab = new IdEtablissement();
		if (etablissement != null) {
			idEtab = new IdEtablissement(etablissement.getId(), etablissement.getCode(), etablissement.getDisplayName());
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
			log.warn("An error occured ! it can't get the structure information with the identifier {}, so we make a limited fallback !", id);
			structure = new Structure();
			structure.setId(id);
			structure.setCode(id);
			structure.setDisplayName(id);
		}
		return structure;
	}

	protected boolean isUserAuthorized(@NotNull final Map<String, List<String>> userInfos) {
		final FiltreDroit droits = garConfiguration.getAuthorizedUsers();
		return evaluatorDroit.evaluate(droits, userInfos);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.garConfiguration, "The GAR configuration bean wasn't set!");
		Assert.notNull(this.remoteAccessTemplate, "The RestTemplate bean wasn't set!");
		Assert.notNull(this.structureInfoService, "The StructureInfoService bean wasn't set!");
		Assert.notNull(this.httpHeaders, "The httpHeaders bean wasn't set!");
	}
}
