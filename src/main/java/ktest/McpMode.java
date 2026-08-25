package ktest;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public final class McpMode {
    private volatile boolean mcp;

    McpMode() {
    }

    public void enable() {
        mcp = true;
    }

    public void disable() {
        mcp = false;
    }

    public boolean isEnabled() {
        return mcp;
    }
}
