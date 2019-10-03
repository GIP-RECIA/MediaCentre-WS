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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ressource"
})
@ToString
@EqualsAndHashCode
public class ListeLocaleRessources implements Serializable {

    @Valid
    private List<LocalRessource> ressource = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public ListeLocaleRessources() {
        super();
    }

    /**
     *
     * @param ressource
     */
    public ListeLocaleRessources(List<LocalRessource> ressource) {
        super();
        this.ressource = ressource;
    }

    @JsonProperty("ressource")
    public List<LocalRessource> getRessource() {
        return ressource;
    }

    @JsonProperty("ressource")
    public void setRessource(List<LocalRessource> ressource) {
        this.ressource = ressource;
    }

}

