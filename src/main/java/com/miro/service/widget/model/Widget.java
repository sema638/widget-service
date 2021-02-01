package com.miro.service.widget.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Entity
@Table
public class Widget {
    @Id
    private Long id;
    @Column
    private Integer x;
    @Column
    private Integer y;
    @Column
    private Integer zIndex;
    @Column
    private Integer width;
    @Column
    private Integer height;
    @Column
    private LocalDateTime lastModified;
}
