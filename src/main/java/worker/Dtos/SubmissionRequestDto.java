package worker.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmissionRequestDto {

    @JsonProperty(value = "language")
    private String Language;

    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value="testCases")
    private List<TestCasesDto> testCasesDtoList;
}
