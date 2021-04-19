package com.manouti.twitter.wikipedia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import reactor.core.publisher.Mono;

class WikipediaPageManagerTest {

    private WebClient webClient;

    @SuppressWarnings("unchecked")
    void prepareClient() throws IOException {
        // Mocking method call chain for WebClient...
        webClient = mock(WebClient.class);
        RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(RequestHeadersUriSpec.class);
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).uri(any(Function.class));
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).accept(MediaType.APPLICATION_JSON);
        ResponseSpec responseSec = mock(ResponseSpec.class);
        doReturn(responseSec).when(requestHeadersUriSpec).retrieve();
        Mono<ResponseEntity<String>> responseEntity = mock(Mono.class);
        doReturn(responseEntity).when(responseSec).toEntity(String.class);

        // Simulate three API responses, and later we expect to get a summary for one of the pages
        when(responseEntity.block()).thenReturn(jsonResponse("category-members-part1.json"))
                                    .thenReturn(jsonResponse("category-members-part2.json"))
                                    .thenReturn(jsonResponse("category-members-part3.json"))
                                    // followed by a request for another category
                                    .thenReturn(jsonResponse("another-category-members.json"))
                                    // followed by requests for page summary for two pages
                                    .thenReturn(jsonResponse("page-summary.json"))
                                    .thenReturn(jsonResponse("another-page-summary.json"));
    }

    @Test
    void testManager() throws WikipediaProcessingException, IOException {
        prepareClient();
        WikipediaPageManager wikipediaPageManager = new WikipediaPageManager(webClient);
        wikipediaPageManager.refreshPages(List.of(1, 2));
        Set<Integer> pages = wikipediaPageManager.getPages();
        assertEquals(Set.of(5323, 50761824, 60491, 61776737, 4234887, 2701254, 30780965, 212335, 58469254,
                54690679, 61580753, 1181008, 51245349, 65031309, 18567168, 524425, 21166866, 34549363, 8525,
                49191856, 344767, 3254510, 13363730, 39874090, 5621733, 1932246, 36462606, 2248859,
                5548053, 35117434, 232500, 3224769, 5783, 7380371, 5647807, 42177488, 66657316), pages);

        Optional<Integer> randomPage = wikipediaPageManager.getRandomPage();
        assertTrue(randomPage.isPresent());

        String pageSummary = wikipediaPageManager.getPageSummary(212335);
        assertEquals("Any kind of logic, function, expression, or theory based on the work of George Boole is considered Boolean.", pageSummary);

        String pageSummary2 = wikipediaPageManager.getPageSummary(5548053);
        assertEquals("Coding best practices are a set of informal rules that the software development community employ to help improve the quality of software.", pageSummary2);
    }

    private ResponseEntity<String> jsonResponse(String file) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/" + file);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String body = br.lines().collect(Collectors.joining("\n"));
            return new ResponseEntity<String>(body, HttpStatus.OK);
        }
    }

}
