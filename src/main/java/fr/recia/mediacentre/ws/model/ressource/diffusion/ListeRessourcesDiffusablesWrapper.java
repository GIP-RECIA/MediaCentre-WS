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

package fr.recia.mediacentre.ws.model.ressource.diffusion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.Valid;
import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "listeRessourcesDiffusables"
})
@ToString
@EqualsAndHashCode
public class ListeRessourcesDiffusablesWrapper implements Serializable {

    @Valid
    private ListeRessourcesDiffusables listeRessourcesDiffusables;

    /**
     * No args constructor for use in serialization
     *
     */
    public ListeRessourcesDiffusablesWrapper() {
        super();
    }

    /**
     *
     * @param listeRessourcesDiffusables
     */
    public ListeRessourcesDiffusablesWrapper(ListeRessourcesDiffusables listeRessourcesDiffusables) {
        super();
        this.listeRessourcesDiffusables = listeRessourcesDiffusables;
    }

    @JsonProperty("listeRessourcesDiffusables")
    public ListeRessourcesDiffusables getListeRessourcesDiffusables() {
        return listeRessourcesDiffusables;
    }

    @JsonProperty("listeRessourcesDiffusables")
    public void setListeRessourcesDiffusables(ListeRessourcesDiffusables listeRessourcesDiffusables) {
        this.listeRessourcesDiffusables = listeRessourcesDiffusables;
    }

}
