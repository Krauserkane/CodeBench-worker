package worker.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCasesDto {

    @JsonProperty(value = "input")
    private String input;

    @JsonProperty(value = "output")
    private String output;

    @JsonProperty(value = "testCaseId")
    private String testCaseId;
}
