package fr.dcotton.ktest.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

class LogOutputStream extends OutputStream {
    private static final Logger LOG = LoggerFactory.getLogger(LogOutputStream.class);

    private final Level level;
    private StringBuilder buf;

    public LogOutputStream(final Level pLevel) {
        level = pLevel;
        buf = new StringBuilder();
    }

    @Override
    public void write(final int pByte) throws IOException {
        if (pByte == 0) {
            return;
        }
        buf.append((char) pByte);
    }

    @Override
    public void flush() {
        if (buf.isEmpty()) {
            return;
        }
        Arrays.stream(buf.toString().split(System.lineSeparator())).forEach(l -> LOG.atLevel(level).log(l));
        buf = new StringBuilder();
    }
}
