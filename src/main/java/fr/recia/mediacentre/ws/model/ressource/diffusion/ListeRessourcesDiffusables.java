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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "dateGeneration",
        "ressourceDiffusable"
})
@ToString
@EqualsAndHashCode
@JacksonXmlRootElement(localName = "listeRessourcesDiffusables" )
public class ListeRessourcesDiffusables implements Serializable {

    @JacksonXmlProperty(localName = "dateGeneration")
    private String dateGeneration;

    @Valid
    @JacksonXmlElementWrapper(localName = "ressourceDiffusable",useWrapping = false)
    @JacksonXmlProperty(localName = "ressourceDiffusable")
    private List<RessourceDiffusable> ressourceDiffusable = new ArrayList<RessourceDiffusable>();

    /**
     * No args constructor for use in serialization
     *
     */
    public ListeRessourcesDiffusables() {
        super();
    }

    /**
     *
     * @param ressourceDiffusable
     */
    public ListeRessourcesDiffusables(List<RessourceDiffusable> ressourceDiffusable) {
        super();
        this.ressourceDiffusable = ressourceDiffusable;
    }

    @JsonProperty("ressourceDiffusable")
    public List<RessourceDiffusable> getRessourceDiffusable() {
        return ressourceDiffusable;
    }

    @JsonProperty("ressourceDiffusable")
    public void setRessourceDiffusable(List<RessourceDiffusable> ressourceDiffusable) {
        this.ressourceDiffusable = ressourceDiffusable;
    }

    @JsonProperty("dateGeneration")
    public String getDateGeneration() {
        return dateGeneration;
    }

    @JsonProperty("dateGeneration")
    public void setDateGeneration(String dateGeneration) {
        this.dateGeneration = dateGeneration;
    }


}

