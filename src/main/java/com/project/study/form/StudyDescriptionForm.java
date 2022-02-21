package com.project.study.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class StudyDescriptionForm {

    @NotEmpty
    @Length(max = 100)
    private String shortDescription;

    @NotEmpty
    private String fullDescription;

}
