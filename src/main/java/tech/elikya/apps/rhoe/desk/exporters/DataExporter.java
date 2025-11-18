/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.exporters;

import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import tech.elikya.apps.rhoe.desk.ui.Notifier;
import tech.elikya.apps.rhoe.desk.ui.StagesPaths;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Mafole Loemelah
 */
public class DataExporter {
    
    private static final Properties LANG = ControlsHandler.getLanguage();
    private static final String PATH = System.getProperty("user.home") + File.separator
            + "Documents" + File.separator + "Elikya Rhoe";
    
    private static void createDirectory() {
        File dir = new File(PATH);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) Notifier.notify(StagesPaths.ERROR_NOTIF, LANG.getProperty("no_export_folder"));
        }
    }
    
    public static void writeStream(XSSFWorkbook workbook) {
        createDirectory();
        String fileName = File.separator + LocalDateTime.now()
                .toString().replaceAll(":", "-") + ".xlsx";
        try (FileOutputStream stream = new FileOutputStream(PATH + fileName)) {
            workbook.write(stream);
            Notifier.notify(StagesPaths.SUCCESS_NOTIF, LANG.getProperty("data_exported"));
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println("DATA NOT EXPORTED");
        }
    }
    
}
