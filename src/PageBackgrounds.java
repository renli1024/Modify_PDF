import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.File;
import java.io.IOException;


public class PageBackgrounds {

    public static void main(String[] args) throws Exception {
        String orig_path = "original PDF file path";
        String colored_path = "generated PDF file path";
        PageBackgrounds pbClass = new PageBackgrounds();
        pbClass.manipulateDir(orig_path, colored_path);

    }

    // 要保证这两个目录都是存在的
    public void manipulateDir(String origDirPath, String coloredDirPath){
        File origDir = new File(origDirPath);
        File[] origFiles = origDir.listFiles();

        for (int i=0;i<origFiles.length;i++){
            // 判断是文件还是目录
            if(origFiles[i].isFile()){
                // 是文件

                // 判断是否是pdf文件
                Boolean isPDF = checkPDF(origFiles[i].toString());
                if (isPDF == false){
                    continue;
                }

                // 是pdf文件
                // 判断是否存在相应colored pdf文件
                String origFileName = origFiles[i].toString();
                String[] origPathParts = origFileName.split("/");
                String noPathFileName = origPathParts[origPathParts.length-1];
                String newColoredFilePath = coloredDirPath + "/" + noPathFileName;
                File newColoredFile = new File(newColoredFilePath);
                if (!newColoredFile.exists()){
                    // 不存在相应PDF文件，复制pdf文件，并进行颜色转换
                    try{
                        manipulatePdf(origFileName, newColoredFilePath);
                        System.out.println("New PDF file: "+ newColoredFilePath);
                    }
                    catch (java.lang.Exception e){
                        e.printStackTrace();
                    }
                }

            }
            else{
                //是目录，递归

                //判断是否存在相应colored目录
                String origDirName = origFiles[i].toString();
                String[] origPathParts = origDirName.split("/");
                String noPathFileName = origPathParts[origPathParts.length-1];
                String newColoredDirPath = coloredDirPath + "/" + noPathFileName;
                File newColoredDir = new File(newColoredDirPath);
                if (!newColoredDir.exists()){
                    // 不存在相应colored目录，新建一个
                    newColoredDir.mkdir();
                    System.out.println("New Directory: "+ newColoredDirPath);
                }

                // 递归操作子目录
                manipulateDir(origDirName, newColoredDirPath);
            }
        }
    }

    // 判断一个文件是否是pdf文件
    protected Boolean checkPDF(String filePath) {
        Boolean isPDF = false;
        try{
            PdfDocument doc = new PdfDocument(new PdfReader(filePath));
            isPDF = true;
            doc.close();

        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (com.itextpdf.io.IOException e){
            System.out.println("File: " + filePath + " is not PDF.");
        }

        return isPDF;
    }

    protected void manipulatePdf(String src, String dest) throws Exception {
        PdfDocument srcDoc = new PdfDocument(new PdfReader(src).setUnethicalReading(true));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new PageBackgroundsEventHandler());
        srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), pdfDoc);
        pdfDoc.close();

    }

    private static class PageBackgroundsEventHandler implements IEventHandler {
        @Override
        public void handleEvent(Event currentEvent) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();

            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(),
                    page.getResources(), pdfDoc);
            Rectangle rect = page.getPageSize();
            canvas
                    .saveState()
                    .setFillColor(new DeviceRgb(199, 237, 204))
                    .rectangle(rect)
                    .fill()
                    .restoreState();

            canvas.release();

        }
    }
}
