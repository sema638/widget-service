package com.miro.service.widget.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class WidgetDTO {
    private final Long id;
    private final Integer x;
    private final Integer y;
    private final Integer zIndex;
    private final Integer width;
    private final Integer height;
    private final LocalDateTime lastModified;
}
