package fr.recia.mediacentre.ws.service;

import fr.recia.mediacentre.ws.model.ressource.ListeRessourcesWrapper;
import fr.recia.mediacentre.ws.model.ressource.diffusion.ListeRessourcesDiffusables;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import fr.recia.mediacentre.ws.config.bean.HttpHostProperties;
import fr.recia.mediacentre.ws.config.bean.RessourceProperties;

class GARRequestServiceImplTest {

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
}