package fr.recia.mediacentre.ws.config;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import fr.recia.mediacentre.ws.config.bean.UserKeyMapping;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "key-overrides")
@Validated
@Data
@Slf4j
public class KeyOverridesConfiguration {

    private List<UserKeyMapping> userKeyMappingList;

    private Map<String, String> structureKeyMappingList;

    @PostConstruct
    public void debug() {
        log.debug("Keys Override Configuration users {} and Structures {} ", userKeyMappingList, structureKeyMappingList );
    }

}
