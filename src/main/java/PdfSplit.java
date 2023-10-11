import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PageRange;
import com.itextpdf.kernel.utils.PdfSplitter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class PdfSplit {

    public static void main(String[] args) throws IOException {
        // 读取配置文件
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("project.properties");
        properties.load(in);
        String dir = properties.get("file_dir").toString();
        String input = properties.get("split_input").toString();
        String output = properties.get("split_output").toString();

        String[] temp_list = properties.get("split_pages").toString().split(", ");
        List<Integer> split_pages = new ArrayList<Integer>();
        int last_p = 1;
        for (String s : temp_list) {
            int p = Integer.parseInt(s);
            // 判断前后页数满足大小约束
            assert last_p < p ;
            // 我设置的划分效果指定结束页, 但iText7 splitByPageNumbers()是指定开始页
            // 如输入2, 4, 我希望的效果: 1~2, 3~4, 因此传入函数的值需要为: 3, 5 (itex会自动在开头补1)
            split_pages.add(p+1);
            last_p = p;
        }

        new PdfSplit().manipulatePdf(dir, input, output, split_pages);
        in.close();
    }

    protected void manipulatePdf(final String dir, final String src, final String dst,
                                 final List<Integer> split_pages) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(dir+src));
        System.out.printf("Split input: %s\n", src);
        List<PdfDocument> splitDocuments = new PdfSplitter(pdfDoc) {
            int part_index = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    System.out.printf("Split output %d: %s\n", part_index, String.format(dst, part_index));
                    return new PdfWriter(String.format(dir+dst, part_index++));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
          }.splitByPageNumbers(split_pages);

        for (PdfDocument doc : splitDocuments) {
            doc.close();
        }

        pdfDoc.close();
    }
}