package com.jcryptosync.ui.container;

import com.jcryptosync.vfs.webdav.AbstractFile;
import com.jcryptosync.vfs.webdav.CryptFile;
import com.jcryptosync.vfs.webdav.Folder;

public class MessageService {

    private static ShowMessage sm;

    public static void setMessageService(ShowMessage showMessage) {
        sm = showMessage;
    }

    public static void showMessage(String message) {
        if(sm != null)
            sm.showMessage(message);
    }

    public static void authenticationComplited(String clientId) {
        showMessage("выполнена аутентификация с клиентом: " + getIdClient(clientId));
    }

    public static void startSyncWithClient(String clientId) {
        showMessage("синхроназация файлов с клиентом: " + getIdClient(clientId));
    }

    public static void syncFile(CryptFile cryptFile) {
        showMessage(String.format("синхроназация файла \"%s\"", cryptFile.getName()));
    }

    public static void syncFolder(Folder folder) {
        showMessage(String.format("синхроназация каталога \"%s\"", folder.getName()));
    }

    public static void addFile(AbstractFile file) {
        if(file instanceof CryptFile) {
            showMessage(String.format("добавлен новый файл \"%s\"", file.getName()));
        } else {
            showMessage(String.format("добавлен новый каталог \"%s\"", file.getName()));
        }
    }

    public static void openFile(CryptFile cryptFile) {
        showMessage(String.format("открыт файл \"%s\"", cryptFile.getName()));
    }

    public static void updateFile(CryptFile cryptFile) {
        showMessage(String.format("обновлен файл \"%s\"", cryptFile.getName()));
    }

    public static void renameFile(AbstractFile file, String newName) {
        if(file instanceof CryptFile) {
            showMessage(String.format("файл \"%s\" переименован в \"%s\"", file.getName(), newName));
        } else {
            showMessage(String.format("каталог \"%s\" переименован в \"%s\"", file.getName(), newName));
        }
    }

    public static void moveFile(AbstractFile file, Folder folder) {
        if(file instanceof CryptFile) {
            showMessage(String.format("файл \"%s\" перемещен в каталог \"%s\"", file.getName(), folder.getName()));
        } else {
            showMessage(String.format("каталог \"%s\" перемещен в каталог \"%s\"", file.getName(), folder.getName()));
        }
    }

    public static void deleteFile(AbstractFile file) {
        if(file instanceof CryptFile) {
            showMessage(String.format("удален файл \"%s\"", file.getName()));
        } else {
            showMessage(String.format("удален каталог \"%s\"", file.getName()));
        }
    }

    public static void cryptFile(CryptFile file) {
        showMessage(String.format("зашифрован файл \"%s\"", file.getName()));
    }

    public static void encryptFile(CryptFile file) {
        showMessage(String.format("расшифрован файл \"%s\"", file.getName()));
    }

    private static String getIdClient(String clientId) {
        String[] id = clientId.split("-");

        return id[0];
    }
}
