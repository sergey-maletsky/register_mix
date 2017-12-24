package com.firstlinesoftware.rmrs.server.importers;

import com.firstlinesoftware.base.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RequirementsXmlImportTest extends BaseTest {
    @Autowired
    private RequirementsXmlImporter requirementsXmlImporter;

    @Test
    public void testImport() {
        requirementsXmlImporter.doImport();
    }
}
