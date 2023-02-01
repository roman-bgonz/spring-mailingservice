package com.rbg.springmailingservice.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * The Class QRCodeHelper.
 */
public class QRCodeService {

	/**
	 * Gets the QR code image.
	 *
	 * @param text   Url which will be decoded when qr is scanned
	 * @param width  Width of QR image
	 * @param height Height of QR image
	 * @return QR image
	 */
	public static String getQRCodeImage(String text, int width, int height) {
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
			byte[] qr = pngOutputStream.toByteArray();
			return Base64.getEncoder().encodeToString(qr);
		}
		catch (Exception e) {
			return "";
		}
	}
}
