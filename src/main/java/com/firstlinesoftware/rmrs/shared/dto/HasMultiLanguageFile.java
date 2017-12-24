package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.base.shared.dto.AttachedFile;

public interface HasMultiLanguageFile {

    AttachedFile getRussian();

    AttachedFile getEnglish();

    void setEnglish(AttachedFile english);

    void setRussian(AttachedFile russian);
}
