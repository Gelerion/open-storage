package com.gelerion.open.storage.api.copy.factory;

import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.copy.spi.CopyTaskProviderSpi;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class CopyTaskFactory {

    private final static Map<String, CopyTaskProviderSpi> PROVIDERS = StreamSupport
            .stream(ServiceLoader.load(CopyTaskProviderSpi.class).spliterator(), false)
            .collect(toMap(CopyTaskProviderSpi::scheme, identity()));

    public static CopyTaskProviderSpi getProvider(String scheme) {
        Objects.requireNonNull(scheme);
        return PROVIDERS.get(scheme);
    }

}
