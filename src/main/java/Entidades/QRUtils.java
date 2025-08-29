package Entidades;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

public class QRUtils {

    /**
     * Genera un BufferedImage con el QR del contenido dado.
     * @param contenido Texto/URL a codificar
     * @param sizePx tamaño en px (ancho=alto)
     */
    public static BufferedImage generarQRImage(String contenido, int sizePx) throws Exception {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix matrix = new MultiFormatWriter().encode(
                contenido, BarcodeFormat.QR_CODE, sizePx, sizePx, hints
        );

        // ZXing nos da directamente un BufferedImage
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}
