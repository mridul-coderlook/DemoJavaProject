package com.coderlook.chatrachatri.subscriptions.exceptions;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Java custom exceptions are used to customize the exception according to user need. 
 * By the help of custom exception, you can have your own exception and message. 
 * 
 */
public class EntityNotFoundException extends RuntimeException {
	  /**
     * Handle EntityNotFoundException. Triggered when a Object is missing
     *
     * @param clazz
     * @param searchParamsMap 
     * @return RuntimeException
     */
    public EntityNotFoundException(Class clazz, String... searchParamsMap) {
        super(EntityNotFoundException.generateMessage(clazz.getSimpleName(), toMap(String.class, String.class, searchParamsMap)));
    }
    /**
     * It is being used for generate message while needed from other exception method
     *
     * @param entity
     * @param searchParams 
     * @return String message
     */
    private static String generateMessage(String entity, Map<String, String> searchParams) {
        return StringUtils.capitalize(entity) +
                " was not found for parameters " +
                searchParams;
    }
    /**
     * It is being used to convert set of keys and set of values to map object
     *
     * @param keyType
     * @param valueType
     * @param entries 
     * @return Map<K, V>
     */
    private static <K, V> Map<K, V> toMap(
            Class<K> keyType, Class<V> valueType, Object... entries) {
        if (entries.length % 2 == 1)
            throw new IllegalArgumentException("Invalid entries");
        return IntStream.range(0, entries.length / 2).map(i -> i * 2)
                .collect(HashMap::new,
                        (m, i) -> m.put(keyType.cast(entries[i]), valueType.cast(entries[i + 1])),
                        Map::putAll);
    }

}