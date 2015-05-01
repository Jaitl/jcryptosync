package com.jcryptosync.container;

import com.jcryptosync.QuickPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

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
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
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


            // Dequeueing events
            WatchEvent.Kind<?> kind = null;
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                // Get the type of the event
                kind = watchEvent.kind();
                if (StandardWatchEventKinds.ENTRY_CREATE == kind) {
                    // A new Path was created
                    Path newPath = ((WatchEvent<Path>) watchEvent).context();
                    log.info("created new file: " + newPath);
                    newPath = QuickPreferences.getPathToFilesDir().resolve(newPath);

                    new AddFileAsync(newPath).fork();
                }
            }

            key.reset();
        }

        log.info("stop watch Files folder");
    }
}
