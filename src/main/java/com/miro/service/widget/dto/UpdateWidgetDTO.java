package com.miro.service.widget.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UpdateWidgetDTO {
    private Integer x;
    private Integer y;
    private Integer zIndex;
    @Positive
    private Integer width;
    @Positive
    private Integer height;
}
