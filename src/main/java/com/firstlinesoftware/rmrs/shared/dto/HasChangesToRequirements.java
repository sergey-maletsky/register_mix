package com.firstlinesoftware.rmrs.shared.dto;

import java.util.List;

public interface HasChangesToRequirements {
    List<Requirement> getCreateNew();

    void setCreateNew(List<Requirement> createNew);

    List<Requirement> getModifyExisting();

    void setModifyExisting(List<Requirement> modifyExisting);
}
