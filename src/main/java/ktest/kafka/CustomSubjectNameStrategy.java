package ktest.kafka;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.serializers.subject.strategy.SubjectNameStrategy;
import io.quarkus.runtime.annotations.RegisterForReflection;
import ktest.core.Strings;

import java.util.HashMap;
import java.util.Map;

@RegisterForReflection(registerFullHierarchy = true)
public class CustomSubjectNameStrategy implements SubjectNameStrategy {
    private static final Map<String, String> forcedSchemas = new HashMap<>();

    static void define(final String pTopic, final String pForcedKeySchema, final String pForcedValueSchema) {
        forcedSchemas.put(pTopic + "#true", pForcedKeySchema);
        forcedSchemas.put(pTopic + "#false", pForcedValueSchema);
    }

    @Override
    public void configure(final Map<String, ?> pMap) {
        // Nothing to configure
    }

    @Override
    public boolean usesSchema() {
        return true;
    }

    @Override
    public String subjectName(final String pTopic, final boolean pIsKey, final ParsedSchema pSchema) {
        final String forcedSchema = forcedSchemas.get(pTopic + "#" + pIsKey);
        if (Strings.isNullOrEmpty(forcedSchema)) {
            return (pTopic != null && pSchema != null) ? pTopic + (pIsKey ? "-key" : "-value") : null;
        }
        return forcedSchema;
    }
}
