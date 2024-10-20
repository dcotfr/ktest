package ktest.domain.xlsx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class XlsxFileUtils {
    private XlsxFileUtils() {
    }

    static void save(final File pFile, final Workbook pWorkbook) throws IOException {
        try (final var fos = new FileOutputStream(pFile);
             final var zos = new ZipOutputStream(fos)) {
            addEntry(zos, "[Content_Types].xml", new ContentTypes(pWorkbook).toXml());
            addEntry(zos, "_rels/.rels", new Rels(null).toXml());
            addEntry(zos, "xl/sharedStrings.xml", pWorkbook.sharedStrings().toXml());
            addEntry(zos, "xl/styles.xml", pWorkbook.styles().toXml());
            addEntry(zos, "xl/workbook.xml", pWorkbook.toXml());
            addEntry(zos, "xl/_rels/workbook.xml.rels", new Rels(pWorkbook).toXml());
            for (final var ws : pWorkbook.worksheets()) {
                addEntry(zos, "xl/worksheets/sheet" + pWorkbook.worksheetId(ws) + ".xml", ws.toXml());
            }
        }
    }

    private static void addEntry(final ZipOutputStream pZOS, final String pEntry, final String pContent) throws IOException {
        pZOS.putNextEntry(new ZipEntry(pEntry));
        pZOS.write(pContent.getBytes(StandardCharsets.UTF_8), 0, pContent.length());
    }
}
