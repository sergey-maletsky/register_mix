package com.firstlinesoftware.rmrs.server.services.preview;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.Pair;
import com.firstlinesoftware.base.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

/**
 * Created by YSpiridonov on 8/7/2017.
 */
public class IcePdfThumbnailCreatorTest extends BaseTest {
    private static final String FILENAME = "4.4.9.1_en.pdf";
    private static final String SOURCE = "preview/";
    private static final int lastPage = 100500;

    @Autowired
    private RequirementThumbnailCreator requirementThumbnailCreator;

    @Test
    public void createThumbnailTest() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final File sourceFile = new File(classLoader.getResource(SOURCE + FILENAME).getFile());
        InputStream targetStream = new FileInputStream(sourceFile);
        RepositoryService.Content content = new RepositoryService.Content();
        content.stream = targetStream;

        Pair<RenderedImage, Integer> imageIntegerPair = requirementThumbnailCreator.createThumbnail(content, lastPage);
        assertNotNull(imageIntegerPair);
    }
}

