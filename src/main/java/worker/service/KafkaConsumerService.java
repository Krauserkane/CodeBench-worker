package worker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import worker.Dtos.SubmissionRequestDto;
import worker.Dtos.TestCasesDto;
import worker.Entity.Submission;
import worker.Entity.SubmissionId;
import worker.Repository.SubmissionRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.Map;

@Component
public class KafkaConsumerService {

    private final Map<String, ExecutionerService> executionerServices;


    @Autowired
    SubmissionRepository submissionRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public KafkaConsumerService(Map<String, ExecutionerService> executionerServices) {
        this.executionerServices = executionerServices;
    }

    @KafkaListener(topics = "thing1", groupId = "new1-group")
    public void handleKafkaEvent(@Payload String rawMessage, @Header(name="submissionId")String submissionId) throws Exception {
        SubmissionRequestDto message = objectMapper.readValue(rawMessage, SubmissionRequestDto.class);
        System.out.println(message.getTestCasesDtoList().toString());


        //Create an isolate box here
        ProcessBuilder initBuilder=new ProcessBuilder("isolate","--init");
        Process initProcess=initBuilder.start();
        String boxId=null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(initProcess.getInputStream()))) {
             boxId = reader.readLine().trim();
        }
        boxId=boxId.replace("/var/local/lib/isolate/","");
        System.out.println(boxId);

        String workDirectory="/var/local/lib/isolate/"+boxId+"/box/";
        System.out.println(workDirectory);


        //TODO get the appropriate executioner service
        String language=message.getLanguage();
        String beanName=language+"ExecutionerService";
        ExecutionerService executionerService=executionerServices.get(beanName);

        for(TestCasesDto testCasesDto:message.getTestCasesDtoList()){
            String input=testCasesDto.getInput();
            String output=testCasesDto.getOutput();
            Integer testCaseId= Integer.valueOf(testCasesDto.getTestCaseId());

            //Create the code files in that directory -/var/local/lib/isolate/0/box
            executionerService.createCodeFiles(workDirectory,submissionId,message.getCode(),input);

            //build the code file
            executionerService.build(workDirectory,submissionId);

            //run the code file
            Process run=executionerService.run(workDirectory,submissionId);
            run.waitFor();


            String stdout = readStream(run.getInputStream());
            String stderr = readStream(run.getErrorStream());

            System.out.println(stdout);
            System.out.println(stderr);


            // I have a meta file which gets generated and it will contain these things- What all things would I need now from this?
            //        time:0.048
            //        time-wall:0.047
            //        max-rss:39392
            //        csw-voluntary:101
            //        csw-forced:11
            //        exitcode:0

            Double timeWall = null;
            Integer maxRss = null;

            for (String line : Files.readAllLines(Paths.get(workDirectory+"/meta.txt"))) {
                line = line.trim();
                if (line.startsWith("time-wall:")) {
                    timeWall = Double.parseDouble(line.substring("time-wall:".length()).trim());
                } else if (line.startsWith("max-rss:")) {
                    maxRss = Integer.parseInt(line.substring("max-rss:".length()).trim());
                }
            }

            System.out.println("Time taken by the program"+timeWall);
            System.out.println("Memory taken by the program"+maxRss);
            System.out.println("The stdout is"+stdout);
            System.out.println("The stderr is"+stderr);
            SubmissionId submissionid=new SubmissionId(submissionId,testCaseId);
            Submission submission= Submission.builder().submissionId(submissionid).input(input).output(output).stdout(stdout).stderr(stderr).timeTaken(timeWall).memoryTaken(maxRss).build();
            submissionRepository.save(submission);
        }

    }

    private String readStream(java.io.InputStream stream) throws Exception {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }
}
