package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import com.ellirion.buildframework.model.Point;

public class TemplateSession {

    @Getter @Setter private Template template;
    @Getter @Setter private Point point;

    /**
     * Constructor of TemplateSession.
     * @param template Template of current session.
     * @param point Point of the current template.
     */
    public TemplateSession(final Template template, final Point point) {
        this.template = template;
        this.point = point;
    }
}
