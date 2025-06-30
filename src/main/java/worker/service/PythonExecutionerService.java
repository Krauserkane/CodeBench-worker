package worker.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component("PythonExecutionerService")
public class PythonExecutionerService implements ExecutionerService{
    @Override
    public void build(String code, String submissionId) throws IOException, InterruptedException {

    }

    @Override
    public Process run(String location, String submissionId) throws Exception {
        ProcessBuilder runBuilder = new ProcessBuilder("isolate","--meta=meta.txt","-m 20240","-t 3","--run","/usr/bin/python3",submissionId+".py","--stdin="+submissionId+"-input.txt");
        runBuilder.directory(new File(location));
        Process run=runBuilder.start();
        run.waitFor();
        return run;
    }

    @Override
    public void createCodeFiles(String location, String submissionId, String code, String input) throws IOException {
        FileWriter codeWriter= new FileWriter(new File(location+submissionId+".py"));
        codeWriter.write(code);
        codeWriter.close();

        FileWriter inputWriter= new FileWriter(new File(location+submissionId+"-input.txt"));
        inputWriter.write(input);
        inputWriter.close();
    }
}
