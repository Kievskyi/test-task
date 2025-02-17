package com.denysdudnik;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
@Data
public class DocumentManager {
    private List<Document> documents = new ArrayList<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null) {
            document.setId(UUID.randomUUID().toString());
        }
        documents.add(document);

        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documents.stream()
                .filter(document -> matchesTitlePrefixes(document, request.getTitlePrefixes()))
                .filter(document -> matchesContents(document, request.getContainsContents()))
                .filter(document -> matchesAuthorIds(document, request.getAuthorIds()))
                .filter(document -> matchesCreatedFrom(document, request.getCreatedFrom()))
                .filter(document -> matchesCreatedTo(document, request.getCreatedTo()))
                .toList();
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return documents.stream()
                .filter(document -> document.getId().equals(id))
                .findFirst();
    }

    private boolean matchesTitlePrefixes(Document document, List<String> titlePrefixes) {
        return titlePrefixes == null || titlePrefixes.stream().anyMatch(prefix -> document.getTitle().startsWith(prefix));
    }

    private boolean matchesContents(Document document, List<String> containsContents) {
        return containsContents == null || containsContents.stream().anyMatch(content -> document.getContent().contains(content));
    }

    private boolean matchesAuthorIds(Document document, List<String> authorIds) {
        return authorIds == null || authorIds.contains(document.getAuthor().getId());
    }

    private boolean matchesCreatedFrom(Document document, Instant createdFrom) {
        return createdFrom == null || !createdFrom.isAfter(document.getCreated());
    }

    private boolean matchesCreatedTo(Document document, Instant createdTo) {
        return createdTo == null || !createdTo.isBefore(document.getCreated());
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
