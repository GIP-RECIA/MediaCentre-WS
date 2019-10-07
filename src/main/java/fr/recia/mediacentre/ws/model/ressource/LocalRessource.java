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
package fr.recia.mediacentre.ws.model.ressource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "distributeurTech",
        "domaineEnseignement",
        "idEditeur",
        "idRessource",
        "idEtablissement",
        "idType",
        "niveauEducatif",
        "nomEditeur",
        "nomRessource",
        "sourceEtiquette",
        "typePedagogique",
        "typePresentation",
        "typologieDocument",
        "urlAccesRessource",
        "urlSourceEtiquette",
        "urlVignette",
        "validateurTech",
        "description",
        "filtreDroit",
        "extractEtablissement"
})
@ToString(callSuper = true)
@EqualsAndHashCode
public class LocalRessource extends Ressource {

    @Valid
    public FiltreDroit filtreDroit;

    @Valid
    public List<ExtractEtablissementOnPattern> extractEtablissement = new ArrayList<>();

    public LocalRessource() {
        super();
    }

    public LocalRessource(final String distributeurTech, final String validateurTech, final String sourceEtiquette, final List<UriDescription> domaineEnseignement, final String idEditeur, final String idRessource,
                          final List<IdEtablissement> idEtablissement, final String idType, final List<UriDescription> niveauEducatif, final String nomEditeur, final String nomRessource, final List<UriDescription> typePedagogique,
                          final TypePresentation typePresentation, final List<UriDescription> typologieDocument, final String urlAccesRessource, final String urlSourceEtiquette, final String urlVignette, final String description,
                          final FiltreDroit filtreDroit, final List<ExtractEtablissementOnPattern> extractEtablissement) {
        super(distributeurTech, validateurTech, sourceEtiquette, domaineEnseignement, idEditeur, idRessource, idEtablissement, idType, niveauEducatif, nomEditeur, nomRessource, typePedagogique, typePresentation, typologieDocument,
                urlAccesRessource, urlSourceEtiquette, urlVignette, description);
        this.filtreDroit = filtreDroit;
        this.extractEtablissement = extractEtablissement;
    }

    @JsonProperty("filtreDroit")
    public FiltreDroit getFiltreDroit() {
        return filtreDroit;
    }
    @JsonProperty("filtreDroit")
    public void setFiltreDroit(FiltreDroit filtreDroit) {
        this.filtreDroit = filtreDroit;
    }
    @JsonProperty("extractEtablissement")
    public List<ExtractEtablissementOnPattern> getExtractEtablissement() {
        return extractEtablissement;
    }
    @JsonProperty("extractEtablissement")
    public void setExtractEtablissement(List<ExtractEtablissementOnPattern> extractEtablissement) {
        this.extractEtablissement = extractEtablissement;
    }
}
