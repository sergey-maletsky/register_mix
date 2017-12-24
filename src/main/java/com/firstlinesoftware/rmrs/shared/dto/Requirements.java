package com.firstlinesoftware.rmrs.shared.dto;

import java.util.ArrayList;
import java.util.List;

public class Requirements {
    private List<Requirement> requirements;

    public Requirements() {
        requirements = new ArrayList<>();
    }

    public Requirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }
}
