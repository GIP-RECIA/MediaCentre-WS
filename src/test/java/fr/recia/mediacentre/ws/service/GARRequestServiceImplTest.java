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

import fr.recia.mediacentre.ws.config.bean.ParamValueProperty;
import fr.recia.mediacentre.ws.model.ressource.ListeRessources;
import fr.recia.mediacentre.ws.model.ressource.ListeRessourcesWrapper;
import fr.recia.mediacentre.ws.model.ressource.Ressource;
import fr.recia.mediacentre.ws.model.ressource.diffusion.ListeRessourcesDiffusables;
import fr.recia.mediacentre.ws.service.exception.GarDataIntegrityException;
import fr.recia.mediacentre.ws.service.exception.ListRequestErrorException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import fr.recia.mediacentre.ws.config.bean.HttpHostProperties;
import fr.recia.mediacentre.ws.config.bean.RessourceProperties;

class GARRequestServiceImplTest {

    /**
     * Construit un service GAR configuré avec un attribut de boucle "uai" : l'URI {@code ressourceUriTemplate}
     * (un seul placeholder {uai}) est appelée une fois par valeur de userInfos["UAIS"].
     */
    private GARRequestServiceImpl buildLoopingService(RestTemplate restTemplate, String ressourceUriTemplate) {
        GARRequestServiceImpl service = new GARRequestServiceImpl();
        service.setRemoteAccessTemplate(restTemplate);
        service.setHttpHeaders(new HttpHeaders());
        service.setStructureInfoService(ids -> new HashMap<>());

        HttpHostProperties hostConfig = new HttpHostProperties();
        hostConfig.setScheme("https");
        hostConfig.setHost("fake-gar-host");
        hostConfig.setPort(443);

        ParamValueProperty loopParam = new ParamValueProperty();
        loopParam.setParam("uai");
        loopParam.setValue("UAIS");

        RessourceProperties garConfiguration = new RessourceProperties();
        garConfiguration.setHostConfig(hostConfig);
        garConfiguration.setRessourceUri(ressourceUriTemplate);
        garConfiguration.setRessourceDiffusableUri("/api/ressourcesDiffusables/fixed");
        garConfiguration.setAttributeOnLoop("uai");
        garConfiguration.setAttributeIdEtab("uai");
        garConfiguration.setAppParams(new ArrayList<>());
        garConfiguration.setUserParams(List.of(loopParam));
        service.setGarConfiguration(garConfiguration);

        return service;
    }

    private static ResponseEntity<ListeRessourcesWrapper> responseWith(int ressourceCount) {
        List<Ressource> list = new ArrayList<>();
        for (int i = 0; i < ressourceCount; i++) {
            list.add(new Ressource());
        }
        return new ResponseEntity<>(new ListeRessourcesWrapper(new ListeRessources(list)), HttpStatus.OK);
    }

    private static Map<String, List<String>> userInfosWithUais(String... uais) {
        Map<String, List<String>> userInfos = new HashMap<>();
        userInfos.put("UAIS", List.of(uais));
        return userInfos;
    }

    @Test
    void concurrentCallsRaceOnSharedAcceptHeader() throws Exception {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        List<String> observedHeaders = new CopyOnWriteArrayList<>();

        Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenAnswer(invocation -> {
                    HttpEntity<?> entity = invocation.getArgument(2);
                    ParameterizedTypeReference<?> responseType = invocation.getArgument(3);
                    Thread.sleep(2);
                    observedHeaders.add(entity.getHeaders().getFirst("Accept"));
                    Object body = responseType.getType().toString().contains("ListeRessourcesDiffusables")
                            ? new ListeRessourcesDiffusables()
                            : new ListeRessourcesWrapper();
                    return new ResponseEntity<>(body, HttpStatus.OK);
                });

        GARRequestServiceImpl service = new GARRequestServiceImpl();
        service.setRemoteAccessTemplate(restTemplate);
        service.setHttpHeaders(new HttpHeaders());
        HttpHostProperties hostConfig = new HttpHostProperties();
        hostConfig.setScheme("https");
        hostConfig.setHost("fake-gar-host");
        hostConfig.setPort(443);

        RessourceProperties garConfiguration = new RessourceProperties();
        garConfiguration.setHostConfig(hostConfig);
        service.setGarConfiguration(garConfiguration);
        Method getRessources = GARRequestServiceImpl.class.getDeclaredMethod("getRessources", String.class);
        getRessources.setAccessible(true);
        Method getRessourcesDiffusables = GARRequestServiceImpl.class.getDeclaredMethod("getRessourcesDiffusables", String.class);
        getRessourcesDiffusables.setAccessible(true);

        ExecutorService pool = Executors.newFixedThreadPool(8);
        List<Future<?>> tasks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            tasks.add(pool.submit(() -> getRessources.invoke(service, "/some-uri")));
            tasks.add(pool.submit(() -> getRessourcesDiffusables.invoke(service, "/some-uri")));
        }
        for (Future<?> t : tasks) t.get();
        pool.shutdown();

