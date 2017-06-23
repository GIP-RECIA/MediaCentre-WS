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
package org.esco.mediacentre.ws.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.config.StructureRestClientConfiguration;
import org.esco.mediacentre.ws.config.bean.StructureInfosRestProperties;
import org.esco.mediacentre.ws.model.structure.MapStructuresWrapper;
import org.esco.mediacentre.ws.model.structure.Structure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jgribonvald on 15/06/17.
 */
@Service
@Data
@Slf4j
public class StructureInfoRequestService {

    @Autowired
    @Qualifier(value = "StructureRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private StructureRestClientConfiguration structureRestClientConfiguration;

    @Autowired
    private StructureInfosRestProperties restProperties;

    public Map<String,Structure> getStructuresInfos(@NotNull final Set<String> structuresIds) {
        try {
            final MapStructuresWrapper map = runCallAPI(structuresIds);
            if (log.isDebugEnabled()) {
                log.debug("Obtained Structure List from {} returned \n{}", structuresIds, map.getMapStructures());
            }
            return map.getMapStructures();
        } catch (CustomRestRequestException e) {
            return Maps.newLinkedHashMap();
        }
    }

    public MapStructuresWrapper runCallAPI(@NotNull final Set<String> structuresIds) throws CustomRestRequestException {
       if (!structuresIds.isEmpty()){
           String ids = "";
           for (String id: structuresIds) {
               if (!"".equals(ids)) {
                   ids += ",";
               }
               if (StringUtils.hasText(id)) {
                   ids+= id;
               }
           }
           try {
               final URI uri =  new URI(restProperties.getHostConfig().getScheme() + "://" + restProperties.getHostConfig().getHost() +
                       (restProperties.getHostConfig().getPort() > 0 ? ":" + restProperties.getHostConfig().getPort() : "") + restProperties.getUri() + ids);

               if (log.isDebugEnabled()) {
                   log.debug("Requesting uri {}", uri.toString());
               }

               HttpHeaders headers = new HttpHeaders();
               headers.put("Accept", Lists.newArrayList(MediaType.APPLICATION_JSON_VALUE));
               headers.put("Accept-Charset", Lists.newArrayList("UTF-8"));
               headers.put("Accept-Encoding", Lists.newArrayList("gzip, deflate"));

               HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);

               final ResponseEntity<HashMap<String,Structure>> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<HashMap<String,Structure>>(){});

               if (log.isDebugEnabled()) {
                   log.debug("Calling Structure info on {} returned a response with status {} and \n" +
                                   "\nresponse{}\n",
                           structuresIds, response.getStatusCode(), response);
               }
               return new MapStructuresWrapper(response.getBody());
           } catch (URISyntaxException e) {
               log.error("Error to construct the URL !", e);
               throw new CustomRestRequestException(e);
           }
       }
       return new MapStructuresWrapper();
    }


}
