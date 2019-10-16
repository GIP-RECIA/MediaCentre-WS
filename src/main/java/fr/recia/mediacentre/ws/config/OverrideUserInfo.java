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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import fr.recia.mediacentre.ws.model.ressource.IdEtablissement;
import fr.recia.mediacentre.ws.model.ressource.Ressource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by jgribonvald on 24/07/17.
 */
@Component
@Aspect
@Profile("USER_MAPPING")
@Slf4j
public class OverrideUserInfo {

    private static Map<String, List<String>> lycelv;
    private static Map<String, List<String>> lycprof;
    private static Map<String, List<String>> lycpers;
    private static Map<String, List<String>> collelv;
    private static Map<String, List<String>> collprof;
    static {
        lycelv = ImmutableMap.<String, List<String>>builder()
                .put("uid", Lists.newArrayList("F1800lsy"))
                .put("ESCOUAI", Lists.newArrayList("0290009C"))
                .put("ESCOUAICourant",Lists.newArrayList("0290009C"))
                .put("ENTPersonProfils", Lists.newArrayList("National_ELV"))
                .put( "ENTPersonGARIdentifiant", Lists.newArrayList("8f7b8af4-69d1-4972-81c6-0062375a2e1a"))
                .put("isMemberOf", Lists.newArrayList("esco:Etablissements:FICTIF_0290009C:Eleves"))
                .build();
        collelv = ImmutableMap.<String, List<String>>builder()
                .put("uid", Lists.newArrayList("F1800me5"))
                .put("ESCOUAI", Lists.newArrayList("0291595B"))
                .put("ESCOUAICourant", Lists.newArrayList("0291595B"))
                .put("ENTPersonProfils", Lists.newArrayList("National_ELV"))
                .put("ENTPersonGARIdentifiant", Lists.newArrayList("b194fef1-1de9-44e8-ab77-e34113950702"))
                .put("isMemberOf", Lists.newArrayList("esco:Etablissements:FICTIF_0291595B:Eleves"))
                .build();
        lycprof = ImmutableMap.<String, List<String>>builder()
                .put("uid", Lists.newArrayList("F1800mlt"))
                .put("ESCOUAI", Lists.newArrayList("0290009C","0291595B"))
                .put("ESCOUAICourant", Lists.newArrayList("0290009C"))
                .put("ENTPersonProfils", Lists.newArrayList("National_ENS"))
                .put("ENTPersonGARIdentifiant", Lists.newArrayList("e2ebef58-a342-4229-a3ab-401cb6c1095a"))
                .put("isMemberOf", Lists.newArrayList("esco:Etablissements:FICTIF_0291595B:Profs", "esco:Etablissements:FICTIF_0290009C:Profs",
                        "esco:Applications:Ressources_Editoriales:generation_5:FICTIF_0291595B"))
                .build();
        collprof = ImmutableMap.<String, List<String>>builder()
                .put("uid", Lists.newArrayList("F1800mod"))
                .put("ESCOUAI", Lists.newArrayList("0291595B"))
                .put("ESCOUAICourant", Lists.newArrayList("0291595B"))
                .put("ENTPersonProfils", Lists.newArrayList("National_ENS"))
                .put("ENTPersonGARIdentifiant", Lists.newArrayList("6a2c1e64-ec79-4799-a649-af0aef9d6773"))
                .put("isMemberOf", Lists.newArrayList("esco:Etablissements:FICTIF_0291595B:Profs", "esco:Applications:Ressources_Editoriales:generation_5:FICTIF_0291595B"))
                .build();
        lycpers = ImmutableMap.<String, List<String>>builder()
                .put("uid", Lists.newArrayList("F1800mms"))
                .put("ESCOUAI", Lists.newArrayList("0291595B"))
                .put("ESCOUAICourant", Lists.newArrayList("0291595B"))
                .put("ENTPersonProfils", Lists.newArrayList("National_DOC"))
                .put("ENTPersonGARIdentifiant", Lists.newArrayList("1ce97574-aff4-490b-bd9a-a44bc1cf27c2"))
                .put("isMemberOf", Lists.newArrayList("esco:Etablissements:FICTIF_0291595B:Enseignements:DOCUMENTATION"))
                .build();
    }

    @Around("execution(* fr.recia.mediacentre.ws.web.rest.RessourceListResource.getRessources(..)) && args(userInfos)")
    public Object overrideUserInfos(ProceedingJoinPoint pjp, Map<String, List<String>> userInfos) throws Throwable {
        Map<String, List<String>> modified = userInfos;

        if (userInfos != null && userInfos.containsKey("uid") && !userInfos.get("uid").isEmpty()) {
            final String val = userInfos.get("uid").get(0);
            switch (val) {
                case "F16Z0064":
                    modified =  lycelv; logMapping(userInfos, modified); break;
                case "F16Z0065":
                    modified = lycpers; logMapping(userInfos, modified); break;
                case "F16Z0066":
                    modified = lycprof; logMapping(userInfos, modified); break;
                case "F16X001n":
                    modified = collprof; logMapping(userInfos, modified); break;
                case "F16X001m":
                    modified = collelv; logMapping(userInfos, modified); break;
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
        if (objs != null && objs instanceof List) {
            for (Object obj: (List) objs) {
                if (obj instanceof Ressource) {
                    Ressource r = (Ressource) obj;
                    for (IdEtablissement s: r.getIdEtablissement()) {
                        if (s.getUAI() != null) {
                            if (s.getUAI().equals("0291595B")) {
                                s.setUAI("0377777U");
                                log.warn("Structure infos replacement : \nfrom {} \ninto {}", "0291595B", s.getUAI());
                            } else if (s.getUAI().equals("0290009C")) {
                                s.setUAI("0450822X");
                                log.warn("Structure infos replacement : \nfrom {} \ninto {}", "0290009C", s.getUAI());
                            }
                        }
                    }
                }
            }
        }
        return objs;
    }

}
