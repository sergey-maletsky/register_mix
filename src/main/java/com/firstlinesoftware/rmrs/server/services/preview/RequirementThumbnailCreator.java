package com.firstlinesoftware.rmrs.server.services.preview;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.server.services.impl.preview.PdfPreviewFactory;
import com.firstlinesoftware.base.server.services.impl.preview.ThumbnailCreator;
import com.firstlinesoftware.base.shared.dto.Pair;
import com.firstlinesoftware.pdfcropper.cropper.PdfCropper;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.PDimension;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class RequirementThumbnailCreator extends ThumbnailCreator {
    @Autowired
    private PdfPreviewFactory previewFactory;

    @PostConstruct
    private void init() {
        previewFactory.register("IcePdfCropped", this);
    }

    @Override
    public Pair<RenderedImage, Integer> createThumbnail(RepositoryService.Content content, int page) throws IOException {
        final Document doc = new Document();
        try {
            final RenderedImage image = renderPages(doc, content.stream);
            return image != null ? new Pair<>(image, doc.getNumberOfPages()) : null;
        } catch (PDFSecurityException e) {
            throw new HTTPException(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (PDFException e) {
            throw new HTTPException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            doc.dispose();
        }
    }

    private static RenderedImage renderPages(Document doc, InputStream stream) throws IOException, PDFException, PDFSecurityException {
        byte[] inputBytes = IOUtils.toByteArray(stream);
        doc.setInputStream(new ByteArrayInputStream(inputBytes), null);
        List<PDRectangle> rectangles = PdfCropper.getCroppedRectangles(new ByteArrayInputStream(inputBytes));
        int height;
        int width;
        int xOffset;
        int yOffset;

        if (doc.getNumberOfPages() == 1) {
            PDRectangle pdRectangle = rectangles.get(rectangles.size() - 1);
            height = (int) pdRectangle.getHeight();
            width = (int) pdRectangle.getWidth();
            xOffset = (int) pdRectangle.getLowerLeftX();
            yOffset = (int) (doc.getPageDimension(0, 0.0f).getHeight() - pdRectangle.getUpperRightY());
            return renderPage(doc, 0, xOffset, yOffset, width, height);
        } else {
            width = 0;
            for (PDRectangle pdRectangle : rectangles) {
                if ((int) pdRectangle.getWidth() > width) {
                    width = (int) pdRectangle.getWidth();
                }
            }

            final List<RenderedImage> pages = new ArrayList<>();
            for (int i = 0; i < rectangles.size(); i++) {

                PDRectangle pdRectangle = rectangles.get(i);
                height = (int) pdRectangle.getHeight();
                xOffset = (int) pdRectangle.getLowerLeftX();
                yOffset = (int) (doc.getPageDimension(i, 0.0f).getHeight() - pdRectangle.getUpperRightY());
                pages.add(renderPage(doc, i, xOffset, yOffset, width, height));
            }

            return renderPages(pages);
        }
    }

    private static RenderedImage renderPage(Document doc, int i, int xOffset, int yOffset, int width, int height) {
        Page page = doc.getPageTree().getPage(i);
        page.init();
        PDimension sz = page.getSize(Page.BOUNDARY_MEDIABOX, 0f, 2.0f);
        int pageWidth;
        int pageHeight;
        if (height > 0 && height < page.getMediaBox().getHeight()) {
            pageHeight = height * 2;
            yOffset *= 2;
        } else {
            pageHeight = (int) sz.getHeight();
        }

        if (width > 0 && width < page.getMediaBox().getWidth()) {
            pageWidth = width * 2;
            xOffset *= 2;
        } else {
            pageWidth = (int) sz.getWidth();
        }

        final BufferedImage image = new BufferedImage(pageWidth + xOffset, pageHeight + yOffset, BufferedImage.TYPE_INT_RGB);
        final Graphics g = image.createGraphics();
        page.paint(g, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_MEDIABOX, 0f, 2.0f);
        g.dispose();

        return renderPage(image, xOffset, yOffset, i);
    }

    private static BufferedImage renderPages(List<RenderedImage> pages) {
        int height = 0;
        int width = 0;
        for (RenderedImage page : pages) {
            height += page.getHeight();
            width = Math.max(width, page.getWidth());
        }

        final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = result.createGraphics();
        height = 0;
        for (RenderedImage page : pages) {
            g2.setPaint(Color.WHITE);
            g2.fillRect(0, height, page.getWidth(), page.getHeight());
            g2.drawImage(((Image) page), 0, height, null);
            height += page.getHeight();
        }
        g2.dispose();

        return result;
    }

    private static BufferedImage renderPage(RenderedImage image, int xOffset, int yOffset, int i) {
        int imageW = image.getWidth() - xOffset;
        int imageH = image.getHeight() - yOffset;
        int marginX = 10;
        int marginY = 0;
        if (i == 0) {
            marginY = 5;
        }
        final BufferedImage result = new BufferedImage(imageW + marginX * 2, imageH + marginY, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = result.createGraphics();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, imageW + marginX*2, imageH + marginY);
        g2.drawImage(((Image) image), -xOffset + marginX, -yOffset + marginY, null);
        g2.dispose();

        return result;
    }
}