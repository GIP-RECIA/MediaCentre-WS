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
package fr.recia.mediacentre.ws.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.google.common.collect.Sets;
import fr.recia.mediacentre.ws.service.util.Pair;
import lombok.extern.slf4j.Slf4j;
import fr.recia.mediacentre.ws.model.structure.Structure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by jgribonvald on 23/06/17.
 */
@Service
@Profile("!WITHOUT_STRUCT_REST")
@Slf4j
public class StructureInfoServiceImpl implements IStructureInfoService {

    @Autowired
    private StructureInfoRequestService structureInfoRequestService;

    @Value("${structureInfoRest.expiringDataDuration:3600}")
    //duration in second
    private int duration;

    private Map<String, Pair<Structure, Date>> structureMap = new ConcurrentHashMap<>();

    @Override
    public Map<String, Structure> getStructuresInfosList(final List<String> ids) {
        log.debug("Requesting Structures infos on {}", ids);
        Set<String> idsToRequest = Sets.newHashSet();
        Map<String, Structure> structuresInfos = new HashMap<>();

        final Date now = new Date();
        log.debug("Current date is {}", now);

        for (final String id : ids){
            final Pair<Structure, Date> structMap = structureMap.get(id);
            if (structMap != null) {
                structuresInfos.put(id, structMap.getKey());
                // expired data check to refresh if can
                if (structMap.getValue().before(now)) {
                    idsToRequest.add(id);
                }
            } else {
                idsToRequest.add(id);
            }
        }

        if (!idsToRequest.isEmpty()) {
            final Date expirationDate = new Date(now.getTime() + duration * 1000);
            log.debug("New expiring date is {}, from {}", expirationDate, now);
            Map<String, Structure> requestedStructures = structureInfoRequestService.getStructuresInfos(idsToRequest);
            for (Map.Entry<String, Structure> entry : requestedStructures.entrySet()) {
                structuresInfos.put(entry.getKey(), entry.getValue());
                structureMap.put(entry.getKey(), new Pair<Structure, Date>(entry.getValue(), expirationDate));
            }
        }
        log.debug("Returning infos on {}", structuresInfos);
        return structuresInfos;
    }

    @PostConstruct
    private void afterInit() {
        log.debug("Duration to keep structure data is setted to {} seconds", duration);
    }

}
