package net.sector.util;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.sector.Constants;


/**
 * Sector static logger class.
 * 
 * @author Ondřej Hruška (MightyPork)
 * @copy (c) 2012
 */
public class Log {
	/**
	 * Global PowerCraft's logger.
	 */
	private static final Logger logger = Logger.getLogger("Sector");
	private static final Logger loggerError = Logger.getLogger("SectorErr");
	/** Logging enabled */
	public static boolean loggingEnabled = true;
	/** Stdout printing enabled */
	public static boolean printToStdout = false;

	static {
		try {
			FileHandler handler1 = new FileHandler(Utils.getGameSubfolder(Constants.FILE_LOG).getPath());
			handler1.setFormatter(new LogFormatter(true));
			logger.addHandler(handler1);

			FileHandler handler2 = new FileHandler(Utils.getGameSubfolder(Constants.FILE_LOG_E).getPath());
			handler2.setFormatter(new LogFormatter(false));
			loggerError.addHandler(handler2);

			loggingEnabled = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);
		logger.info("Main logger initialized.");
		logger.info((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));

		loggerError.setUseParentHandlers(false);
		loggerError.setLevel(Level.ALL);
		loggerError.info("Error logger initialized.");
		loggerError.info((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));

		org.newdawn.slick.util.Log.setVerbose(false);
	}

	/**
	 * Enable logging.
	 * 
	 * @param flag do enable logging
	 */
	public static void enable(boolean flag) {
		loggingEnabled = flag;
	}



	/**
	 * Enable debug mode - log also printed to stdout.
	 * 
	 * @param printToStdout
	 */
	public static void setPrintToStdout(boolean printToStdout) {
		Log.printToStdout = printToStdout;
	}

	/**
	 * Log INFO message
	 * 
	 * @param msg message
	 */
	private static void info(String msg) {
		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.INFO, msg);
	}

	/**
	 * Log FINE (important) message
	 * 
	 * @param msg message
	 */
	private static void fine(String msg) {
		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.FINE, msg);
	}

	/**
	 * Log FINER (loss important) message
	 * 
	 * @param msg message
	 */
	private static void finer(String msg) {
		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.FINER, msg);
	}

	/**
	 * Log FINEST (least important) message
	 * 
	 * @param msg message
	 */
	private static void finest(String msg) {
		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.FINEST, msg);
	}

	/**
	 * Log WARNING message
	 * 
	 * @param msg message
	 */
	private static void warning(String msg) {
		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.WARNING, msg);
		loggerError.log(Level.WARNING, msg);
	}

	/**
	 * Log FINE message
	 * 
	 * @param msg message
	 */
	public static void f1(String msg) {
		fine(msg);
	}

	/**
	 * Log FINER message
	 * 
	 * @param msg message
	 */
	public static void f2(String msg) {
		finer(msg);
	}

	/**
	 * Log FINEST message
	 * 
	 * @param msg message
	 */
	public static void f3(String msg) {
		finest(msg);
	}

	/**
	 * Log INFO message
	 * 
	 * @param msg message
	 */
	public static void i(String msg) {
		info(msg);
	}


	/**
	 * Log WARNING message (less severe than ERROR)
	 * 
	 * @param msg message
	 */
	public static void w(String msg) {
		warning(msg);
	}

	/**
	 * Log SEVERE (critical warning) message
	 * 
	 * @param msg message
	 */
	private static void severe(String msg) {
		loggerError.log(Level.SEVERE, msg);
		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.SEVERE, msg);
	}

	/**
	 * Log ERROR message
	 * 
	 * @param msg message
	 */
	public static void e(String msg) {
		severe(msg);
	}

	/**
	 * Log THROWING message
	 * 
	 * @param msg message
	 * @param thrown thrown exception
	 */
	public static void e(String msg, Throwable thrown) {
		loggerError.log(Level.SEVERE, msg + "\n" + getStackTrace(thrown));
		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.SEVERE, msg + "\n" + getStackTrace(thrown));
	}

	/**
	 * Log exception thrown
	 * 
	 * @param thrown thrown exception
	 */
	public static void e(Throwable thrown) {
		loggerError.log(Level.SEVERE, getStackTrace(thrown));
		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.SEVERE, getStackTrace(thrown));
	}

	/**
	 * Get stack trace from throwable
	 * 
	 * @param t
	 * @return trace
	 */
	private static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}


	/**
	 * PowerCraft Log file formatter.
	 * 
	 * @author Ondřej Hruška (MightyPork)
	 * @copy (c) 2012
	 */
	private static class LogFormatter extends Formatter {

		/** Newline string constant */
		private static final String nl = System.getProperty("line.separator");
		private boolean sysout;

		public LogFormatter(boolean sysout) {
			this.sysout = sysout;
		}

		@Override
		public String format(LogRecord record) {

			StringBuffer buf = new StringBuffer(180);

			if (record.getMessage().equals("\n")) {
				return nl;
			}

			if (record.getMessage().charAt(0) == '\n') {
				buf.append(nl);
				record.setMessage(record.getMessage().substring(1));
			}

			Level level = record.getLevel();
			String trail = "[ ? ]";
			if (level == Level.FINE) {
				trail = "[ # ] ";
			}
			if (level == Level.FINER) {
				trail = "[ - ] ";
			}
			if (level == Level.FINEST) {
				trail = "[   ] ";
			}
			if (level == Level.INFO) {
				trail = "[ i ] ";
			}
			if (level == Level.SEVERE) {
				trail = "[!E!] ";
			}
			if (level == Level.WARNING) {
				trail = "[!W!] ";
			}

			record.setMessage(record.getMessage().replaceAll("\n", nl + trail));

			buf.append(trail);
			buf.append(formatMessage(record));

			buf.append(nl);

			Throwable throwable = record.getThrown();
			if (throwable != null) {

				buf.append("at ");
				buf.append(record.getSourceClassName());
				buf.append('.');
				buf.append(record.getSourceMethodName());
				buf.append(nl);

				StringWriter sink = new StringWriter();
				throwable.printStackTrace(new PrintWriter(sink, true));
				buf.append(sink.toString());

				buf.append(nl);
			}

			if (Log.printToStdout && sysout) {
				if (level == Level.FINE || level == Level.FINER || level == Level.FINEST || level == Level.INFO) {
					System.out.print(buf.toString());
				} else if (level == Level.SEVERE || level == Level.WARNING) {
					System.err.print(buf.toString());
				}
			}

			return buf.toString();
		}
	}


}
