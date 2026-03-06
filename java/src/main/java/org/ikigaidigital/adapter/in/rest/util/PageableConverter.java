package org.ikigaidigital.adapter.in.rest.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableConverter {

    private PageableConverter() {
    }

    /**
     * Converts query parameters into a Spring {@link Pageable}.
     *
     * @param page zero-based page index
     * @param size page size
     * @param sort comma-separated field and direction (e.g. "id,asc")
     * @return pageable instance
     */
    public static Pageable toPageable(Integer page, Integer size, String sort) {
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = parts.length > 1
                ? Sort.Direction.fromString(parts[1].trim())
                : Sort.Direction.ASC;
            return PageRequest.of(pageNumber, pageSize, Sort.by(direction, property));
        }

        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));
    }
}
