package org.sonarsource.plugins.qiskit.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import java.io.IOException;

import static org.sonarsource.plugins.qiskit.measures.QiskitMetrics.LCG;


public class LowComplexityGates implements Sensor {

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("Calculate the gates complexity property of the circuit");
  }

  @Override
  public void execute(SensorContext context) {
    FileSystem fs = context.fileSystem();
    Iterable<InputFile> files = fs.inputFiles(fs.predicates().hasType(InputFile.Type.MAIN));
    for (InputFile file : files) {
      try {
        context.<Integer>newMeasure()
            .forMetric(LCG)
            .on(file)
            .withValue(calculate(file, context))
            .save();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public int calculate(InputFile f, SensorContext context) throws IOException {
    int totalLCG = 0;
        if (QuantumCircuitDetector.isQuantumSource(f)) {

      try {
        String content = f.contents();
        String[] lines = content.split("\\R");
        for (String line : lines) {
          if (line.contains(".h(")) totalLCG++;
          if (line.contains(".i(")) totalLCG++;
          if (line.contains(".p(")) totalLCG++;
          if (line.contains(".r(")) totalLCG++;
          if (line.contains(".rx(")) totalLCG++;
          if (line.contains(".ry(")) totalLCG++;
          if (line.contains(".rz(")) totalLCG++;
          if (line.contains(".s(")) totalLCG++;
          if (line.contains(".sdg(")) totalLCG++;
          if (line.contains(".sx(")) totalLCG++;
          if (line.contains(".sxdg(")) totalLCG++;
          if (line.contains(".t(")) totalLCG++;
          if (line.contains(".tdg(")) totalLCG++;
          if (line.contains(".u(")) totalLCG++;
          if (line.contains(".x(")) totalLCG++;
          if (line.contains(".y(")) totalLCG++;
          if (line.contains(".z(")) totalLCG++;
        }
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return totalLCG;
  }
}