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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "UAI"
})
@ToString
@EqualsAndHashCode
public class IdEtablissement implements Serializable {

    @NonNull
    @JsonProperty(value = "UAI", required = true)
    public String UAI;

    @NonNull
    @JsonProperty("nom")
    public String nom;

    public IdEtablissement() {
        super();
    }

    public IdEtablissement(final String UAI, final String nom) {
        this.UAI = UAI;
        this.nom = nom;
    }

    @JsonProperty("UAI")
    public String getUAI() {
        return UAI;
    }
    @JsonProperty("UAI")
    public void setUAI(String UAI) {
        this.UAI = UAI;
    }
    @JsonProperty("nom")
    public String getNom() {
        return nom;
    }
    @JsonProperty("nom")
    public void setNom(String nom) {
        this.nom = nom;
    }
}
