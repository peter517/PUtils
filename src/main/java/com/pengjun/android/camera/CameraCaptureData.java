package com.pengjun.android.camera;

public class CameraCaptureData {

	private byte[] data;
	private int width;
	private int heigth;
	public byte[] getData() {
		return data;
	}

	public CameraCaptureData(byte[] data, int width, int heigth) {
		super();
		this.data = data;
		this.width = width;
		this.heigth = heigth;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeigth() {
		return heigth;
	}
	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}

}
