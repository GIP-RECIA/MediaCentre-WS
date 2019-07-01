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
package fr.recia.mediacentre.ws.config.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RessourceProperties {

	@NotBlank
	private String ressourceUri;
	private String ressourceDiffusableUri;
	private String attributeOnLoop;
	private String attributeIdEtab;
	@NotEmpty
	private List<ParamValueProperty> appParams;
	@NotEmpty
	private List<ParamValueProperty> userParams;
	@NotNull
	private HttpHostProperties hostConfig;
	private List<ParamValueProperty> headers;

	private String clientKeyAlias;

	private Map<String,List<String>> authorizedUsers = new HashMap<>();

	private Map<String, List<String>> testUser;

}
