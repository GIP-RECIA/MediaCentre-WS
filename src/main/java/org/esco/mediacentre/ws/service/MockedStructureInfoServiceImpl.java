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

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.model.structure.Structure;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by jgribonvald on 23/06/17.
 */
@Service
@Profile("WITHOUT_STRUCT_REST")
@Slf4j
public class MockedStructureInfoServiceImpl implements IStructureInfoService {

    @Override
    public Map<String, Structure> getStructuresInfosList(final List<String> ids) {
        log.debug("Returning mocked datas for runtime needs - " +
                "\nThis is due to runing with profile 'WITHOUT_STRUCT_REST' and a willing to doesn't make rest call on portal (or due to filtered acces on REST and doesn't able to get on) ");
        Map<String, Structure> map = Maps.newHashMap();
        for (String id: ids) {
            map.put(id, new Structure(id, "Mocked Établissement " + id, "Mocked Établissement avec l'id " + id, null, null, id));
        }
        return map;
    }

}
