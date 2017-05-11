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

package org.esco.mediacentre.ws.model.structure;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "displayName",
    "description",
    "otherAttributes",
    "code"
})
@ToString
@EqualsAndHashCode
public class Structure implements Serializable
{

    @JsonProperty("id")
    private String id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("otherAttributes")
    @Valid
    private Map<String, List<String>> otherAttributes;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Structure() {
    }

    /**
     * 
     * @param id
     * @param description
     * @param name
     * @param code
     * @param otherAttributes
     * @param displayName
     */
    public Structure(String id, String name, String displayName, String description, Map<String, List<String>> otherAttributes, String code) {
        super();
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.otherAttributes = otherAttributes;
        this.code = code;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("otherAttributes")
    public Map<String, List<String>> getOtherAttributes() {
        return otherAttributes;
    }

    @JsonProperty("otherAttributes")
    public void setOtherAttributes(Map<String, List<String>> otherAttributes) {
        this.otherAttributes = otherAttributes;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

}
