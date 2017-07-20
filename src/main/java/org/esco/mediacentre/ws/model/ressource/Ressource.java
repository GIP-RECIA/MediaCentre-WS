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

package org.esco.mediacentre.ws.model.ressource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

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
    "description"
})
@ToString
@EqualsAndHashCode
public class Ressource implements Serializable {

    /** Source de la ressource : GAR par exemple). */
    @NonNull
    public String sourceEtiquette;
    @NonNull
    public String distributeurTech;
    @Valid
    public List<UriDescription> domaineEnseignement = new ArrayList<UriDescription>();
    @NonNull
    public String idEditeur;
    @NonNull
    public String idRessource;
    /** Ajout interne */
    @Valid
    public List<IdEtablissement> idEtablissement = new ArrayList<IdEtablissement>();
    @NonNull
    public String idType;
    @Valid
    public List<UriDescription> niveauEducatif = new ArrayList<UriDescription>();
    @NonNull
    public String nomEditeur;
    @NonNull
    public String nomRessource;
    @Valid
    public List<UriDescription> typePedagogique = new ArrayList<UriDescription>();
    @NonNull
    @Valid
    public TypePresentation typePresentation;
    @Valid
    public List<UriDescription> typologieDocument = new ArrayList<UriDescription>();
    @NonNull
    public String urlAccesRessource;

    public String urlSourceEtiquette;

    public String urlVignette;
    @NonNull
    public String validateurTech;

    public String description;

    /**
     * No args constructor for use in serialization
     *
     */
    public Ressource() {
        super();
    }

    /**
     *
     * @param idType
     * @param typologieDocument
     * @param nomRessource
     * @param domaineEnseignement
     * @param urlVignette
     * @param typePedagogique
     * @param idRessource
     * @param idEtablissement
     * @param distributeurTech
     * @param sourceEtiquette
     * @param urlAccesRessource
     * @param idEditeur
     * @param typePresentation
     * @param niveauEducatif
     * @param nomEditeur
     * @param urlSourceEtiquette
     * @param validateurTech
     */
    public Ressource(final String distributeurTech, final String validateurTech, final String sourceEtiquette, final List<UriDescription> domaineEnseignement,
                     final String idEditeur, final String idRessource, final List<IdEtablissement> idEtablissement, final String idType, final List<UriDescription> niveauEducatif,
                     final String nomEditeur, final String nomRessource,final  List<UriDescription> typePedagogique,final TypePresentation typePresentation,
                     final List<UriDescription> typologieDocument, final String urlAccesRessource, final String urlSourceEtiquette, final String urlVignette, final String description) {
        this.distributeurTech = distributeurTech;
        this.validateurTech = validateurTech;
        this.sourceEtiquette = sourceEtiquette;
        this.domaineEnseignement = domaineEnseignement;
        this.idEditeur = idEditeur;
        this.idRessource = idRessource;
        this.idEtablissement = idEtablissement;
        this.idType = idType;
        this.niveauEducatif = niveauEducatif;
        this.nomEditeur = nomEditeur;
        this.nomRessource = nomRessource;
        this.typePedagogique = typePedagogique;
        this.typePresentation = typePresentation;
        this.typologieDocument = typologieDocument;
        this.urlAccesRessource = urlAccesRessource;
        this.urlSourceEtiquette = urlSourceEtiquette;
        this.urlVignette = urlVignette;
        this.description = description;
    }

    @JsonProperty(value = "distributeurTech", required = true)
    public String getDistributeurTech() {
        return distributeurTech;
    }

    @JsonProperty(value = "distributeurTech", required = true)
    public void setDistributeurTech(String distributeurTech) {
        this.distributeurTech = distributeurTech;
    }

    @JsonProperty(value = "domaineEnseignement", required = false)
    public List<UriDescription> getDomaineEnseignement() {
        return domaineEnseignement;
    }

