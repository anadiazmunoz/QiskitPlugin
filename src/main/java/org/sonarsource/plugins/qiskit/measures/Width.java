package org.sonarsource.plugins.qiskit.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import java.io.IOException;

import static org.sonarsource.plugins.qiskit.measures.QiskitMetrics.WIDTH;

public class Width implements Sensor {

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Calculate the width property of the circuit");
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
                    .forMetric(WIDTH)
                    .on(file)
                    .withValue(calculate(file, context))
                    .save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int calculate(InputFile f, SensorContext context) throws IOException {
        int totalqubits = 0, intqubits = 0;
        String[] parts, parts2;
        String qubits, part1, build_qubits;
                if (QuantumCircuitDetector.isQuantumSource(f)) {
            try {
                String content = f.contents();
                String[] lines = content.split("\\R");
                for (String line : lines) {
                    if (line.contains("QuantumRegister(") && !line.contains("QuantumCircuit(")) {
                        build_qubits = line.replace(" ", "");
                        parts = build_qubits.split("\\(");
                        part1 = parts[1];
                        if(part1.contains(",")) {
                            parts = part1.split("\\,");
                            qubits = parts[0];
                        }else{
                            parts = part1.split("\\)");
                            qubits = parts[0];
                        }
                        intqubits = Integer.valueOf(qubits);
                        totalqubits = totalqubits + intqubits;
                    }
                    else if (line.contains("QuantumRegister(") && line.contains("QuantumCircuit(")) {
                        build_qubits = line.replace(" ", "");
                        parts = build_qubits.split("\\,");
                        for(int i=0; i<parts.length; i++){
                            if(parts[i].contains("QuantumRegister")){
                                parts2 = parts[i].split("\\(");
                                if(parts2.length == 3){
                                    part1 = parts2[2];
                                } else {
                                    part1 = parts2[1];
                                }
                                if(part1.contains(")")){
                                    parts2 = part1.split("\\)");
                                    qubits = parts2[0];
                                } else {
                                    qubits = part1;
                                }
                                intqubits = Integer.valueOf(qubits);
                                totalqubits = totalqubits + intqubits;
                            }
                        }
                    }
                    else if (line.contains("QuantumCircuit(")
                            && !line.contains("QuantumRegister(")
                            && totalqubits == 0) {
                        java.util.regex.Matcher matcher =
                            java.util.regex.Pattern.compile(
                                "QuantumCircuit\\s*\\(\\s*(\\d+)\\s*(?:,|\\))"
                            ).matcher(line);

                        if (matcher.find()) {
                            totalqubits = Integer.parseInt(matcher.group(1));
                        }
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return totalqubits;
    }
}
