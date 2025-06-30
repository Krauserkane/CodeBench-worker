package worker.service;


import ch.qos.logback.core.joran.sanity.Pair;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;


@Component
public interface ExecutionerService {

     public void build(String code,String submissionId) throws IOException, InterruptedException;

     public Process run(String location,String submissionId) throws Exception;

     public void createCodeFiles(String location,String submissionId,String code,String input) throws IOException;

}
