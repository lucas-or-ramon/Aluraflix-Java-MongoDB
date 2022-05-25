package br.com.devcanoa.aluraflix.controller.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryRequest {

    @NotBlank
    @Size(max = 20)
    private final String title;

    @NotBlank
    @Size(max = 20)
    private final String color;

}
