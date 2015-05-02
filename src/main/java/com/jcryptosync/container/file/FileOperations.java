package com.jcryptosync.container.file;

import com.jcryptosync.container.operations.AddFileAsync;
import com.jcryptosync.container.operations.DeleteFileAsync;
import com.jcryptosync.container.operations.ModificateFileAsync;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class FileOperations {

    public static List<ForkJoinTask<Void>> operations = new CopyOnWriteArrayList<>();

    public static void addNewFile(Path path) {
        RecursiveAction action = new AddFileAsync(path);
        operations.add(action.fork());
    }

    public static void deleteFile(Path path) {
        RecursiveAction action = new DeleteFileAsync(path);
        operations.add(action.fork());
    }

    public static void modificateFile(Path path) {
        RecursiveAction action = new ModificateFileAsync(path);
        operations.add(action.fork());
    }
}
