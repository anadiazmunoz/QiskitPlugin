package org.sonarsource.plugins.qiskit.measures;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;
import org.sonar.api.batch.fs.InputFile;

/** Static, non-executing detection of likely Qiskit/Quirk files. Metric calculations are unchanged. */
public final class QuantumCircuitDetector {
    private static final Pattern QISKIT_IMPORT = Pattern.compile(
        "(?m)^\\s*(?:from\\s+qiskit(?:\\.[\\w.]+)?\\s+import\\s+|import\\s+qiskit(?:\\.[\\w.]+)?\\b)"
    );
    private static final Pattern QISKIT_CONSTRUCTION = Pattern.compile(
        "\\b(?:QuantumCircuit|QuantumRegister|ClassicalRegister|AncillaRegister)\\s*\\("
    );
    private static final Pattern QUIRK_REFERENCE = Pattern.compile(
        "(?i)(?:\\bquirk\\b|[\\\"']cols[\\\"']\\s*:\\s*\\[)"
    );

    private QuantumCircuitDetector() { }

    public static boolean isQuantumSource(InputFile file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        String name = file.filename().toLowerCase(Locale.ROOT);
        // Keep the legacy filename rule to avoid silently changing historical datasets.
        if (name.contains("qiskit") || name.contains("quirk")) {
            return true;
        }
        if (!name.endsWith(".py")) {
            return false;
        }
        try {
            String content = file.contents();
            return QISKIT_IMPORT.matcher(content).find()
                || QISKIT_CONSTRUCTION.matcher(content).find()
                || QUIRK_REFERENCE.matcher(content).find();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot inspect quantum source: " + file.filename(), e);
        }
    }
}
