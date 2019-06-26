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

package org.esco.mediacentre.ws.model.ressource.diffusion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.esco.mediacentre.ws.model.ressource.IdEtablissement;
import org.esco.mediacentre.ws.model.ressource.TypePresentation;
import org.esco.mediacentre.ws.model.ressource.UriDescription;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "idRessource",
    "nomRessource",
    "idEditeur",
    "nomEditeur",
    "distributeursCom",
    "distributeurTech",
    "nomDistributeurTech",
    "affichable",
    "diffusable"
})
@ToString
@EqualsAndHashCode
public class RessourceDiffusable implements Serializable {

    @NonNull
    public String distributeurTech;
    @NonNull
    public String nomDistributeurTech;
    @NonNull
    public String idEditeur;
    @NonNull
    public String idRessource;
    @NonNull
    public String nomEditeur;
    @NonNull
    public String nomRessource;
    public boolean affichable;
    public boolean diffusable;
    @NonNull
    @Valid
    public List<DistributeurCom> distributeursCom = new ArrayList<DistributeurCom>();

    /**
     * No args constructor for use in serialization
     *
     */
    public RessourceDiffusable() {
        super();
    }

    public RessourceDiffusable(@NonNull final String distributeurTech, @NonNull final String nomDistributeurTech,
                               @NonNull final String idEditeur, @NonNull final String idRessource, @NonNull final String nomEditeur,
                               @NonNull final String nomRessource, final boolean affichable, final boolean diffusable,
                               @NonNull @Valid final List<DistributeurCom> distributeursCom) {
        this.distributeurTech = distributeurTech;
        this.nomDistributeurTech = nomDistributeurTech;
        this.idEditeur = idEditeur;
        this.idRessource = idRessource;
        this.nomEditeur = nomEditeur;
        this.nomRessource = nomRessource;
        this.affichable = affichable;
        this.diffusable = diffusable;
        this.distributeursCom = distributeursCom;
    }

    @JsonProperty(value = "distributeurTech", required = true)
    public String getDistributeurTech() {
        return distributeurTech;
    }

    @JsonProperty(value = "distributeurTech", required = true)
    public void setDistributeurTech(String distributeurTech) {
        this.distributeurTech = distributeurTech;
    }

    @JsonProperty(value = "nomDistributeurTech", required = true)
    public String getNomDistributeurTech() {
        return nomDistributeurTech;
    }

    @JsonProperty(value = "nomDistributeurTech", required = true)
    public void setNomDistributeurTech(String nomDistributeurTech) {
        this.nomDistributeurTech = nomDistributeurTech;
    }

    @JsonProperty(value = "idEditeur", required = true)
    public String getIdEditeur() {
        return idEditeur;
    }

    @JsonProperty(value = "idEditeur", required = true)
    public void setIdEditeur(String idEditeur) {
        this.idEditeur = idEditeur;
    }

    @JsonProperty(value = "idRessource", required = true)
    public String getIdRessource() {
        return idRessource;
    }

    @JsonProperty(value = "idRessource", required = true)
    public void setIdRessource(String idRessource) {
        this.idRessource = idRessource;
    }

    @JsonProperty(value = "nomEditeur", required = true)
    public String getNomEditeur() {
        return nomEditeur;
    }

    @JsonProperty(value = "nomEditeur", required = true)
    public void setNomEditeur(String nomEditeur) {
        this.nomEditeur = nomEditeur;
    }

    @JsonProperty(value = "nomRessource", required = true)
    public String getNomRessource() {
        return nomRessource;
    }

    @JsonProperty(value = "nomRessource", required = true)
    public void setNomRessource(String nomRessource) {
        this.nomRessource = nomRessource;
    }

    @JsonProperty(value = "affichable", required = true)
    public boolean isAffichable() {
        return affichable;
    }

    @JsonProperty(value = "affichable", required = true)
    public void setAffichable(boolean affichable) {
        this.affichable = affichable;
    }

    @JsonProperty(value = "diffusable", required = true)
    public boolean isDiffusable() {
        return diffusable;
    }

    @JsonProperty(value = "diffusable", required = true)
    public void setDiffusable(boolean diffusable) {
        this.diffusable = diffusable;
    }

    @JsonProperty(value = "distributeursCom", required = true)
    public List<DistributeurCom> getDistributeursCom() {
        return distributeursCom;
    }

    @JsonProperty(value = "distributeursCom", required = true)
    public void setDistributeursCom(List<DistributeurCom> distributeursCom) {
        this.distributeursCom = distributeursCom;
    }


}
