package com.oracle.config;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class RecodeUtil {

    /**
     *  生成二维码
     * @param content 二维码的内容
     * @return BitMatrix对象
     * */
    public static BitMatrix createCode(String content, HttpServletResponse response) throws IOException {

        // 设置响应流信息
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        OutputStream stream = response.getOutputStream();

        //二维码的宽高
        int width = 200;
        int height = 200;

        //其他参数，如字符集编码
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //容错级别为H
        hints.put(EncodeHintType.ERROR_CORRECTION , ErrorCorrectionLevel.H);
        //白边的宽度，可取0~4
        hints.put(EncodeHintType.MARGIN , 0);

        BitMatrix bitMatrix = null;
        try {
            //生成矩阵，因为我的业务场景传来的是编码之后的URL，所以先解码
            bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, width, height, hints);

            //以流的形式输出到前端
            MatrixToImageWriter.writeToStream(bitMatrix , "jpg" , stream);
            //bitMatrix = deleteWhite(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitMatrix;
    }

    /**
     *  删除生成的二维码周围的白边，根据审美决定是否删除
     * @param matrix BitMatrix对象
     * @return BitMatrix对象
     * */
    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }

}
