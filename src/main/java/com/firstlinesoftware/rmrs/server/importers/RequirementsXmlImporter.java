package com.firstlinesoftware.rmrs.server.importers;

import com.firstlinesoftware.base.client.utils.StringUtils;
import com.firstlinesoftware.base.server.adapters.ImportAdapter;
import com.firstlinesoftware.base.server.importers.utils.FileTraverser;
import com.firstlinesoftware.base.server.importers.utils.FolderScanner;
import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.AttachedFile;
import com.firstlinesoftware.ecm.server.providers.impl.CompositeDocumentProvider;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.Requirements;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class RequirementsXmlImporter extends AbstractRequirementsImporter {
    public static final String PDF_MIME_TYPE = "application/pdf";

    @Autowired
    private RepositoryService repositoryService;

    @Resource(name = "requirementsXmlAdapter")
    private ImportAdapter<Requirements, File> adapter;

    @Autowired
    private RequirementsUpdateStrategy strategy;

    @Autowired
    private FolderScanner folderScanner;

    @Scheduled(fixedDelay = 60000)
    public void importFromFolder() {
        final File rootDir = new File(repositoryService.getTempDir(), "import");
        final File inDir = new File(rootDir, "in");
        final File finishedDir = new File(rootDir, "finished");
        final File failedDir = new File(rootDir, "failed");
        if(inDir.exists()) {
            folderScanner.scan("file://" + inDir.getAbsolutePath(), new FileTraverser<File, File>() {
                @Override
                public void process(File file, Callback<File> cb) {
                    cb.onSuccess(file, file);
                }
            }, new FileTraverser.Callback<File>() {
                @Override
                public void onSuccess(File obj, File f) {
                    try {
                        importZip(f);
                        FileUtils.deleteQuietly(new File(finishedDir, f.getName()));
                        FileUtils.moveFileToDirectory(f, finishedDir, true);
                    } catch (IOException e) {
                        handleError(f, e);
                    }
                }

                @Override
                public void onFailure(Exception e, File f) {
                    handleError(f, e);
                }

                private void handleError(File f, Exception e) {
                    Logger.getLogger(RequirementsXmlImporter.class).error("while importing " + f.getName(), e);
                    try {
                        FileUtils.moveFileToDirectory(f, failedDir, true);
                    } catch (IOException ignored) {
                    }
                }
            });
        }
    }

    public void doImport() {
        final URL resource = getClass().getResource("pdfrules/requirements.xml");
        if (resource != null) {
            final File file = new File(resource.getFile());
            CompositeDocumentProvider.storeLocalFiles.set(true);
            doImport(adapter, new RequirementsUpdateStrategy() {
                @Override
                public void importItem(Requirements requirements) {
                    for (final Requirement requirement : requirements.getRequirements()) {
                        requirement.russian = getAttachedFromResource(requirement.getRussian());
                        requirement.english = getAttachedFromResource(requirement.getEnglish());
                    }
                    strategy.importItem(requirements);
                }
            }, file);
            CompositeDocumentProvider.storeLocalFiles.set(false);
        }
    }

    public void importZip(final File file) throws IOException {
        final Path tempDirectory = extract(file);
        doImport(adapter, new RequirementsUpdateStrategy() {
            @Override
            public void importItem(Requirements requirements) {
                for (final Requirement requirement : requirements.getRequirements()) {
                    requirement.russian = getAttachedByRelativePath(requirement.getRussian(), tempDirectory);
                    requirement.english = getAttachedByRelativePath(requirement.getEnglish(), tempDirectory);
                    if(requirement.parent == null) {
                        requirement.setLifecycle(Requirement.LIFECYCLE_SIGNED);
                    }
                }
                strategy.importItem(requirements);
            }
        }, new File(tempDirectory + File.separator + "requirements.xml"));
        FileUtils.forceDelete(tempDirectory.toFile());
    }

    private Path extract(File file) throws IOException {
        final Path tempDirectory = Files.createTempDirectory(repositoryService.getTempDir().toPath(), "req");
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            for (ZipEntry entry = zipInputStream.getNextEntry(); entry != null; entry = zipInputStream.getNextEntry()) {
                final File destination = new File(tempDirectory + File.separator + entry.getName());
                if (!entry.isDirectory()) {
                    FileUtils.copyToFile(zipInputStream, destination);
                } else {
                    FileUtils.forceMkdir(destination);
                }
                zipInputStream.closeEntry();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass()).error("while importing requirements", e);
            throw e;
        }
        return tempDirectory;
    }

    private AttachedFile getAttachedFromResource(final AttachedFile attachedFile) {
        AttachedFile newAttachedFile = null;
        if (attachedFile != null && attachedFile.name != null) {
            try {
            newAttachedFile = new AttachedFile();
            URL resource = getClass().getResource("pdfrules/" + attachedFile.getName());
            if (resource == null) {
                return null;
            }
                File file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
                newAttachedFile.setId(file.getAbsolutePath());
                newAttachedFile.setName(file.getName());
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
            newAttachedFile.mime = PDF_MIME_TYPE;
        }
        return newAttachedFile;
    }

    private AttachedFile getAttachedByRelativePath(final AttachedFile attachedFile, Path dir) {
        if (attachedFile != null && attachedFile.name != null) {
            try {
                final AttachedFile result = new AttachedFile();
                final File file = new File(dir + File.separator + URLDecoder.decode(attachedFile.getName(), "UTF-8"));
                result.mime = PDF_MIME_TYPE;
                result.setId(file.getAbsolutePath());
                result.setName(file.getName());
                return result;
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
}
