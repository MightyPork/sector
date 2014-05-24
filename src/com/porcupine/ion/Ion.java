package com.porcupine.ion;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.porcupine.math.Calc;


/**
 * Universal data storage system
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class Ion {

	/** Ionizables<Mark, Class> */
	private static Map<Byte, Class<?>> customIonizables = new HashMap<Byte, Class<?>>();

	// register default ionizables
	static {
		registerIonizable(IonMarks.MAP, IonMap.class);
		registerIonizable(IonMarks.LIST, IonList.class);
	}

	/**
	 * Register new Ionizable for direct reconstructing.
	 * 
	 * @param mark byte mark to be used, see {@link IonMarks} for reference.
	 * @param objClass class of the registered Ionizable
	 */
	public static void registerIonizable(byte mark, Class<?> objClass) {
		if (customIonizables.containsKey(mark)) {
			throw new RuntimeException("IonMark " + mark + " is already used @ " + objClass.getSimpleName());
		}
		customIonizables.put(mark, objClass);
	}



	/**
	 * Load Ion object from file.
	 * 
	 * @param file file path
	 * @return the loaded object
	 */
	public static Object fromFile(String file) {
		return fromFile(new File(file));
	}


	/**
	 * Load Ion object from file.
	 * 
	 * @param file file
	 * @return the loaded object
	 */
	public static Object fromFile(File file) {
		try {
			InputStream in = new FileInputStream(file);

			Object obj = fromStream(in);

			in.close();
			return obj;
		} catch (FileNotFoundException e) {
			System.err.println("Could not find ION file " + file);
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Load Ion object from stream.
	 * 
	 * @param in input stream
	 * @return the loaded object
	 */
	public static Object fromStream(InputStream in) {
		try {
			return readObject(in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Store Ion object to file.
	 * 
	 * @param path file path
	 * @param obj object to store
	 */
	public static void toFile(String path, Object obj) {
		toFile(new File(path), obj);
	}

	/**
	 * Store Ion object to file.
	 * 
	 * @param path file path
	 * @param obj object to store
	 */
	public static void toFile(File path, Object obj) {
		try {
			String f = path.toString();
			File dir = new File(f.substring(0, f.lastIndexOf(File.separator)));

			dir.mkdirs();

			OutputStream out = new FileOutputStream(path);

			toStream(out, obj);

			out.flush();
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Store Ion object to output stream.
	 * 
	 * @param out output stream *
	 * @param obj object to store
	 */
	public static void toStream(OutputStream out, Object obj) {
		try {
			writeObject(out, obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Read single ionizable or primitive object from input stream
	 * 
	 * @param in input stream
	 * @return the loaded object
	 */
	public static Object readObject(InputStream in) {
		try {
			int bi = in.read();
			if (bi == -1) throw new RuntimeException("Unexpected end of stream.");
			byte b = (byte) bi;
			if (customIonizables.containsKey(b)) {
				Ionizable ion = ((Ionizable) customIonizables.get(b).newInstance());
				ion.ionRead(in);
				return ion;
			}

			switch (b) {
				case IonMarks.BOOLEAN:
					return StreamUtils.readBoolean(in);
				case IonMarks.BYTE:
					return StreamUtils.readByte(in);
				case IonMarks.CHAR:
					return StreamUtils.readChar(in);
				case IonMarks.SHORT:
					return StreamUtils.readShort(in);
				case IonMarks.INT:
					return StreamUtils.readInt(in);
				case IonMarks.LONG:
					return StreamUtils.readLong(in);
				case IonMarks.FLOAT:
					return StreamUtils.readFloat(in);
				case IonMarks.DOUBLE:
					return StreamUtils.readDouble(in);
				case IonMarks.STRING:
					String s = StreamUtils.readString(in);
					return s;
				default:
					throw new RuntimeException("Invalid Ion mark " + Integer.toHexString(bi));
			}

		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}


	/**
	 * Write single ionizable or primitive object to output stream
	 * 
	 * @param out output stream
	 * @param obj stored object
	 */
	public static void writeObject(OutputStream out, Object obj) {
		try {
			if (obj instanceof Ionizable) {
				out.write(((Ionizable) obj).ionMark());
				((Ionizable) obj).ionWrite(out);
				return;
			}

			if (obj instanceof Boolean) {
				out.write(IonMarks.BOOLEAN);
				StreamUtils.writeBoolean(out, (Boolean) obj);
				return;
			}

			if (obj instanceof Byte) {
				out.write(IonMarks.BYTE);
				StreamUtils.writeByte(out, (Byte) obj);
				return;
			}

			if (obj instanceof Character) {
				out.write(IonMarks.CHAR);
				StreamUtils.writeChar(out, (Character) obj);
				return;
			}

			if (obj instanceof Short) {
				out.write(IonMarks.SHORT);
				StreamUtils.writeShort(out, (Short) obj);
				return;
			}

			if (obj instanceof Integer) {
				out.write(IonMarks.INT);
				StreamUtils.writeInt(out, (Integer) obj);
				return;
			}

			if (obj instanceof Long) {
				out.write(IonMarks.LONG);
				StreamUtils.writeLong(out, (Long) obj);
				return;
			}

			if (obj instanceof Float) {
				out.write(IonMarks.FLOAT);
				StreamUtils.writeFloat(out, (Float) obj);
				return;
			}

			if (obj instanceof Double) {
				out.write(IonMarks.DOUBLE);
				StreamUtils.writeDouble(out, (Double) obj);
				return;
			}

			if (obj instanceof String) {
				out.write(IonMarks.STRING);
				StreamUtils.writeString(out, (String) obj);
				return;
			}

			throw new RuntimeException(Calc.className(obj) + " can't be stored to Ion storage.");

		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}


}
