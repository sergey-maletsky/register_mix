package com.firstlinesoftware.rmrs.server.importers;

import com.firstlinesoftware.base.server.importers.AbstractImporter;
import com.firstlinesoftware.base.server.services.AuthService;
import com.firstlinesoftware.rmrs.shared.dto.Requirements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public abstract class AbstractRequirementsImporter extends AbstractImporter<Requirements, Requirements, File> {
}
