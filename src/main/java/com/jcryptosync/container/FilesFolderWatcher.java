package com.jcryptosync.container;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.file.FileOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class FilesFolderWatcher extends Thread {

    protected static Logger log = LoggerFactory.getLogger(FilesFolderWatcher.class);
    WatchService watchService;

    public FilesFolderWatcher() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            log.error("fail create watchService", e);
        }
    }

    public void run() {
        startWatch();
    }

    public void stopWatch() {
        try {
            watchService.close();
        } catch (IOException e) {
            log.error("fail close watchService", e);
        }
    }

    public void startWatch() {
        log.info("start watch Files folder");

        Path path = QuickPreferences.getPathToFilesDir();

        try {
            path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        WatchKey key = null;

        while (true) {
            try {
                key = watchService.take();
            } catch (ClosedWatchServiceException e) {
                break;
            } catch (InterruptedException e) {
                log.error("error", e);
            }

            List<WatchEvent<?>> list = key.pollEvents();

            key.reset();


            // Dequeueing events
            WatchEvent.Kind<?> kind = null;
            for (WatchEvent<?> watchEvent : list) {
                // Get the type of the event
                kind = watchEvent.kind();

                Path eventPath = ((WatchEvent<Path>) watchEvent).context();

                if(eventPath.getFileName().toString().contains(".goutputstream"))
                    continue;

                if (StandardWatchEventKinds.ENTRY_CREATE == kind) {
                    // A new Path was created

                    log.info("request to add file: " + eventPath);
                    eventPath = QuickPreferences.getPathToFilesDir().resolve(eventPath);

                    FileOperations.addNewFile(eventPath);

                } else if(StandardWatchEventKinds.ENTRY_DELETE == kind) {
                    log.info("request to delete file: " + eventPath);

                    FileOperations.deleteFile(eventPath);
                } else if(StandardWatchEventKinds.ENTRY_MODIFY == kind) {
                    eventPath = QuickPreferences.getPathToFilesDir().resolve(eventPath);
                    log.info("request to modificate file: " + eventPath);

                    FileOperations.modificateFile(eventPath);
                }
            }
        }

        log.info("stop watch Files folder");
    }
}
