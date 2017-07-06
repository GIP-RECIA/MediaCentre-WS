package org.esco.mediacentre.ws.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.google.common.collect.Sets;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.model.structure.Structure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by jgribonvald on 23/06/17.
 */
@Service
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
            final Date expirationDate = new Date(now.getTime() + duration);
            log.debug("Expiring date is {}, from {}", expirationDate, now);
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
