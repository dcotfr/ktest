package ktest.kafka;

import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class SearchRange {
    private final String topicName;
    private final Map<Integer, Long> currentOffsets = new HashMap<>();
    private final Map<Integer, Long> endOffsets = new HashMap<>();

    SearchRange(final String pTopicName) {
        topicName = pTopicName;
    }

    void addRange(final int pPartition, final long pStartOffset, final long pEndOffset) {
        currentOffsets.put(pPartition, pStartOffset);
        endOffsets.put(pPartition, pEndOffset);
    }

    boolean hasNext() {
        return endOffsets.keySet().stream().anyMatch(this::hasNext);
    }

    void currentOffset(final int pPartition, final long pOffset) {
        currentOffsets.put(pPartition, pOffset);
    }

    List<TopicPartition> partitionsHavingNext() {
        return endOffsets.keySet().stream()
                .filter(this::hasNext)
                .map(p -> new TopicPartition(topicName, p))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean hasNext(final int pPartition) {
        return currentOffsets.get(pPartition) < endOffsets.get(pPartition);
    }
}
