package com.manouti.twitter.wikipedia;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * This class is responsible for managing the category pages that source the tweet content from Wikipedia.
 *
 * @author manouti
 *
 */
public final class WikipediaPageManager implements IWikipediaPageManager {

    private static final String BASE_URL = "https://ar.wikipedia.org/w/api.php";
    private static final String ACTION_QUERY_PARAM = "action";
    private static final String QUERY_ACTION = "query";
    private static final String FORMAT_QUERY_PARAM = "format";
    private static final String JSON_FORMAT = "json";

    private static final int MAX_PAGES_COUNT = 50000;
    private static final int MAX_QUEUE_CAPACITY = 300;

    private static final Logger log = LoggerFactory.getLogger(WikipediaPageManager.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Set<Integer> pages = new HashSet<>();

    public WikipediaPageManager() {
        this(WebClient.create(BASE_URL));
    }

    WikipediaPageManager(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void refreshPages(List<Integer> categoryPageIds) throws WikipediaProcessingException {
        pages.clear();
        Set<Integer> visitedCategories = new HashSet<>();
        Queue<Integer> queue = new PriorityQueue<>(randomComparator());

        for (int categoryPageId : categoryPageIds) {
            visitedCategories.add(categoryPageId);
            queue.add(categoryPageId);

            boolean stopEnqueueing = false;
            while (!queue.isEmpty() && pages.size() < MAX_PAGES_COUNT) {
                if (queue.size() >= MAX_QUEUE_CAPACITY) {
                    stopEnqueueing = true;
                }
                int categoryid = queue.poll();
                log.info("Processing category " + categoryid);

                String continueValue = null;
                do {
                    JsonNode rootNode = getCategoryMembers(categoryid, continueValue);
                    fillCategoryMemberPageIds(rootNode, pages, queue, stopEnqueueing, visitedCategories);
                    continueValue = getContinueValue(rootNode);
                } while (continueValue != null);
            }
        }
    }

    private Comparator<Integer> randomComparator() {
        Random random = new Random();
        return (i1, i2) -> Integer.compare(random.nextInt(), random.nextInt());
    }

    @Override
    public Optional<Integer> getRandomPage() {
        if(pages.isEmpty()) {
            return Optional.empty();
        }
        Random random = new Random();
        int index = random.nextInt(pages.size());
        Iterator<Integer> iterator = pages.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return Optional.of(iterator.next());
    }

    @Override
    public String getPageSummary(int id) throws WikipediaProcessingException {
        ResponseEntity<String> responseEntity = webClient.get()
                 .uri(uriBuilder -> uriBuilder.queryParam(ACTION_QUERY_PARAM, QUERY_ACTION)
                         .queryParam("prop", "extracts")
                         .queryParam("exintro")
                         .queryParam("explaintext")
                         .queryParam("redirects", 1)
                         .queryParam("pageids", id)
                         .queryParam(FORMAT_QUERY_PARAM, JSON_FORMAT)
                         .build())
                 .accept(MediaType.APPLICATION_JSON)
                 .retrieve()
                 .toEntity(String.class)
                 .block();
        String body = responseEntity.getBody();
        try {
            JsonNode rootNode = objectMapper.readValue(body, JsonNode.class);
            JsonNode extractNode = rootNode.get("query").get("pages").get(String.valueOf(id)).get("extract");
            if (extractNode != null) {
                return extractNode.asText();
            }
            return null;
        } catch (JsonProcessingException e) {
            throw new WikipediaProcessingException(e);
        }
    }

    public Set<Integer> getPages() {
        return pages;
    }

    private JsonNode getCategoryMembers(int categoryPageId, String continueValue) throws WikipediaProcessingException {
        ResponseEntity<String> responseEntity = webClient.get()
                         .uri(uriBuilder -> uriBuilder.queryParam(ACTION_QUERY_PARAM, QUERY_ACTION)
                         .queryParam("list", "categorymembers")
                         .queryParam("cmpageid", categoryPageId)
                         .queryParam(FORMAT_QUERY_PARAM, JSON_FORMAT)
                         .queryParamIfPresent("cmcontinue", Optional.ofNullable(continueValue))
                         .queryParam("cmprop", "type|ids|title")
                         .queryParam("cmlimit", 500)
                         .build())
                 .accept(MediaType.APPLICATION_JSON)
                 .retrieve()
                 .toEntity(String.class)
                 .block();
        String body = responseEntity.getBody();
        try {
            return objectMapper.readValue(body, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new WikipediaProcessingException(e);
        }
    }

    private static void fillCategoryMemberPageIds(JsonNode rootNode, Set<Integer> pages, Queue<Integer> categoryQueue, boolean stopEnqueueing, Set<Integer> visitedCategories) {
        JsonNode queryNode = rootNode.get("query");
        if (queryNode == null) {
            return;
        }
        ArrayNode categoryMembers = (ArrayNode) queryNode.get("categorymembers");
        if (categoryMembers == null) {
            return;
        }
        for (JsonNode member : categoryMembers) {
            int pageId = member.get("pageid").asInt();
            String pageType = member.get("type").asText();
            if (pageType.equals("subcat")) {
                if (!visitedCategories.contains(pageId) && !stopEnqueueing) {
                    visitedCategories.add(pageId);
                    categoryQueue.offer(pageId);
                }
            } else if (pageType.equals("page")) {
                pages.add(pageId);
            }
        }
    }

    private static String getContinueValue(JsonNode rootNode) {
        JsonNode continueNode = rootNode.get("continue");
        if (continueNode != null) {
            JsonNode cmContinueNode = continueNode.get("cmcontinue");
            if (cmContinueNode != null) {
                return cmContinueNode.asText();
            }
        }
        return null;
    }

}
