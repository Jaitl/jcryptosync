package com.jcryptosync.container;

import com.jcryptosync.container.exceptoins.ContainerMountException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WindowsContainerManager extends ContainerManager {

    private char diskLetter;

    public WindowsContainerManager() {
        Path home = Paths.get(System.getProperty("user.home"));
    }

    @Override
    public void mountContainer() throws ContainerMountException {
        diskLetter = findLetter('c');
        
        Runtime rt = Runtime.getRuntime();
        Process pr = null;

        try {
            pr = rt.exec(String.format("net use %s: \"%s\" /User:%s %s", diskLetter, pathToWebDavServer, user.getName(), user.getPassword()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int code = pr.waitFor();
            if(code != 0)
                throw new ContainerMountException("�� ������� ���������� ���������");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unmountContainer() throws ContainerMountException {
        Runtime rt = Runtime.getRuntime();
        Process pr = null;

        try {
            pr = rt.exec(String.format("net use %s: /Delete", diskLetter));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int code = pr.waitFor();
            if(code != 0)
                throw new ContainerMountException("�� ������� �������� ���������");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isMount() {
        return isUsedLetter(diskLetter);
    }

    public char findLetter(char startLetter) {
        File[] roots = File.listRoots();

        boolean isContain = true;
        char letter = startLetter;

        while (isContain) {
            for (File f : roots) {
                if (f.getAbsolutePath().toLowerCase().contains(Character.toString(letter))) {
                    isContain = true;
                    letter++;
                    break;
                } else {
                    isContain = false;
                }
            }
        }

        return letter;
    }

    public boolean isUsedLetter(char letter)  {
        File[] roots = File.listRoots();

        for (File f : roots) {
            if (f.getAbsolutePath().toLowerCase().contains(Character.toString(letter))) {
                return true;
            }
        }

        return false;
    }
}
