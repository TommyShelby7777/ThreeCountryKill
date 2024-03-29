package com.partydeck.server.repositories;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;

/**
 * The component responsible for decoding urls
 * @author Itay Schechner
 * @version 1.0
 */
@Component
public class UrlParser {

    public Map<String, String> parse(@Nullable URI uri) {
        try {
            Map<String, String> decoded = new LinkedHashMap<>();
            String query = Optional.ofNullable(uri).orElseThrow().getQuery();

            Arrays.stream(query.split("&"))
                    .forEach(pair -> {
                        int equalsIndex = pair.indexOf("=");
                        if (equalsIndex > 0) {
                            decoded.put(pair.substring(0, equalsIndex), pair.substring(equalsIndex + 1));
                        }
                    });
            return decoded;
        } catch(Exception e) {
            return new HashMap<>();
        }
    }


}
