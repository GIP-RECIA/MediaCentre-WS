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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "UAI",
    "nom"
})
@ToString
@EqualsAndHashCode
public class IdEtablissement implements Serializable {

    @NotBlank
    public String id;

    public String UAI;

    @NotBlank
    public String nom;

    public IdEtablissement() {
        super();
    }

    public IdEtablissement(@NotBlank final String id, final String UAI, @NotBlank final String nom) {
        this.id = id;
        this.UAI = UAI;
        this.nom = nom;
    }

    @JsonProperty(value = "id", required = true)
    public String getId() {
        return id;
    }
    @JsonProperty(value = "id", required = true)
    public void setId(String id) {
        this.id = id;
    }
    @JsonProperty(value = "UAI")
    public String getUAI() {
        return UAI;
    }
    @JsonProperty(value = "UAI")
    public void setUAI(String UAI) {
        this.UAI = UAI;
    }
    @JsonProperty(value = "nom", required = true)
    public String getNom() {
        return nom;
    }
    @JsonProperty(value = "nom", required = true)
    public void setNom(String nom) {
        this.nom = nom;
    }
}
