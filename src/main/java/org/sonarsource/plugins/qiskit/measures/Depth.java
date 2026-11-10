package org.sonarsource.plugins.qiskit.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import org.sonarsource.plugins.qiskit.measures.Util_Depth.q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.a_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.a_b_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.a_b_c_d_q_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.a_b_c_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.a_b_c_q_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.a_q_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.a_b_q_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.q_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.q_q_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.q_q_q_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.q_q_q_q_q;
import main.java.org.sonarsource.plugins.qiskit.measures.Util_Depth.measure;

import static org.sonarsource.plugins.qiskit.measures.QiskitMetrics.DEPTH;

import java.io.IOException;

public class Depth implements Sensor {
    
    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Calculate the depth property of the circuit");
    }

  @Override
    public void execute(SensorContext context) {
        
        FileSystem fs = context.fileSystem();
        Iterable<InputFile> files = fs.inputFiles(fs.predicates().hasType(InputFile.Type.MAIN));
        for (InputFile file : files) {
            if (!QuantumCircuitDetector.isQuantumSource(file)) {
                continue;
            }
            try {
                context.<Integer>newMeasure()
                    .forMetric(DEPTH)
                    .on(file)
                    .withValue(calculate(file, context))
                    .save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int calculate(InputFile f, SensorContext context) throws IOException {
        int n = Width.calculate(f, context);
        int ancilla = AuxiliaryQubits.calculate(f, context);
        int[] depths = new int[n];
        depths = contar(depths, ancilla, f, context);
        int maxDepth = 0;
        for (int i=0; i<depths.length; i++) {
            if (depths[i] > maxDepth)
                maxDepth = depths[i];
        }
        return maxDepth;
    }

    public int[] contar(int[] depths, int ancilla, InputFile f, SensorContext context) {
        String linea, linea1;
        String[] parts;
                if (QuantumCircuitDetector.isQuantumSource(f)) {
            try {
                String content = f.contents();
                String[] lines = content.split("\\R");
                for (String line : lines) {
                    linea = line.replace(" ", "");

                    if (linea.contains(".h(") || linea.contains(".i(") || linea.contains(".id(") || linea.contains(".s(")
                    || linea.contains(".sdg(") || linea.contains(".sx(") || linea.contains(".sxdg(") || linea.contains(".t(")
                    || linea.contains(".tdg(") || linea.contains(".x(") || linea.contains(".y(") || linea.contains(".z(")) {
                        
                        if(!linea.contains(".c_if()"))
                            depths = q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = q.calculate(depths, linea1, lines);
                        }

                    }

                    else if(linea.contains(".p(") || linea.contains(".rx(") || linea.contains(".ry(") || linea.contains(".rz(")) {

                        if(!linea.contains(".c_if()"))
                            depths = a_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = a_q.calculate(depths, linea1, lines);
                        }

                    }

                    else if(linea.contains(".r(")) {
                        
                        if(!linea.contains(".c_if()"))
                            depths = a_b_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = a_b_q.calculate(depths, linea1, lines);
                        }

                    }

                    else if (linea.contains(".u(")) {
                        
                        if(!linea.contains(".c_if()"))
                            depths = a_b_c_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = a_b_c_q.calculate(depths, linea1, lines);
                        }
                    }

                    if (linea.contains(".ch(") || linea.contains(".cs(") || linea.contains(".csdg(")
                    || linea.contains(".csx(") || linea.contains(".cx(") || linea.contains(".cy(") ||
                    linea.contains(".cz(") || linea.contains(".dcx(") || linea.contains(".ecr(") ||
                    linea.contains(".swap(") || linea.contains(".iswap(")) {
                        
                        if(!linea.contains(".c_if()"))
                            depths = q_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = q_q.calculate(depths, linea1, lines);
                        }

                    }

                    if (linea.contains(".cp(") || linea.contains(".crx(") || linea.contains(".cry(")
                    || linea.contains(".crz(") || linea.contains(".cu1(") || linea.contains(".rxx(")
                    || linea.contains(".ryy(") || linea.contains(".rzz(") || linea.contains(".rzx(")) {
                        
                        if(!linea.contains(".c_if()"))
                            depths = a_q_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = a_q_q.calculate(depths, linea1, lines);
                        }

                    }

                    if (linea.contains(".cu(")) {
                        
                        if(!linea.contains(".c_if()"))
                            depths = a_b_c_d_q_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = a_b_c_d_q_q.calculate(depths, linea1, lines);
                        }

                    }

                    if (linea.contains(".cu3(")) {
                        
                        if(!linea.contains(".c_if()"))
                            depths = a_b_c_q_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = a_b_c_q_q.calculate(depths, linea1, lines);
                        }
                        
                    }

                    if (linea.contains(".xx-yy(") || linea.contains(".xx+yy(")) {
                        
                        if(!linea.contains(".c_if()"))
                            depths = a_b_q_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = a_b_q_q.calculate(depths, linea1, lines);
                        }

                    }

                    if (linea.contains(".rccx(") || linea.contains(".cswap(") || linea.contains(".fredkin(")
                    || linea.contains(".ccx(") || linea.contains(".toffoli(")) {
                        
                        if(!linea.contains(".c_if()"))
                            depths = q_q_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = q_q_q.calculate(depths, linea1, lines);
                        }

                    }

                    if (linea.contains(".c3x(") || linea.contains(".c3sx(") || linea.contains(".rc3x(")) {
                       
                        if(!linea.contains(".c_if()"))
                            depths = q_q_q_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = q_q_q_q.calculate(depths, linea1, lines);
                        }

                    }

                    if (linea.contains(".c4x(")) {

                        if(!linea.contains(".c_if()"))
                            depths = q_q_q_q_q.calculate(depths, linea, lines);
                        else{
                            parts = linea.split("//.c_if");
                            linea1 = parts[0];
                            depths = q_q_q_q_q.calculate(depths, linea1, lines);
                        }

                    }
                    
                    if (linea.contains(".measure(")) {

                        depths = measure.calculate(depths, linea, lines);

                    }

                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
              }
        }
        return depths;
    }
    
}
  