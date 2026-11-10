package org.sonarsource.plugins.qiskit.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import java.io.IOException;

import static org.sonarsource.plugins.qiskit.measures.QiskitMetrics.IFINST;

public class ConditionalInstructions implements Sensor {

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Calculate the conditional instructions property of the circuit");
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fs = context.fileSystem();
        Iterable<InputFile> files = fs.inputFiles(fs.predicates().hasType(InputFile.Type.MAIN));
        for (InputFile file : files) {
        	try {
            		context.<Integer>newMeasure()
                	.forMetric(IFINST)
                	.on(file)
                	.withValue(calculate(file, context))
                	.save();
        	} catch (IOException e) {
            		e.printStackTrace();
        	}
        }
    }

    public int calculate(InputFile f, SensorContext context) throws IOException {
        int totalIF = 0;
            	if (QuantumCircuitDetector.isQuantumSource(f)) {
        	try {
            		String content = f.contents();
            		String[] lines = content.split("\\R");
            		for (String line : lines) {
            			if (line.contains(".c_if(")) {
                			totalIF++;
            			}
            		}
        	} catch (IOException e) {
            		throw new IllegalStateException(e);
        	}
        }
        return totalIF;
    }
}
