package com.miro.service.widget.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "widget")
public class Widget {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private Integer x;
    @Column
    private Integer y;
    @Column
    private Integer zindex;
    @Column
    private Integer width;
    @Column
    private Integer height;
    @Column
    private LocalDateTime lastModified;
}
