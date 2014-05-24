package com.porcupine.ion;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * Ionizable Arraylist
 * 
 * @author Ondřej Hruška (MightyPork)
 * @param <T>
 */
public abstract class AbstractIonList<T> extends ArrayList<T> implements Ionizable {

	@Override
	public void ionRead(InputStream in) throws IOException {
		while (true) {
			byte b = StreamUtils.readByte(in);

			if (b == IonMarks.ENTRY) {
				T value = (T) Ion.readObject(in);
				add(value);
			} else if (b == IonMarks.END) {
				break;
			} else {
				throw new RuntimeException("Unexpected mark in AbstractIonList: " + Integer.toHexString(b));
			}
		}
		ionReadCustomData(in);
	}

	@Override
	public void ionWrite(OutputStream out) throws IOException {
		for (T entry : this) {
			if (entry instanceof IonizableOptional && !((IonizableOptional) entry).ionShouldSave()) continue;
			StreamUtils.writeByte(out, IonMarks.ENTRY);
			Ion.writeObject(out, entry);
		}
		StreamUtils.writeByte(out, IonMarks.END);
		ionWriteCustomData(out);
	}

	/**
	 * Read custom data of this AbstractIonList implementation
	 * 
	 * @param in input stream
	 */
	public void ionReadCustomData(InputStream in) {}

	/**
	 * Write custom data of this AbstractIonList implementation
	 * 
	 * @param out output stream
	 */
	public void ionWriteCustomData(OutputStream out) {}


	@Override
	public abstract byte ionMark();

}
