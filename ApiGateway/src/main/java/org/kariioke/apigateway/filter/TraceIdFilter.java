package org.kariioke.apigateway.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdFilter implements WebFilter {

    private static final String TRACE_ID = "traceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        final String finalTraceId = traceId;

        exchange.getResponse().getHeaders().add(TRACE_ID, finalTraceId);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.header(TRACE_ID, finalTraceId))
                .build();

        return chain.filter(mutatedExchange)
                .contextWrite(ctx -> ctx.put(TRACE_ID, finalTraceId))
                .doOnSubscribe(s -> MDC.put(TRACE_ID, finalTraceId))
                .doFinally(s -> MDC.clear());
    }
}
