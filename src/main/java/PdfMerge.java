import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.utils.PdfMerger;
import java.io.*;
import java.util.Properties;


public class PdfMerge {

    public static void main(String[] args) throws IOException {
        // 读取配置文件中的地址信息
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("project.properties");
        properties.load(in);
        String dir = properties.get("file_dir").toString();
        String[] input_pdfs = properties.get("merge_input").toString().split(", ");
        String output_pdf = properties.get("merge_output").toString();
        in.close();

        for (int i=0; i< input_pdfs.length; i++){
            input_pdfs[i] = dir + input_pdfs[i];
        }

        PdfDocument dest_doc = new PdfDocument(new PdfWriter(dir + output_pdf));
        PdfMerger merger = new PdfMerger(dest_doc);
        merger.setCloseSourceDocuments(true);  // merge 后自动关闭

        // 依次merge
        for (String file_name : input_pdfs) {
            PdfDocument temp_doc = new PdfDocument(new PdfReader(file_name));
            merger.merge(temp_doc, 1, temp_doc.getNumberOfPages());
        }
        dest_doc.close();

        String sep = File.separator;
        for (int i=0; i< input_pdfs.length; i++){
            String input = input_pdfs[i].split(sep)[input_pdfs[i].split(sep).length-1];
            System.out.printf("Merging pdf %d: %s\n", i+1, input);
        }
        System.out.printf("Merging output: %s\n", output_pdf);
    }
}

