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

import fr.recia.mediacentre.ws.model.ressource.FiltreDroit;
import fr.recia.mediacentre.ws.model.ressource.FiltreDroitOnPattern;
import fr.recia.mediacentre.ws.model.ressource.OperatorDroit;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@NoArgsConstructor
@Slf4j
public class EvaluatorDroit {

    public boolean evaluate(FiltreDroit droits, Map<String, List<String>> userInfos) {
        if (droits != null && !droits.getFiltreDroitType().isEmpty()) {
            for (FiltreDroitOnPattern filtre: droits.getFiltreDroitType()) {
                final List<String> userPropValues = userInfos.getOrDefault(filtre.getAttribut(), new ArrayList<>());
                boolean andResult = false;
                for (String value: userPropValues) {
                    Matcher matcher = filtre.getCompiledPattern().matcher(value);
                    if (matcher.matches()) {
                        if (droits.getOperator() == OperatorDroit.AND) {
                            andResult = true;
                            break;
                        }
                        return true;
                    }
                }
                if (!andResult && droits.getOperator() == OperatorDroit.AND) return false;
            }
            // so operator OR aucun FiltreDroitOnPattern n'a été return true alors return false
            if (droits.getOperator() == OperatorDroit.OR) return false;
        }
        // return true si filtreDroit vide ou si au aucun AND à false
        return true;
    }
}
