package net.praqma.logging;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.Arrays;
import java.util.List;
import javaposse.jobdsl.dsl.Context;

class LoggingTargetsJobDslContext implements Context {

    String name;
    public void name(String value) {
        name = value;
    }

    String level;
    final List<String> levels = Arrays.asList(
            "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST"
    );

    public void level(String value) {
        checkArgument(levels.contains(value));
        level = value;
    }
}
