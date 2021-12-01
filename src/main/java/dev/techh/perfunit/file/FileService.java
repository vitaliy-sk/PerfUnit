package dev.techh.perfunit.file;

import dev.techh.perfunit.configuration.data.Configuration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@Singleton
public class FileService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final File rootFolder;

    @Inject
    public FileService(Configuration configuration) {
        rootFolder = new File(configuration.getReportPath());
        recreateFolder(rootFolder);
    }

    public File getRootFolder() {
        return rootFolder;
    }

    public File getFolder(String name ) {
        File folder = new File(rootFolder, name);
        folder.mkdirs();
        return folder;
    }

    public File getTempFolder( String name ) {
        File folder = getFolder(name);
        folder.deleteOnExit();
        return folder;
    }

    private void recreateFolder(File folder) {
        try {
            LOG.info("Creating folder [{}]", folder.getAbsolutePath());
            if (folder.exists()) { removeFolder(folder); }
            folder.mkdirs();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeFolder(File folder) throws IOException {
        Files.walk(folder.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

}
