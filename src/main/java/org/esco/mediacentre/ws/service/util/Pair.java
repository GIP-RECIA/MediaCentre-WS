package org.esco.mediacentre.ws.service.util;

import lombok.Data;
import lombok.NonNull;

@Data
public class Pair<K,V> {
    @NonNull
    private K key;
    @NonNull
    private V value;
}
