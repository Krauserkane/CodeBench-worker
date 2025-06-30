package worker.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component("JavaExecutionerService")
public class JavaExecutionerService implements ExecutionerService{
    @Override
    public void build(String workDirectory, String submissionId) throws IOException, InterruptedException {
        String codeLocation=workDirectory+"MyClass.java";
        ProcessBuilder compileBuilder=new ProcessBuilder("javac",codeLocation);
        Process compile=compileBuilder.start();
        compile.waitFor();
        System.out.println("Code compilation done?");
    }

    @Override
    public Process run(String location, String submissionId) throws Exception {
        ProcessBuilder runBuilder = new ProcessBuilder("isolate","--processes=20","--meta=meta.txt","--run","--dir=/opt/java/openjdk:rw","--run","/opt/java/openjdk/bin/java","MyClass","--stdin="+submissionId+"-input.txt");
        runBuilder.directory(new File(location));

        Process run=runBuilder.start();
        run.waitFor();
        return run;
    }

    @Override
    public void createCodeFiles(String location, String submissionId, String code, String input) throws IOException {
        FileWriter codeWriter= new FileWriter(new File(location+"MyClass.java"));
        codeWriter.write(code);
        codeWriter.close();

        FileWriter inputWriter= new FileWriter(new File(location+submissionId+"-input.txt"));
        inputWriter.write(input);
        inputWriter.close();

    }
}
