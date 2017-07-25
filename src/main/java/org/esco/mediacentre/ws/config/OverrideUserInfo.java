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
        lycelv = ImmutableMap.of("uid", Lists.newArrayList("F1600m3y"),"ESCOUAI", Lists.newArrayList("0290009C"),"ESCOUAICourant",
                Lists.newArrayList("0290009C"), "ENTPersonProfils", Lists.newArrayList("National_ELV"));
        collelv = ImmutableMap.of("uid", Lists.newArrayList("F1600m40"),"ESCOUAI", Lists.newArrayList("0291595B"),"ESCOUAICourant",
                Lists.newArrayList("0291595B"), "ENTPersonProfils", Lists.newArrayList("National_ELV"));
        lycprof = ImmutableMap.of("uid", Lists.newArrayList("F1600m5c"),"ESCOUAI", Lists.newArrayList("0290009C","0291595B"),"ESCOUAICourant",
                Lists.newArrayList("0290009C"), "ENTPersonProfils", Lists.newArrayList("National_ENS"));
        collprof = ImmutableMap.of("uid", Lists.newArrayList("F1600m5c"),"ESCOUAI", Lists.newArrayList("0291595B"),"ESCOUAICourant",
                Lists.newArrayList("0291595B"), "ENTPersonProfils", Lists.newArrayList("National_ENS"));
        lycpers = ImmutableMap.of("uid", Lists.newArrayList("F1600m5c"),"ESCOUAI", Lists.newArrayList("0290009C"),"ESCOUAICourant",
                Lists.newArrayList("0290009C"), "ENTPersonProfils", Lists.newArrayList("National_DOC"));
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
