package ktest.kafka.proto;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public final class Json2ProtoConverter {
    public DynamicMessage toProtobuf(final JsonNode pJsonNode, final Descriptors.Descriptor pProtobufDescriptor) {
        if (pJsonNode == null || pJsonNode.isNull()) {
            return null;
        }
        if (pJsonNode.isTextual()) {
            throw new ProtoGenException("Cannot serialize scalar JSON value '" + pJsonNode + "' as Protobuf message '" + pProtobufDescriptor.getFullName() + "'");
        }
        if (pProtobufDescriptor == null) {
            throw new ProtoGenException("Cannot serialize JSON value '" + pJsonNode + "' because of missing Protobuf descriptor");
        }
        try {
            final var builder = DynamicMessage.newBuilder(pProtobufDescriptor);
            JsonFormat.parser().ignoringUnknownFields().merge(pJsonNode.toString(), builder);
            return builder.build();
        } catch (final Exception e) {
            throw new ProtoGenException("Failed to convert JSON value '" + pJsonNode + "' to Protobuf message '" + pProtobufDescriptor.getFullName() + '\'', e);
        }
    }
}