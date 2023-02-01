package com.rbg.springmailingservice.service;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

class QRCodeServiceTest {

	@Test
	void testOk() {
		assertNotNull(QRCodeService.getQRCodeImage("texto", 200, 200));
		assertNotNull(new QRCodeService());
	}

	@Test
	void test() {
		try (MockedConstruction<QRCodeWriter> mocked = mockConstruction(QRCodeWriter.class, (mock, context) -> {
			when(mock.encode(anyString(), any(BarcodeFormat.class), anyInt(), anyInt()))
					.thenThrow(NullPointerException.class);
		})) {
			assertTrue(QRCodeService.getQRCodeImage("texto", 200, 200).isEmpty());
		}
		catch (Exception e) {
			assertInstanceOf(Exception.class, e);
		}
	}
}
