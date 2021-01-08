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
        String file_dir = properties.get("file_dir").toString();
        String new_name = properties.get("new_name").toString() + ".pdf";
        String[] file_names = properties.get("file_names").toString().split(", ");
        in.close();

        for (int i=0; i< file_names.length; i++){
            file_names[i] = file_dir + file_names[i] + ".pdf";
        }

        PdfDocument dest_doc = new PdfDocument(new PdfWriter(file_dir + new_name));
        PdfMerger merger = new PdfMerger(dest_doc);
        merger.setCloseSourceDocuments(true);  // merge 后自动关闭

        // 依次merge
        for (String file_name : file_names) {
            PdfDocument temp_doc = new PdfDocument(new PdfReader(file_name));
            merger.merge(temp_doc, 1, temp_doc.getNumberOfPages());
        }
        dest_doc.close();

    }
}

