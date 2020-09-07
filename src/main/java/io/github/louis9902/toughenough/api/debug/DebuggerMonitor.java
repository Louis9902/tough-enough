package io.github.louis9902.toughenough.api.debug;

public interface DebuggerMonitor {

    void append(Section section);

    class Section {
        public final String identifier;
        public final String name;
        public final String value;

        public Section(String identifier, String name, String value) {
            this.identifier = identifier;
            this.name = name;
            this.value = value;
        }
    }

}
