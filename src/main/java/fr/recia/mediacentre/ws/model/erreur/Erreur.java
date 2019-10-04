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

package fr.recia.mediacentre.ws.model.erreur;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Code",
    "Message",
    "Resource"
})
@ToString
@EqualsAndHashCode
public class Erreur implements Serializable
{

    @JsonProperty(value = "Code", required = true)
    @NotBlank
    private String code;
    @JsonProperty(value = "Message", required = true)
    @NotBlank
    private String message;
    @JsonProperty("Resource")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String resource;

    /**
     * No args constructor for use in serialization
     *
     */
    public Erreur() {
    }

    /**
     * All args constructor.
     * @param code Code de l'erreur.
     * @param message Message de l'erreur.
     * @param resource URL/URI Rest de la request.
     */
    public Erreur(@NotBlank final String code, @NotBlank final String message, final String resource) {
        this.code = code;
        this.message = message;
        this.resource = resource;
    }

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    @JsonProperty("Code")
    public void setCode(@NotBlank final String code) {
        this.code = code;
    }

    @JsonProperty("Message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("Message")
    public void setMessage(@NotBlank final String message) {
        this.message = message;
    }

    @JsonProperty("Resource")
    public String getResource() {
        return resource;
    }

    @JsonProperty("Resource")
    public void setResource(final String resource) {
        this.resource = resource;
    }
}
