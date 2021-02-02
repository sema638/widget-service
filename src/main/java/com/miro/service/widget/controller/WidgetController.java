package com.miro.service.widget.controller;

import com.miro.service.widget.dto.CreateWidgetDTO;
import com.miro.service.widget.dto.UpdateWidgetDTO;
import com.miro.service.widget.dto.WidgetDTO;
import com.miro.service.widget.model.Paging;
import com.miro.service.widget.model.Widget;
import com.miro.service.widget.service.WidgetService;
import com.miro.service.widget.util.WidgetUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api("widget")
@RestController
@RequestMapping(path = {"/api/v1/widgets"}, produces = APPLICATION_JSON_VALUE)
public class WidgetController {
    @Autowired
    private WidgetService widgetService;

    @Operation(summary = "Returns a list of all widget")
    @ApiResponse(responseCode = "200", description = "List of widgets returned",
                 content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = WidgetDTO.class))})
    @GetMapping
    public List<WidgetDTO> all(
            @RequestParam(value = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        final Paging paging = new Paging(page, size);
        return widgetService.findAll(paging).stream()
                .map(WidgetUtil::convert)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get a widget by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the widget",
                 content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = WidgetDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Widget not found", content = @Content)})
    @GetMapping("/{id}")
    public WidgetDTO one(@PathVariable final long id) {
        return WidgetUtil.convert(widgetService.findById(id));
    }

    @Operation(summary = "Create a new widget")
    @ApiResponse(responseCode = "201", description = "Widget is created",
                 content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = WidgetDTO.class))})
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<WidgetDTO> create(@Valid @RequestBody final CreateWidgetDTO widgetDTO) {
        final Widget widget = new Widget(null, widgetDTO.getX(), widgetDTO.getY(), widgetDTO.getZIndex(),
                widgetDTO.getWidth(), widgetDTO.getHeight(), null);
        return new ResponseEntity<>(WidgetUtil.convert(widgetService.create(widget)), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a widget by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Widget is updated",
                         content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = WidgetDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Widget not found", content = @Content)})
    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public WidgetDTO update(@PathVariable final long id, @Valid @RequestBody final UpdateWidgetDTO widgetDTO) {
        final Widget widget = new Widget(id, widgetDTO.getX(), widgetDTO.getY(), widgetDTO.getZIndex(),
                widgetDTO.getWidth(), widgetDTO.getHeight(), null);
        return WidgetUtil.convert(widgetService.update(widget));
    }

    @Operation(summary = "Delete a widget by its id")
    @ApiResponse(responseCode = "204", description = "Widget is deleted", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final long id) {
        widgetService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
