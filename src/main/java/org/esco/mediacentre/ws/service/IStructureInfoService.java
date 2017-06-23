package org.esco.mediacentre.ws.service;

import java.util.List;
import java.util.Map;

import org.esco.mediacentre.ws.model.structure.Structure;

/**
 * Created by jgribonvald on 23/06/17.
 */
public interface IStructureInfoService {

    Map<String, Structure> getStructuresInfosList(final List<String> ids);
}
