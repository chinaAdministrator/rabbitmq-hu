package com.oracle.config;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Service
public class QRcodeService {
    private static final String CHARSET = "utf-8";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 400;
    // LOGO宽度
    private static final int WIDTH = 150;
    // LOGO高度
    private static final int HEIGHT = 150;

    private static BufferedImage createImage(List<String> code, String content, String imgPath, boolean needCompress) throws Exception {
        Map<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        //在二维码下方增加文字显示
        BufferedImage bi = new BufferedImage(width, height+20*code.size(), BufferedImage.TYPE_INT_RGB);//将高度增加20，在二维码下方增加一个区域显示文字
        Graphics2D g2 = bi.createGraphics();
        g2.setBackground(new Color(0xFF,0xFF,0xFF));
        g2.clearRect(0, 0, width, height);

        g2.drawImage(image, 0, 20*code.size(), width, height, null); //x，y为image图片的位置
        //设置生成图片的文字样式
        Font font = new Font("黑体", Font.BOLD, 17);
        g2.setFont(font);
        g2.setPaint(new Color(0x0,0x0,0x0));

        // 设置字体在图片中的位置 在这里是居中
        for(int i=0;i<code.size();){
            // 防止生成的文字带有锯齿
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // 在图片上生成文字
            g2.drawString(code.get(i), 5, 20*++i); //x，y为文字的位置
        }

        image=bi;
        if (imgPath == null || "".equals(imgPath))
            return image;
        // 插入图片
        QRcodeService.insertImage(image, imgPath, needCompress, code);
        return image;
    }

    /**
     * 插入logo图标
     * @param source 二维码Image对象
     * @param imgPath logo路径
     * @param needCompress 是否需要缩小logo图标
     * @param code
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, String imgPath, boolean needCompress, List<String> code) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println("" + imgPath + "该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.drawImage(src, 0, 0, width, height, null); // 绘制图
            // 画边框
            g.setColor(Color.BLACK);
            g.drawRect(4, 4, width - 8, height - 8);
            g.drawRect(5, 5, width - 10, height - 10);

            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y+code.size()*20, width, height, null);//logo的位置可能需要调整
        Shape shape = new RoundRectangle2D.Float(x, y+code.size()*20, width, width, 6, 6);//阴影的位置可能需要调整
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 生成包含logo的二维码
     * @param code 需要显示在二维码上方的list文字集合
     * @param content 二维码中包含的内容
     * @param imgPath logo图像地址
     * @param needCompress 是否需要缩小logo图标
     * @param fileUtil 保存文件的类对象
     * @return 保存后的文件路径
     * @throws Exception
     */
    public static String encode(List<String> code, String content, String imgPath, boolean needCompress, FileUtil fileUtil) throws Exception {
        BufferedImage image = QRcodeService.createImage(code, content, imgPath, needCompress);
        //获取当前时间并格式化
        String path=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date());
        path=path.substring(0,10);
        //保存文件到磁盘
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        InputStream input = new ByteArrayInputStream(os.toByteArray());
        return fileUtil.writeFile(input, "E:/"+path, path+".jpg");
    }

    /**
     * 不包含logo的二维码
     * @param code 需要显示在二维码上方的list字符串集合
     * @param content 二维码中的内容
     * @param fileUtil 保存文件的工具类
     * @return 保存成功之后的路径地址
     * @throws Exception
     */
    public String encode(List<String> code, String content, FileUtil fileUtil) throws Exception {
        return QRcodeService.encode(code, content, null, false, fileUtil);
    }

//    public static void main(String[] args) throws Exception {
//        List<String> showContent = new ArrayList<>();
//        //二维码上没有显示内容则传递空的list集合即可
////        showContent.add("名称：这是显示在二维码上的文字");
////        showContent.add("编码：这是显示在二维码上的文字");
//        //无logo的二维码
//        String path = new QRcodeService().encode(showContent, "这是二维码中的内容", new FileUtil());
//        System.out.println("无logo的二维码  "+path);
//        //有logo的二维码
//        path = new QRcodeService().encode(showContent, "http://lilidongxu.oss-cn-beijing.aliyuncs.com/2021/03/08/3.jpg?x-oss-process=image/resize,w_500", "E:/4.jpg",true, new FileUtil());
//        System.out.println("有logo的二维码  "+path);
//    }
}
