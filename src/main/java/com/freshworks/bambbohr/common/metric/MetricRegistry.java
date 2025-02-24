package com.freshworks.bambbohr.common.metric;

import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.Duration;


@Component
public class MetricRegistry {

    private final PrometheusMeterRegistry prometheusRegistry;

    private final Timer histogramRequestTimer;
    private final Timer histogramLatencyTimer;

   // @Value("${TEST_DEFAULT_PERCENTILE}")
    private Double[] percentiles;



    private Double[] latencyPercentiles;


    @Autowired
    public MetricRegistry(@Value("${DEFAULT_PERCENTILE:0.9,0.95}")Double[] percentiles ,  @Value("${DEFAULT_LATENCY_PERCENTILE:0.9,0.95}")Double[] latencyPercentiles ,PrometheusMeterRegistry prometheusRegistry) {
        this.prometheusRegistry = prometheusRegistry;
        this.percentiles = percentiles;
        this.latencyPercentiles = latencyPercentiles;

        //The histogram buckets 300, 1200 ms are linked to how Apdex (indicative) is calculated in trigmetry.
        //If a change is made here, the formula in trigmetry needs to be updated accordingly.
        histogramRequestTimer = Timer.builder("http.server.requests.histo")
                .publishPercentiles(ArrayUtils.toPrimitive(percentiles)) // Default: 90th and 95th percentile
                .publishPercentileHistogram()
                .sla(Duration.ofMillis(300), Duration.ofMillis(600), Duration.ofMillis(1200))
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(30)).register(prometheusRegistry);

        histogramLatencyTimer = Timer.builder("message.latency.histo")
                .publishPercentiles(ArrayUtils.toPrimitive(latencyPercentiles)) // Default: 90th and 95th percentile
                .publishPercentileHistogram()
                .register(prometheusRegistry);


    }

    public PrometheusMeterRegistry getRegistry() {
        return prometheusRegistry;
    }

    public Timer getHistogramRequestTimer() {
        return histogramRequestTimer;
    }

    public Timer getHistogramLatencyTimer() {
        return histogramLatencyTimer;
    }

}
