package org.sonarsource.plugins.qiskit.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import java.io.IOException;

import static org.sonarsource.plugins.qiskit.measures.QiskitMetrics.MCG;


public class MediumComplexityGates implements Sensor {

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
            .forMetric(MCG)
            .on(file)
            .withValue(calculate(file, context))
            .save();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public int calculate(InputFile f, SensorContext context) throws IOException {
    int totalMCG = 0;
        if (QuantumCircuitDetector.isQuantumSource(f)) {

      try {
        String content = f.contents();
        String[] lines = content.split("\\R");
        for (String line : lines) {
          if (line.contains(".dcx(")) totalMCG++;
          if (line.contains(".ch(")) totalMCG++;
          if (line.contains(".cp(")) totalMCG++;
          if (line.contains(".crx(")) totalMCG++;
          if (line.contains(".cry(")) totalMCG++;
          if (line.contains(".crz(")) totalMCG++;
          if (line.contains(".csx(")) totalMCG++;
          if (line.contains(".cu(")) totalMCG++;
          if (line.contains(".cu1(")) totalMCG++;
          if (line.contains(".cu3(")) totalMCG++;
          if (line.contains(".cx(")) totalMCG++;
          if (line.contains(".cy(")) totalMCG++;
          if (line.contains(".cz(")) totalMCG++;
          if (line.contains(".rccx(")) totalMCG++;
          if (line.contains(".rxx(")) totalMCG++;
          if (line.contains(".ryy(")) totalMCG++;
          if (line.contains(".rzz(")) totalMCG++;
          if (line.contains(".rzx(")) totalMCG++;
          if (line.contains(".xx+yy(")) totalMCG++;
          if (line.contains(".xx-yy(")) totalMCG++;
          if (line.contains(".ecr(")) totalMCG++;
          if (line.contains(".swap(")) totalMCG++;
          if (line.contains(".iswap(")) totalMCG++;
        }
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return totalMCG;
  }
}