/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.data;

import Dragon.server.io.MySession;
import Dragon.utils.FileIO;
import com.girlkun.network.io.Message;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Administrator
 */
public class smallVersion {

    public static byte[][] smallVersion;

    public static void get() {
        try {
            smallVersion = new byte[4][];
            File[] files;
            int max = -1;
            int id;

            // Chỉ đọc từ folder x4 vì đây là folder gốc
            File x4Folder = new File("data/girlkun/icon/x4");
            if (x4Folder.exists() && x4Folder.isDirectory()) {
                files = x4Folder.listFiles();
                if (files != null) {
                    // Tìm ID lớn nhất
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".png")) {
                            if ((id = Integer.parseInt(FileIO.replacePng(file.getName()))) > max) {
                                max = id;
                            }
                        }
                    }

                    // Tạo mảng cho tất cả zoom levels (1-4)
                    for (int i = 0; i < 4; i++) {
                        smallVersion[i] = new byte[max + 1];
                        for (File file : files) {
                            if (file.isFile() && file.getName().endsWith(".png")) {
                                smallVersion[i][Integer.parseInt(FileIO.replacePng(file.getName()))] = (byte) (Files.readAllBytes(file.toPath()).length % 127);
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("Error in smallVersion.get(): " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void send(MySession session) {
        try {
            byte[] data = smallVersion[session.zoomLevel - 1];
            Message msg = new Message(-77);
            msg.writer().writeShort(data.length);
            msg.writer().write(data);
            msg.writer().flush();
            session.sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
        }
    }
}
