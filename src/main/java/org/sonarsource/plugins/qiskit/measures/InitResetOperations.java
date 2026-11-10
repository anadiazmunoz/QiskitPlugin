package org.sonarsource.plugins.qiskit.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import java.io.IOException;

import static org.sonarsource.plugins.qiskit.measures.QiskitMetrics.INITRESET;

public class InitResetOperations implements Sensor {

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Calculate the initialize and reset operations property of the circuit");
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
                    .forMetric(INITRESET)
                    .on(file)
                    .withValue(calculate(file, context))
                    .save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int calculate(InputFile f, SensorContext context) throws IOException {
        int totalInitReset = 0;
                if (QuantumCircuitDetector.isQuantumSource(f)) {
        	try {
            		String content = f.contents();
            		String[] lines = content.split("\\R");
            		for (String line : lines) {
            			if (line.contains(".initialize") || line.contains(".reset")) {
                			totalInitReset++;
            			}
            		}
        	} catch (IOException e) {
            		throw new IllegalStateException(e);
        	}
        }
        return totalInitReset;
    }
}
