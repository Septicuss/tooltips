package fi.septicuss.tooltips.managers.condition;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Context implements Cloneable{

    private final Map<String, Object> context;

    public Context() {
        this.context = new HashMap<>();
    }

    public Context(Map<String, Object> context) {
        this.context = context;
    }

    public @Nullable Object get(String key) {
        return this.context.get(key);
    }

    public boolean has(String key) {
        return this.context.containsKey(key);
    }

    public void put(String key, Object context) {
        this.context.put(key, context);
    }

    public void put(Context otherContext) {
        this.context.putAll(otherContext.contextMap());
    }

    public void remove(String key) {
        this.context.remove(key);
    }

    public void merge(Context otherContext) {
        this.context.putAll(otherContext.contextMap());
    }

    public Map<String, Object> contextMap() {
        return this.context;
    }

    @Override
    public Context clone() {
        return new Context(this.context);
    }
}
