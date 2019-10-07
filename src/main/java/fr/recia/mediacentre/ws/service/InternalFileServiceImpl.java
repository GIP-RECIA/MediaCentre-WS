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
import com.google.common.collect.Sets;
import fr.recia.mediacentre.ws.config.bean.LocalRessourceProperties;
import fr.recia.mediacentre.ws.model.ressource.ExtractEtablissementOnPattern;
import fr.recia.mediacentre.ws.model.ressource.IdEtablissement;
import fr.recia.mediacentre.ws.model.ressource.LocalRessource;
import fr.recia.mediacentre.ws.model.ressource.Ressource;
import fr.recia.mediacentre.ws.model.ressource.diffusion.ListeRessourcesDiffusables;
import fr.recia.mediacentre.ws.model.structure.Structure;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

@NoArgsConstructor
@Slf4j
public class InternalFileServiceImpl implements IRemoteRequestService, InitializingBean {

	@Setter
	private LocalRessourceProperties localRSConfiguration;
	@Setter
	private IStructureInfoService structureInfoService;
	@Setter
	private LocalRessourceLoader loader;

	private EvaluatorDroit evaluatorDroit = new EvaluatorDroit();

	public ListeRessourcesDiffusables getRessourcesDiffusables() {
		return null;
	}

	public List<Ressource> getRessources(@NotNull final Map<String, List<String>> userInfos) {
		final List<LocalRessource> ressourceList = loader.getLocalRessources();
		log.debug("Liste des ressources définies chargées {}", ressourceList);

		// Pour la completion des ressources
		final Map<String, IdEtablissement> mapEtablissement = getMapIdEtablissement(userInfos);

		// filtrage des ressources et ajout lien établissement
		List<Ressource> authoredRessources = new ArrayList<>();

		for(LocalRessource rs: ressourceList) {
			if (isUserAuthorized(rs, userInfos)) {
				final List<IdEtablissement> authorizedEtabs = getAuthorizedEtablissement(rs, userInfos, mapEtablissement);
				if (!authorizedEtabs.isEmpty()) {
					rs.setIdEtablissement(authorizedEtabs);
					authoredRessources.add(rs);
				} else {
					log.debug("La ressource {} a été filtrée car aucun établissement authorisé n'a été trouvé !", rs);
				}
			} else {
				log.debug("L'utilisateur {} n'est pas autorisé à accéder à la ressource {}", userInfos.get(this.localRSConfiguration.getUserAttributeId()), rs);
			}
		}

		log.debug("L'utilisateur {} aura la liste des ressources attribuées {}", userInfos.get(this.localRSConfiguration.getUserAttributeId()), ressourceList);
		return authoredRessources;
	}

	private List<IdEtablissement> getAuthorizedEtablissement(@NotNull final LocalRessource ressource,
															 @NotNull final Map<String, List<String>> userInfos,
															 @NotNull  Map<String, IdEtablissement> mapEtablissement) {
		final Set<String> authorizedEtablissements = extractAuthorizedEtablissement(ressource, userInfos);
		List<IdEtablissement> returnedIdEtabs = new ArrayList<>();
		for (String etabCode: authorizedEtablissements) {
			final IdEtablissement etab = mapEtablissement.get(etabCode);
			if (etab != null)
				returnedIdEtabs.add(mapEtablissement.get(etabCode));
		}
		return returnedIdEtabs;
	}

	private Map<String, IdEtablissement> getMapIdEtablissement(@NotNull final Map<String, List<String>> userInfos) {
		final List<String> userEtabs = userInfos.getOrDefault(localRSConfiguration.getUserAttributeIdEtab(), Lists.newArrayList());
		final Map<String, Structure> mapStructure = structureInfoService.getStructuresInfosList(userEtabs);
		Map<String, IdEtablissement> mapEtablissement = new HashMap<>();
		if (!userEtabs.isEmpty()) {
			for (String etabId: userEtabs) {
				@NotNull Structure etablissement = getStructureWithFallBack(mapStructure, etabId);
				mapEtablissement.put(etabId, new IdEtablissement(etablissement.getId(), etablissement.getCode(), etablissement.getDisplayName()));
			}
		}

		log.debug("extractIdEtablissement returned {}", mapEtablissement);
		return mapEtablissement;
	}

	private Set<String> extractAuthorizedEtablissement(@NotNull final LocalRessource ressource, @NotNull final Map<String, List<String>> userInfos) {
		Set<String> ids = new HashSet<>();
		if (ressource.getExtractEtablissement() != null && !ressource.getExtractEtablissement().isEmpty()) {
			for (ExtractEtablissementOnPattern extract: ressource.getExtractEtablissement()) {
				final List<String> userPropValues = userInfos.getOrDefault(extract.getAttribut(), new ArrayList<>());
				for (String value: userPropValues) {
					Matcher matcher = extract.getCompiledPattern().matcher(value);
					if (matcher.matches()) {
						ids.add(matcher.group(extract.getGroup()));
					}
				}
			}
			log.debug("Liste des établissements {} où l'utilisateur {} à la ressource {}", ids, userInfos.get(this.localRSConfiguration.getUserAttributeId()), ressource);
			return ids;
		}
		// retourne par défaut la liste de tous les établissements de l'utilisateur
		log.debug("Liste des établissements {} où l'utilisateur à le service {}", userInfos.getOrDefault(localRSConfiguration.getUserAttributeIdEtab(), Lists.newArrayList()), ressource);
		return Sets.newHashSet(userInfos.getOrDefault(localRSConfiguration.getUserAttributeIdEtab(), Lists.newArrayList()));
	}

	private Structure getStructureWithFallBack(final Map<String, Structure> mapStructure, final String id) {
		Structure structure = mapStructure.get(id);
		if (structure == null) {
			log.warn("An error occured ! it can't get the structure information with the identifier {}, so we make a limited fallback !", id);
			structure = new Structure();
			structure.setCode(id);
			structure.setId(id);
			structure.setDisplayName(id);
		}
		return structure;
	}

	private boolean isUserAuthorized(@NotNull final LocalRessource ressource, @NotNull final Map<String, List<String>> userInfos) {
		if (ressource.getFiltreDroit() != null && !ressource.getFiltreDroit().getFiltreDroitType().isEmpty()) {
			return evaluatorDroit.evaluate(ressource.filtreDroit, userInfos);
		}
		// si la liste des filtres est vide cela signifie qu'il n'y a pas de filtrage.
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.localRSConfiguration, "The localRSConfiguration configuration bean wasn't setted !");
		Assert.notNull(this.structureInfoService, "The StructureInfoService bean wasn't setted !");
		Assert.notNull(this.loader, "The loader bean wasn't setted !");
	}
}
