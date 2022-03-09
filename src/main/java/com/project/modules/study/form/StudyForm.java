package com.project.modules.study.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class StudyForm {

    @NotEmpty
    @Length(min = 2, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")
    private String path;

    @NotEmpty
    @Length(max = 50)
    private String title;

    @NotEmpty
    @Length(max = 100)
    private String shortDescription;

    @NotEmpty
    private String fullDescription;

}
