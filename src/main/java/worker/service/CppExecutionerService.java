package worker.service;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import ch.qos.logback.core.joran.sanity.Pair;

import org.springframework.stereotype.Component;

import java.io.*;

@Component("CppExecutionerService")
public class CppExecutionerService implements ExecutionerService{
    @Override
    public void build(String workDirectory, String submissionId) throws IOException, InterruptedException {
        String codeLocation=workDirectory+submissionId+".cpp";
        ProcessBuilder compileBuilder=new ProcessBuilder("g++",codeLocation,"-o",workDirectory+"/main.out");
        Process compile=compileBuilder.start();
        compile.waitFor();
        System.out.println("Code compilation done?");
    }

    @Override
    public Process run(String location,String submissionId) throws Exception {
        ProcessBuilder runBuilder = new ProcessBuilder("isolate","--meta=meta.txt","-m 10240","-t 1","--run","./main.out","<","input.txt");
        runBuilder.directory(new File(location));
        Process run=runBuilder.start();
        return run;
    }

    @Override
    public void createCodeFiles(String location,String submissionId,String code,String input) throws IOException {
        FileWriter codeWriter= new FileWriter(new File(location+submissionId+".cpp"));
        codeWriter.write(code);
        codeWriter.close();

        FileWriter inputWriter= new FileWriter(new File(location+submissionId+"-input.txt"));
        inputWriter.write(input);
        inputWriter.close();

    }

}