    @JsonProperty(value = "domaineEnseignement", required = false)
    public void setDomaineEnseignement(List<UriDescription> domaineEnseignement) {
        this.domaineEnseignement = domaineEnseignement;
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

    @JsonProperty(value = "idEtablissement", required = true)
    public List<IdEtablissement> getIdEtablissement() {
        return idEtablissement;
    }

    @JsonProperty(value = "idEtablissement", required = true)
    public void setIdEtablissement(List<IdEtablissement> idEtablissement) {
        this.idEtablissement = idEtablissement;
    }

    @JsonProperty(value = "idType", required = true)
    public String getIdType() {
        return idType;
    }

    @JsonProperty(value = "idType", required = true)
    public void setIdType(String idType) {
        this.idType = idType;
    }

    @JsonProperty(value = "niveauEducatif", required = false)
    public List<UriDescription> getNiveauEducatif() {
        return niveauEducatif;
    }

    @JsonProperty(value = "niveauEducatif", required = false)
    public void setNiveauEducatif(List<UriDescription> niveauEducatif) {
        this.niveauEducatif = niveauEducatif;
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

    @JsonProperty(value = "sourceEtiquette", required = true, access = JsonProperty.Access.READ_ONLY)
    public String getSourceEtiquette() {
        return sourceEtiquette;
    }

    @JsonProperty(value = "nomSourceEtiquetteGar", required = true, access = JsonProperty.Access.WRITE_ONLY)
    public void setSourceEtiquette(String sourceEtiquette) {
        this.sourceEtiquette = sourceEtiquette;
    }

    @JsonProperty(value = "typePedagogique", required = false)
    public List<UriDescription> getTypePedagogique() {
        return typePedagogique;
    }

    @JsonProperty(value = "typePedagogique", required = false)
    public void setTypePedagogique(List<UriDescription> typePedagogique) {
        this.typePedagogique = typePedagogique;
    }

    @JsonProperty(value = "typePresentation", required = true)
    public TypePresentation getTypePresentation() {
        return typePresentation;
    }

    @JsonProperty(value = "typePresentation", required = true)
    public void setTypePresentation(TypePresentation typePresentation) {
        this.typePresentation = typePresentation;
    }

    @JsonProperty(value = "typologieDocument", required = false)
    public List<UriDescription> getTypologieDocument() {
        return typologieDocument;
    }

    @JsonProperty(value = "typologieDocument", required = false)
    public void setTypologieDocument(List<UriDescription> typologieDocument) {
        this.typologieDocument = typologieDocument;
    }

    @JsonProperty(value = "urlAccesRessource", required = true)
    public String getUrlAccesRessource() {
        return urlAccesRessource;
    }

    @JsonProperty(value = "urlAccesRessource", required = true)
    public void setUrlAccesRessource(String urlAccesRessource) {
        this.urlAccesRessource = urlAccesRessource;
    }

    @JsonProperty(value = "urlSourceEtiquette", required = false)
    public String getUrlSourceEtiquette() {
        return urlSourceEtiquette;
    }

    @JsonProperty(value = "urlSourceEtiquette", required = false)
    public void setUrlSourceEtiquette(String urlSourceEtiquette) {
        this.urlSourceEtiquette = urlSourceEtiquette;
    }

    @JsonProperty(value = "urlVignette", required = false)
    public String getUrlVignette() {
        return urlVignette;
    }

    @JsonProperty(value = "urlVignette", required = false)
    public void setUrlVignette(String urlVignette) {
        this.urlVignette = urlVignette;
    }

    @JsonProperty(value = "validateurTech", required = true)
    public String getValidateurTech() {
        return validateurTech;
    }

    @JsonProperty(value = "validateurTech", required = true)
    public void setValidateurTech(String validateurTech) {
        this.validateurTech = validateurTech;
    }

    @JsonProperty(value = "description", required = true)
    public String getDescription() {
        return description;
    }

    @JsonProperty(value = "description", required = true)
    public void setDescription(String description) {
        this.description = description;
    }
}
