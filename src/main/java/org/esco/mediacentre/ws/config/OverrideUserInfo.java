package org.esco.mediacentre.ws.config;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    private static Map<String, List<String>> lycelv;
    private static Map<String, List<String>> lycprof;
    private static Map<String, List<String>> lycpers;
    private static Map<String, List<String>> collelv;
    private static Map<String, List<String>> collprof;
    static {
        lycelv = ImmutableMap.of("uid", Lists.newArrayList("F1700ivg"),"ESCOUAI", Lists.newArrayList("0290009C"),"ESCOUAICourant",
                Lists.newArrayList("0290009C"), "ENTPersonProfils", Lists.newArrayList("National_ELV"), "ENTPersonGARIdentifiant",
                Lists.newArrayList("5a6c685b-9f00-4cf9-ba84-5c29f8acd6f5"));
        collelv = ImmutableMap.of("uid", Lists.newArrayList("F1700jef"),"ESCOUAI", Lists.newArrayList("0291595B"),"ESCOUAICourant",
                Lists.newArrayList("0291595B"), "ENTPersonProfils", Lists.newArrayList("National_ELV"), "ENTPersonGARIdentifiant",
                Lists.newArrayList("5c9f5a97-56ec-4218-8cfc-7916dab37e7a"));
        lycprof = ImmutableMap.of("uid", Lists.newArrayList("F1700k0b"),"ESCOUAI", Lists.newArrayList("0290009C"),"ESCOUAICourant",
                Lists.newArrayList("0290009C"), "ENTPersonProfils", Lists.newArrayList("National_ENS"), "ENTPersonGARIdentifiant",
                Lists.newArrayList("7e0f273f-ad5d-43cd-b63d-ae6c3f8b6451"));
        collprof = ImmutableMap.of("uid", Lists.newArrayList("F1700k0o"),"ESCOUAI", Lists.newArrayList("0291595B"),"ESCOUAICourant",
                Lists.newArrayList("0291595B"), "ENTPersonProfils", Lists.newArrayList("National_ENS"), "ENTPersonGARIdentifiant",
                Lists.newArrayList("b6ffca54-5f54-4bfe-b0e8-504e2f0a2722"));
        lycpers = ImmutableMap.of("uid", Lists.newArrayList("F1700k17"),"ESCOUAI", Lists.newArrayList("0290009C","0291595B"),"ESCOUAICourant",
                Lists.newArrayList("0290009C"), "ENTPersonProfils", Lists.newArrayList("National_DOC"), "ENTPersonGARIdentifiant",
                Lists.newArrayList("68fc7ecd-1a51-4005-b5fd-bb20375d6528"));
    }

    @Around("execution(* org.esco.mediacentre.ws.web.rest.RessourceListResource.getRessources(..)) && args(userInfos)")
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
        log.debug("Apply User info replacement : \nfrom {} \ninto {}", userInfos, newUserInfos);
    }
}
