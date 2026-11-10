package org.sonarsource.plugins.qiskit.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import java.io.IOException;

import static org.sonarsource.plugins.qiskit.measures.QiskitMetrics.AUXQUBITS;

public class AuxiliaryQubits implements Sensor {

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Calculate the auxiliary qubits property of the circuit");
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fs = context.fileSystem();
        // only "main" files, but not "tests"
        Iterable<InputFile> files = fs.inputFiles(fs.predicates().hasType(InputFile.Type.MAIN));
        for (InputFile file : files) {
            if (!QuantumCircuitDetector.isQuantumSource(file)) {
                continue;
            }
            try {
                context.<Integer>newMeasure()
                    .forMetric(AUXQUBITS)
                    .on(file)
                    .withValue(calculate(file, context))
                    .save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int calculate(InputFile f, SensorContext context) throws IOException {
        int totalAUX = 0, intcubits = 0;
        String[] parts, parts2;
        String cubits, part1, build_cubits;
                if (QuantumCircuitDetector.isQuantumSource(f)) {
            try {
                String content = f.contents();
                String[] lines = content.split("\\R");
                for (String line : lines) {
                    if (line.contains("QuantumRegister(") && (line.contains("ancilla") || line.contains("Ancilla") || line.contains("anc") || line.contains("Anc"))) {
                        build_cubits = line.replace(" ", "");
                        parts = build_cubits.split("\\(");
                        part1 = parts[1];
                        if(part1.contains(",")) {
                            parts = part1.split("\\,");
                            cubits = parts[0];
                        }else{
                            parts = part1.split("\\)");
                            cubits = parts[0];
                        }
                        intcubits = Integer.valueOf(cubits);
                        totalAUX = totalAUX + intcubits;
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return totalAUX;
    }
}