        long jsonCount = observedHeaders.stream().filter(MediaType.APPLICATION_JSON_VALUE::equals).count();
        long xmlCount = observedHeaders.stream().filter(MediaType.APPLICATION_XML_VALUE::equals).count();
        System.out.println("JSON observé: " + jsonCount + " / XML observé: " + xmlCount + " / total: " + observedHeaders.size());
    }

    @Test
    void oneHttpClientErrorAmongSeveralEtablissementsStillReturnsTheOthers() throws Exception {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseWith(1))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .thenReturn(responseWith(1));

        GARRequestServiceImpl service = buildLoopingService(restTemplate, "/api/ressources/{uai}");

        List<Ressource> ressources = service.getRessources(userInfosWithUais("UAI1", "UAI2", "UAI3"));

        assertEquals(2, ressources.size(), "les 2 établissements qui répondent correctement doivent être conservés");
        verify(restTemplate, Mockito.times(3)).exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class));
    }

    @Test
    void oneResourceAccessExceptionAmongSeveralEtablissementsStillReturnsTheOthers() throws Exception {
        // régression du bug corrigé : avant le fix, seule HttpClientErrorException était tolérée -
        // une ResourceAccessException (timeout, connexion refusée...) faisait perdre tous les résultats déjà obtenus.
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseWith(1))
                .thenThrow(new ResourceAccessException("I/O error on GET request"))
                .thenReturn(responseWith(1));

        GARRequestServiceImpl service = buildLoopingService(restTemplate, "/api/ressources/{uai}");

        List<Ressource> ressources = service.getRessources(userInfosWithUais("UAI1", "UAI2", "UAI3"));

        assertEquals(2, ressources.size(), "les 2 établissements qui répondent correctement doivent être conservés malgré l'erreur réseau sur le 2e");
    }

    @Test
    void allEtablissementsFailingThrowsAggregatedException() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .thenThrow(new ResourceAccessException("I/O error on GET request"))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        GARRequestServiceImpl service = buildLoopingService(restTemplate, "/api/ressources/{uai}");

        ListRequestErrorException ex = assertThrows(ListRequestErrorException.class,
                () -> service.getRessources(userInfosWithUais("UAI1", "UAI2", "UAI3")));

        assertEquals(3, ex.getExceptions().size(), "les 3 erreurs doivent être accumulées, pas juste la première");
    }

    @Test
    void unresolvedUriPlaceholderAbortsImmediatelyWithoutCallingRemote() {
        // {missing} n'est jamais remplacé par la boucle sur {uai} : CustomRestRequestException, pas une
        // erreur réseau - ce cas n'est volontairement pas toléré comme les deux précédents.
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

        GARRequestServiceImpl service = buildLoopingService(restTemplate, "/api/ressources/{uai}/{missing}");

        RestClientException ex = assertThrows(RestClientException.class,
                () -> service.getRessources(userInfosWithUais("UAI1", "UAI2")));

        assertEquals("Mauvaise configuration de l'application.", ex.getMessage());
        verify(restTemplate, never()).exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class));
    }

    @Test
    void oneGarDataIntegrityIssueAmongSeveralEtablissementsStillReturnsTheOthers() throws Exception {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseWith(1))
                .thenReturn(new ResponseEntity<>(new ListeRessourcesWrapper(new ListeRessources(null)), HttpStatus.OK))
                .thenReturn(responseWith(1));

        GARRequestServiceImpl service = buildLoopingService(restTemplate, "/api/ressources/{uai}");

        List<Ressource> ressources = service.getRessources(userInfosWithUais("UAI1", "UAI2", "UAI3"));

        assertEquals(2, ressources.size(), "les 2 établissements avec une réponse valide doivent être conservés malgré une liste de ressources nulle sur le 2e");
    }

    @Test
    void allEtablissementsWithNullRessourceListThrowsAggregatedException() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(new ListeRessourcesWrapper(new ListeRessources(null)), HttpStatus.OK));

        GARRequestServiceImpl service = buildLoopingService(restTemplate, "/api/ressources/{uai}");

        ListRequestErrorException ex = assertThrows(ListRequestErrorException.class,
                () -> service.getRessources(userInfosWithUais("UAI1", "UAI2", "UAI3")));

        assertEquals(3, ex.getExceptions().size());
        ex.getExceptions().forEach(e -> assertEquals(GarDataIntegrityException.class, e.getClass()));
    }

    @Test
    void nullRessourceListOnSingleCallPathThrowsWrappedDataIntegrityException() {
        // pas d'attribut de boucle renseigné dans userInfos -> passe par fetchRessourcesOnce, pas fetchRessourcesForEachAttribute
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(new ListeRessourcesWrapper(new ListeRessources(null)), HttpStatus.OK));

        GARRequestServiceImpl service = buildLoopingService(restTemplate, "/api/ressources/fixed");

        ListRequestErrorException ex = assertThrows(ListRequestErrorException.class,
                () -> service.getRessources(new HashMap<>()));

        assertEquals(1, ex.getExceptions().size());
        assertEquals(GarDataIntegrityException.class, ex.getExceptions().get(0).getClass());
    }

    @Test
    void nullElementInRessourceListThrowsWrappedDataIntegrityException() {
        List<Ressource> listWithNullElement = new ArrayList<>();
        listWithNullElement.add(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(new ListeRessourcesWrapper(new ListeRessources(listWithNullElement)), HttpStatus.OK));

        GARRequestServiceImpl service = buildLoopingService(restTemplate, "/api/ressources/fixed");

        ListRequestErrorException ex = assertThrows(ListRequestErrorException.class,
                () -> service.getRessources(new HashMap<>()));

        assertEquals(1, ex.getExceptions().size());
        assertEquals(GarDataIntegrityException.class, ex.getExceptions().get(0).getClass());
    }

    @Test
    void nullBodyOnRessourcesDiffusablesThrowsRestClientException() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        GARRequestServiceImpl service = buildLoopingService(restTemplate, "/api/ressources/fixed");

        RestClientException ex = assertThrows(RestClientException.class, service::getRessourcesDiffusables);

        assertEquals("Réponse du GAR invalide pour les ressources diffusables.", ex.getMessage());
    }
}