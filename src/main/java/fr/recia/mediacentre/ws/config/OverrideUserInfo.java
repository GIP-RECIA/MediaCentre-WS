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
package fr.recia.mediacentre.ws.config;

import java.util.List;
import java.util.Map;

import fr.recia.mediacentre.ws.config.bean.UserKeyMapping;
import fr.recia.mediacentre.ws.model.ressource.IdEtablissement;
import fr.recia.mediacentre.ws.model.ressource.Ressource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by jgribonvald on 24/07/17.
 */
@Component
@Aspect
@Profile("USER_MAPPING")
@Slf4j
public class OverrideUserInfo {

    @Autowired
    private KeyOverridesConfiguration overrideConfiguration;

    @Around("execution(* fr.recia.mediacentre.ws.web.rest.RessourceListResource.getRessources(..)) && args(userInfos)")
    public Object overrideUserInfos(ProceedingJoinPoint pjp, Map<String, List<String>> userInfos) throws Throwable {
        Map<String, List<String>> modified = userInfos;

        if (userInfos != null && userInfos.containsKey("uid") && !userInfos.get("uid").isEmpty()) {
            final String val = userInfos.get("uid").get(0);
            if (overrideConfiguration.getUserKeyMappingList() != null) {
                for (final UserKeyMapping mapping: overrideConfiguration.getUserKeyMappingList()) {
                    if (mapping.getUidToReplace().equalsIgnoreCase(val)) {
                        modified = mapping.getUserInfos();
                        logMapping(userInfos, modified);
                    }
                }
            }
        }

        return pjp.proceed(new Object[] {modified});
    }

    private void logMapping(Map<String, List<String>> userInfos, Map<String, List<String>> newUserInfos) {
        log.warn("Apply User info replacement : \nfrom {} \ninto {}", userInfos, newUserInfos);
    }

    @Around("execution(* fr.recia.mediacentre.ws.service.GARRequestServiceImpl.getRessources(..))")
    public Object overrideStrucInfos(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args=pjp.getArgs();
        Object objs = pjp.proceed(args);
        if (overrideConfiguration.getStructureKeyMappingList() != null && objs != null && objs instanceof List) {
            final Map<String, String> structMapping = overrideConfiguration.getStructureKeyMappingList();
            for (Object obj: (List) objs) {
                if (obj instanceof Ressource) {
                    Ressource r = (Ressource) obj;
                    for (IdEtablissement s: r.getIdEtablissement()) {
                        if (s.getUAI() != null && structMapping.containsKey(s.getUAI())) {
                            log.warn("Structure infos replacement : \nfrom {} \ninto {}", s.getUAI() , structMapping.get(s.getUAI()));
                            s.setUAI(structMapping.get(s.getUAI()));
                        }
                    }
                }
            }
        }
        return objs;
    }

}
