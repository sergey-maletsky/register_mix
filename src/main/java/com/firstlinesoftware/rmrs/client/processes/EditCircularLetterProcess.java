package com.firstlinesoftware.rmrs.client.processes;

import com.firstlinesoftware.ecm.client.processes.CreateCompositeDocumentProcess;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;

public class EditCircularLetterProcess extends CreateCompositeDocumentProcess<CircularLetter> {
    @Override
    protected CircularLetter createDTO() {
        return new CircularLetter();
    }
}