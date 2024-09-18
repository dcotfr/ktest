package ktest.kafka;

public record TopicRef(String broker, String topic, Serde keySerde, Serde valueSerde) {
    public String id() {
        return topic + "@" + broker;
    }
}
