package org.tuvarna.chat.utils;

import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;

public class UniUtils {

    public static <T> Uni<T> processFailures(Uni<T> uni, Logger log, int retryAmount) {
        return uni.onFailure().invoke(err ->
                        log.error("Batch processing failed, will retry...", err))
                .onFailure().retry().atMost(retryAmount)
                .onFailure().invoke(err ->
                        log.error("Batch processing failed after retries", err));
    }

}
