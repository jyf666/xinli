import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import play.libs.Json;
import utils.enums.QuestionTypeEnum;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by wangbin on 2015/11/10.
 */
public class TestTT {

    /**
     *
     * @return
     */
    public  Double caculateDistance(double[] x,double[] y){
        double total = 0;
        for (int i = 0; i < x.length; i++) {
            total += Math.pow( x[i]- y[i],2);
        }
        return Math.sqrt(total);
    }

    public Double caculateMaxDistance(Integer start,Integer end,double[] y){
        double max = 0;
        if(y.length == 5) {
            for (int i = start; i <= end ; i++) {
                for (int j = start; j <= end; j++) {
                    for (int k = start; k <= end; k++) {
                        for (int l = start; l <= end; l++) {
                            for (int m = start; m <= end ; m++) {
                                double[] x ={i,j,k,l,m};
                                if(this.caculateDistance(x,y) >= max)
                                    max = this.caculateDistance(x,y);
                            }
                        }
                    }
                }
            }
        }
        return max;
    }

    public double[] getPointsBySort(String pointsStr){
        ObjectNode objectNode = (ObjectNode) Json.parse(pointsStr);
        Iterator iterator = objectNode.fieldNames();
        List<Character> chars =  new ArrayList<>();
        while (iterator.hasNext()){
            String choice = (String)iterator.next();
            chars.add(choice.charAt(0));
        }
        double[] numberBySort = new double[objectNode.size()];
        Collections.sort(chars);
        for (int i = 0; i < chars.size(); i++) {
            numberBySort[i] = objectNode.findPath(String.valueOf(chars.get(i))).asDouble();
        }

        return numberBySort;
    }


    public  void cutImage(String src,String dest,int x,int y,int w,int h) throws IOException {
        Iterator iterator = ImageIO.getImageReadersByFormatName("png");
        ImageReader reader = (ImageReader)iterator.next();
        InputStream in= new FileInputStream(src);
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(x, y, w,h);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0,param);
        ImageIO.write(bi, "png", new File(dest));

    }


    public static void main(String[] args) {
        System.out.println(QuestionTypeEnum.ANALOGIC_REASONING.getId());
    }





}
