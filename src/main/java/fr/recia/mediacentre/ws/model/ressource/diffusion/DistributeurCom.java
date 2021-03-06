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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "distributeurCom",
    "nomDistributeurCom"
})
@ToString
@EqualsAndHashCode
public class DistributeurCom implements Serializable {

    @NonNull
    @JacksonXmlProperty(localName = "distributeurCom")
    public String distributeurCom;
    @NonNull
    @JacksonXmlProperty(localName = "nomDistributeurCom")
    public String nomDistributeurCom;

    /**
     * No args constructor for use in serialization
     *
     */
    public DistributeurCom() {
        super();
    }

    public DistributeurCom(@NonNull final String distributeurCom, @NonNull final String nomDistributeurCom) {
        this.distributeurCom = distributeurCom;
        this.nomDistributeurCom = nomDistributeurCom;
    }

    @JsonProperty(value = "distributeurCom", required = true)
    public String getDistributeurCom() {
        return distributeurCom;
    }
    @JsonProperty(value = "distributeurCom", required = true)
    public void setDistributeurCom(String distributeurCom) {
        this.distributeurCom = distributeurCom;
    }
    @JsonProperty(value = "nomDistributeurCom", required = true)
    public String getNomDistributeurCom() {
        return nomDistributeurCom;
    }
    @JsonProperty(value = "nomDistributeurCom", required = true)
    public void setNomDistributeurCom(String nomDistributeurCom) {
        this.nomDistributeurCom = nomDistributeurCom;
    }
}
