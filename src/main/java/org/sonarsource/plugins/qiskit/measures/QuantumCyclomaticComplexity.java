package org.sonarsource.plugins.qiskit.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import static org.sonarsource.plugins.qiskit.measures.QiskitMetrics.QCC;


public class QuantumCyclomaticComplexity implements Sensor {

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("Calculate the quantum cyclomatic complexity property of the circuit");
  }

  @Override
  public void execute(SensorContext context) {
    FileSystem fs = context.fileSystem();
    Iterable<InputFile> files = fs.inputFiles(fs.predicates().hasType(InputFile.Type.MAIN));
    for (InputFile file : files) {
      try {
        context.<Integer>newMeasure()
            .forMetric(QCC)
            .on(file)
            .withValue(calculate(file, context))
            .save();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public int calculate(InputFile f, SensorContext context) throws IOException {
    int totalQCC = 0;
        if (QuantumCircuitDetector.isQuantumSource(f)) {

      try {
        String content = f.contents();
        String[] lines_convacios = content.split("\\R");
        ArrayList<String> lines = new ArrayList();
        for(int i=0; i<lines_convacios.length; i++){
          String line = lines_convacios[i].split("#", 2)[0].trim();
          if (!line.isEmpty()) {
            lines.add(line);
          }
        }
        int[] controls = new int [lines.size()];
        for (int i=0; i<lines.size(); i++) {
          
          String line = lines.get(i);

          if ((line.contains(".ch(") || line.contains("cs") || line.contains("csdg") || line.contains("csx")
          || line.contains("cx") || line.contains("cy") || line.contains("cz") || line.contains("dcx")
          || line.contains("ecr") || line.contains("swap") || line.contains("iswap") || line.contains(".cp(")
          || line.contains(".crx(") || line.contains(".cry(") || line.contains(".crz(") || line.contains(".cu1(")
          || line.contains(".rxx(") || line.contains(".ryy(") || line.contains(".rzz(") || line.contains(".rzx(")
          || line.contains(".cu3(") || line.contains(".cu(") || line.contains(".xx-yy(") || line.contains(".xx+yy(")
          || line.contains(".cswap(") || line.contains(".fredkin("))
          && !line.contains(".c_if(")){
            controls[i] = 3;
          }

          if ((line.contains(".rccx(") || line.contains(".ccx(") || line.contains(".toffoli(")) && !line.contains(".c_if(")){
            controls[i] = 5;
          }

          if ((line.contains(".c3x(") || line.contains(".c3sx(") || line.contains(".rcccx("))
          && !line.contains(".c_if(")) {
            controls[i] = 7;
          }

          if ((line.contains(".c4x(")) && !line.contains(".c_if(")) {

            controls[i] = 9;
          }
        }

        for(int i=0; i<controls.length; i++){
          if(controls[i] != 0){
            totalQCC += controls[i];
          }
          if(i>0){
            if(controls[i] != 0 && controls[i-1] != 0){
              totalQCC--;
            }
          }
        }

      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return totalQCC;
  }
}