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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.model.erreur.ErreurWrapper;
import org.esco.mediacentre.ws.model.ressource.ListeRessourcesWrapper;
import org.esco.mediacentre.ws.model.ressource.Ressource;
import org.esco.mediacentre.ws.model.structure.Structure;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
public class MockedRequestServiceImpl implements IRemoteRequestService, InitializingBean {

	@Getter
	private Resource resources0377777U;
	@Getter
	private Resource resources0450822X;
	@Getter
	private Resource resourceErreur;

	@Setter
	private IStructureInfoService structureInfoService;

	private Structure structure0450822X;
	private Structure structure0377777U;

	public MockedRequestServiceImpl() {
		resources0377777U = new ClassPathResource("/json/gar_reponse_0377777U.json");
		resources0450822X = new ClassPathResource("/json/gar_reponse_0450822X.json");
		resourceErreur = new ClassPathResource("/json/gar_reponse_erreur.json");
		String ressourceContent = null;
		String ressourceContent2 = null;
		String ressourceContent3 = null;
		try {
			ressourceContent = Files.toString(new File(resources0377777U.getURI()), Charset.forName("UTF-8"));
			ressourceContent2 = Files.toString(new File(resources0450822X.getURI()), Charset.forName("UTF-8"));
			ressourceContent3 = Files.toString(new File(resourceErreur.getURI()), Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assert.state(ressourceContent != null, "file in classPath '/json/gar_reponse_0450822X.json' doesn't exist!");
		Assert.state(ressourceContent2 != null, "file in classPath '/json/gar_reponse_0377777U.json' doesn't exist!");
		Assert.state(ressourceContent3 != null, "file in classPath '/json/gar_reponse_erreur.json' doesn't exist!");
		Assert.state(!ressourceContent.isEmpty(), "file in classPath '/json/gar_reponse_0450822X.json' is Empty!");
		Assert.state(!ressourceContent2.isEmpty(), "file in classPath '/json/gar_reponse_0377777U.json' is Empty!");
		Assert.state(!ressourceContent3.isEmpty(), "file in classPath '/json/gar_reponse_erreur.json' is Empty!");
	}

	private void init(){
		if (structureInfoService != null) {
			Map<String, Structure> structs = structureInfoService.getStructuresInfosList(Lists.newArrayList("0450822X", "0377777U"));
			Assert.state(structs != null, "The Rest Service to obtain structures informations should not return a null value !");
			structure0450822X = structs.get("0450822X");
			structure0377777U = structs.get("0377777U");
		} else {
			structure0450822X = new Structure("00000000000001", "LPO LYC METIER-FICTIF-ac-ORL._TOURS", "Lycée Fictif", "Lycée Polyvalent", Maps.newHashMap(), "0450822X");
			structure0377777U = new Structure("37373737373737", "\"CLG-FICTIF37-ac-ORL._TOURS", "CLG Fictif37", "COLLEGE", Maps.newHashMap(), "0377777U");
		}
		Assert.state(structure0450822X != null, "The Rest Service to obtain structures informations returned a null Structure for UAI '0450822X' !");
		Assert.state(structure0377777U != null, "The Rest Service to obtain structures informations returned a null Structure for UAI '0377777U' !");
	}

	public List<Ressource> getRessources(@NotNull final Map<String, List<String>> userInfos) {
		init();
		List<Ressource> ressources = Lists.newArrayList();
		ObjectMapper mapper = new ObjectMapper();
		try {
			ListeRessourcesWrapper lr1 = mapper.readValue(resources0450822X.getInputStream(), ListeRessourcesWrapper.class);
			ListeRessourcesWrapper lr2 = mapper.readValue(resources0377777U.getInputStream(), ListeRessourcesWrapper.class);
			ErreurWrapper erreur = mapper.readValue(resourceErreur.getInputStream(), ErreurWrapper.class);

			if (userInfos.get("uid").contains("erreur") && erreur != null) {
				HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
				for (HttpStatus hs: HttpStatus.values()) {
					if (hs.getReasonPhrase().equals(erreur.getErreur().getCode())){
						status=hs;
					}
				}
				throw new HttpClientErrorException(status, erreur.getErreur().getMessage(),
						mapper.writeValueAsBytes(erreur), Charset.defaultCharset());
			}

			if (lr1.getListeRessources() != null && lr1.getListeRessources().getRessource() != null
					&& lr2.getListeRessources() != null && lr2.getListeRessources().getRessource() != null) {
				GARRequestServiceImpl serviceGARRequest = new GARRequestServiceImpl();
				serviceGARRequest.completeAndMergeRessourceInformations(ressources, lr1.getListeRessources().getRessource(), structure0450822X);
				serviceGARRequest.completeAndMergeRessourceInformations(ressources, lr2.getListeRessources().getRessource(), structure0377777U);
			} else {
				log.warn("Please check json datas on files json/gar_response_*.json, theses files should have a readable content for mocking !");
			}
		} catch (IOException e) {
			log.error("During mocking an error occured while reading json datas on files /json/gar_response_*.json", e);
			return new ArrayList<Ressource>();
		}

		return ressources;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.structureInfoService, "The structureInfoService bean wasn't setted !");
	}
}
