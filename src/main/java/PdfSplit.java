package com.itextpdf.samples.sandbox.merge;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PageRange;
import com.itextpdf.kernel.utils.PdfSplitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class PdfSplit {
//    public static final String DEST = "./test/emnlp2021_%s.pdf";
//
//    public static final String RESOURCE = "./test/emnlp2021.pdf";

    public static void main(String[] args) throws IOException {
        // 读取配置文件
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("project.properties");
        properties.load(in);
        String dst_files = properties.get("dst_files").toString();
        String src_file = properties.get("src_file").toString();
        Integer split_page = Integer.valueOf(properties.get("split_page").toString());

        new com.itextpdf.samples.sandbox.merge.PdfSplit().manipulatePdf(src_file, dst_files, split_page);
    }

    protected void manipulatePdf(final String src, final String dst, final Integer split_page) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src));

        List<PdfDocument> splitDocuments = new PdfSplitter(pdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(String.format(dst, partNumber++));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }.splitByPageCount(split_page);

        for (PdfDocument doc : splitDocuments) {
            doc.close();
        }

        pdfDoc.close();
    }
}