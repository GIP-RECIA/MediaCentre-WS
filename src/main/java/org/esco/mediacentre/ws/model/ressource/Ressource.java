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
    @JsonProperty(value = "sourceEtiquette", required = true)
    public String sourceEtiquette;

    @NonNull
    @JsonProperty(value = "distributeurTech", required = true)
    public String distributeurTech;
    @NonNull
    @JsonProperty(value = "domaineEnseignement", required = true)
    @Valid
    public List<UriDescription> domaineEnseignement = new ArrayList<UriDescription>();
    @NonNull
    @JsonProperty(value = "idEditeur", required = true)
    public String idEditeur;
    @NonNull
    @JsonProperty(value = "idRessource", required = true)
    public String idRessource;
    /** Ajout interne */
    @JsonProperty("idEtablissement")
    @Valid
    public List<IdEtablissement> idEtablissement = new ArrayList<IdEtablissement>();
    @NonNull
    @JsonProperty(value = "idType", required = true)
    public String idType;
    @NonNull
    @JsonProperty(value = "niveauEducatif", required = true)
    @Valid
    public List<UriDescription> niveauEducatif = new ArrayList<UriDescription>();
    @NonNull
    @JsonProperty(value = "nomEditeur", required = true)
    public String nomEditeur;
    @NonNull
    @JsonProperty(value = "nomRessource", required = true)
    public String nomRessource;
    @NonNull
    @JsonProperty(value = "typePedagogique", required = true)
    @Valid
    public List<UriDescription> typePedagogique = new ArrayList<UriDescription>();
    @NonNull
    @JsonProperty(value = "typePresentation", required = true)
    @Valid
    public List<TypePresentation> typePresentation = new ArrayList<TypePresentation>();
    @NonNull
    @JsonProperty(value = "typologieDocument", required = true)
    @Valid
    public List<UriDescription> typologieDocument = new ArrayList<UriDescription>();
    @NonNull
    @JsonProperty(value = "urlAccesRessource", required = true)
    public String urlAccesRessource;
    @JsonProperty(value = "urlSourceEtiquette", required = false)
    public String urlSourceEtiquette;
    @NonNull
    @JsonProperty(value = "urlVignette", required = true)
    public String urlVignette;
    @NonNull
    @JsonProperty(value = "validateurTech", required = true)
    public String validateurTech;


    @JsonProperty("description")
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
                     final String nomEditeur, final String nomRessource,final  List<UriDescription> typePedagogique,final  List<TypePresentation> typePresentation,
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

    @JsonProperty("distributeurTech")
    public String getDistributeurTech() {
        return distributeurTech;
    }

    @JsonProperty("distributeurTech")
    public void setDistributeurTech(String distributeurTech) {
        this.distributeurTech = distributeurTech;
    }

    @JsonProperty("domaineEnseignement")
    public List<UriDescription> getDomaineEnseignement() {
        return domaineEnseignement;
    }

    @JsonProperty("domaineEnseignement")
    public void setDomaineEnseignement(List<UriDescription> domaineEnseignement) {
        this.domaineEnseignement = domaineEnseignement;
    }

    @JsonProperty("idEditeur")
    public String getIdEditeur() {
        return idEditeur;
    }

    @JsonProperty("idEditeur")
    public void setIdEditeur(String idEditeur) {
        this.idEditeur = idEditeur;
    }

    @JsonProperty("idRessource")
    public String getIdRessource() {
        return idRessource;
    }

    @JsonProperty("idRessource")
    public void setIdRessource(String idRessource) {
        this.idRessource = idRessource;
    }

    @JsonProperty("idEtablissement")
    public List<IdEtablissement> getIdEtablissement() {
        return idEtablissement;
    }

    @JsonProperty("idEtablissement")
    public void setIdEtablissement(List<IdEtablissement> idEtablissement) {
        this.idEtablissement = idEtablissement;
    }

    @JsonProperty("idType")
    public String getIdType() {
        return idType;
    }

    @JsonProperty("idType")
    public void setIdType(String idType) {
        this.idType = idType;
    }

    @JsonProperty("niveauEducatif")
    public List<UriDescription> getNiveauEducatif() {
        return niveauEducatif;
    }

    @JsonProperty("niveauEducatif")
    public void setNiveauEducatif(List<UriDescription> niveauEducatif) {
        this.niveauEducatif = niveauEducatif;
    }

    @JsonProperty("nomEditeur")
    public String getNomEditeur() {
        return nomEditeur;
    }

    @JsonProperty("nomEditeur")
    public void setNomEditeur(String nomEditeur) {
        this.nomEditeur = nomEditeur;
    }

    @JsonProperty("nomRessource")
    public String getNomRessource() {
        return nomRessource;
    }

    @JsonProperty("nomRessource")
    public void setNomRessource(String nomRessource) {
        this.nomRessource = nomRessource;
    }

    @JsonProperty("sourceEtiquette")
    public String getSourceEtiquette() {
        return sourceEtiquette;
    }

    @JsonProperty("sourceEtiquette")
    public void setSourceEtiquette(String sourceEtiquette) {
        this.sourceEtiquette = sourceEtiquette;
    }

    @JsonProperty("typePedagogique")
    public List<UriDescription> getTypePedagogique() {
        return typePedagogique;
    }

    @JsonProperty("typePedagogique")
    public void setTypePedagogique(List<UriDescription> typePedagogique) {
        this.typePedagogique = typePedagogique;
    }

    @JsonProperty("typePresentation")
    public List<TypePresentation> getTypePresentation() {
        return typePresentation;
    }

    @JsonProperty("typePresentation")
    public void setTypePresentation(List<TypePresentation> typePresentation) {
        this.typePresentation = typePresentation;
    }

    @JsonProperty("typologieDocument")
    public List<UriDescription> getTypologieDocument() {
        return typologieDocument;
    }

    @JsonProperty("typologieDocument")
    public void setTypologieDocument(List<UriDescription> typologieDocument) {
        this.typologieDocument = typologieDocument;
    }

    @JsonProperty("urlAccesRessource")
    public String getUrlAccesRessource() {
        return urlAccesRessource;
    }

    @JsonProperty("urlAccesRessource")
    public void setUrlAccesRessource(String urlAccesRessource) {
        this.urlAccesRessource = urlAccesRessource;
    }

    @JsonProperty("urlSourceEtiquette")
    public String getUrlSourceEtiquette() {
        return urlSourceEtiquette;
    }

    @JsonProperty("urlSourceEtiquette")
    public void setUrlSourceEtiquette(String urlSourceEtiquette) {
        this.urlSourceEtiquette = urlSourceEtiquette;
    }

    @JsonProperty("urlVignette")
    public String getUrlVignette() {
        return urlVignette;
    }

    @JsonProperty("urlVignette")
    public void setUrlVignette(String urlVignette) {
        this.urlVignette = urlVignette;
    }

    @JsonProperty("validateurTech")
    public String getValidateurTech() {
        return validateurTech;
    }

    @JsonProperty("validateurTech")
    public void setValidateurTech(String validateurTech) {
        this.validateurTech = validateurTech;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }
}
