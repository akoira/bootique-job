package io.bootique.job.scheduler.execution;

import io.bootique.job.Job;
import io.bootique.job.JobMetadata;
import io.bootique.job.runnable.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

class SingleJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleJob.class);

    private Job delegate;
    private JobExecution execution;

    SingleJob(Job delegate, JobExecution execution) {
        this.delegate = delegate;
        this.execution = execution;
    }

    @Override
    public JobMetadata getMetadata() {
        return delegate.getMetadata();
    }

    @Override
    public JobResult run(Map<String, Object> parameters) {
        Map<String, Object> mergedParams = mergeParams(parameters, execution.getParams());
        LOGGER.info(String.format("job '%s' started with params %s", getMetadata().getName(), mergedParams));
        try {
            return delegate.run(mergedParams);
        } finally {
            LOGGER.info(String.format("job '%s' finished", getMetadata().getName()));
        }
    }

    private Map<String, Object> mergeParams(Map<String, Object> overridingParams, Map<String, Object> defaultParams) {
        Map<String, Object> merged = new HashMap<>(defaultParams);
        merged.putAll(overridingParams);
        return merged;
    }
}