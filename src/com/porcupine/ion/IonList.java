package com.porcupine.ion;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * Ionizable Arraylist
 * 
 * @author Ondřej Hruška (MightyPork)
 */
@SuppressWarnings("javadoc")
public class IonList extends ArrayList<Object> implements Ionizable {

	public boolean getBoolean(int index) {
		return (Boolean) get(index);
	}

	public boolean getBool(int index) {
		return (Boolean) get(index);
	}

	public byte getByte(int index) {
		return (Byte) get(index);
	}

	public char getChar(int index) {
		return (Character) get(index);
	}

	public char getCharacter(int index) {
		return (Character) get(index);
	}

	public short getShort(int index) {
		return (Short) get(index);
	}

	public int getInteger(int index) {
		return (Integer) get(index);
	}

	public int getInt(int index) {
		return (Integer) get(index);
	}

	public long getLong(int index) {
		return (Long) get(index);
	}

	public float getFloat(int index) {
		return (Float) get(index);
	}

	public double getDouble(int index) {
		return (Double) get(index);
	}

	public String getString(int index) {
		return (String) get(index);
	}

	@Override
	public Object get(int index) {
		return super.get(index);
	}

	public void addBoolean(boolean num) {
		add(num);
	}

	public void addBool(boolean num) {
		add(num);
	}

	public void addByte(int num) {
		add((byte) num);
	}

	public void addChar(char num) {
		add(num);
	}

	public void addShort(int num) {
		add((short) num);
	}

	public void addInteger(int num) {
		add(num);
	}

	public void addInt(int num) {
		add(num);
	}

	public void addLong(long num) {
		add(num);
	}

	public void addFloat(double num) {
		add((float) num);
	}

	public void addDouble(double num) {
		add(num);
	}

	public void addString(String num) {
		add(num);
	}

	@Override
	public void ionRead(InputStream in) throws IOException {
		while (true) {
			byte b = StreamUtils.readByte(in);
			if (b == IonMarks.ENTRY) {
				Object value = Ion.readObject(in);
				add(value);
			} else if (b == IonMarks.END) {
				break;
			} else {
				throw new RuntimeException("Unexpected mark in IonList: " + Integer.toHexString(b));
			}
		}
	}

	@Override
	public void ionWrite(OutputStream out) throws IOException {
		for (Object entry : this) {
			StreamUtils.writeByte(out, IonMarks.ENTRY);
			Ion.writeObject(out, entry);
		}
		StreamUtils.writeByte(out, IonMarks.END);
	}

	@Override
	public byte ionMark() {
		return IonMarks.LIST;
	}

}
