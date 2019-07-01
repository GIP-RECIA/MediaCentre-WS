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

import java.io.Serializable;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "listeRessources"
})
@ToString
@EqualsAndHashCode
public class ListeRessourcesWrapper implements Serializable {

    @Valid
    private ListeRessources listeRessources;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ListeRessourcesWrapper() {
        super();
    }

    /**
     * 
     * @param listeRessources
     */
    public ListeRessourcesWrapper(ListeRessources listeRessources) {
        super();
        this.listeRessources = listeRessources;
    }

    @JsonProperty("listeRessources")
    public ListeRessources getListeRessources() {
        return listeRessources;
    }

    @JsonProperty("listeRessources")
    public void setListeRessources(ListeRessources listeRessources) {
        this.listeRessources = listeRessources;
    }

}
