package ru.aslastin.service;

import java.util.concurrent.CompletableFuture;

public interface Service {
    CompletableFuture<?> start() throws Exception;
    CompletableFuture<?> stop() throws Exception;
}
