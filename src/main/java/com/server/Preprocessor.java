package com.server;

import java.io.*;
import java.nio.channels.*;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import java.nio.*;

/*
 * This Preprocessor Class has to be run independently prior to starting the Server.
 * In pre-processing, goal is to store each line number with its byte offset from beginning of the file.
 * So at any time, any line contents can be accessed just by looking up the byte offset for that line.
 * I have used mapDB with line numbers as "Key" and corresponding offset as "Value". Using mapDB a database model can be generated 
 * and stored on disk. And can be easily loaded while running the server.
 */

public class Preprocessor {
	private static final Logger LOGGER = Logger.getLogger(Preprocessor.class.getName());
	DB db = null;

	public void preProcessInputFileMapping(String ipFile) throws IOException {

		LOGGER.info("Entering Preprocessing.");
		long startTime = System.nanoTime();
		RandomAccessFile raFile = null;
		FileChannel fileChannel = null;
		MappedByteBuffer byteBuffer = null;
		File f = new File(ipFile);
		db = DBMaker.fileDB(Constants.STR_DB_PATH).fileMmapEnable().make();
		@SuppressWarnings("unchecked")
		ConcurrentMap<Long, Long> map = (ConcurrentMap<Long, Long>) db.hashMap(Constants.STR_BYTE_LINE_MAP_NAME)
				.createOrOpen();

		if (map.size() > 0) {
			LOGGER.log(Level.INFO, "DB Model found, Load existing db model in  memory.");
			db.close();
			return;
		}
		try {
			LOGGER.log(Level.INFO, "Preprocessing Started.");
			raFile = new RandomAccessFile(f, "r");
			fileChannel = raFile.getChannel();

			long lineInd = 1L;
			long offset = 0L;
			long bufferSize = 131072L;

			map.put(1L, 0L);
			while ((fileChannel.size() - offset) > 0) {
				bufferSize = ((fileChannel.size() - offset) < (bufferSize)) ? (fileChannel.size() - offset)
						: bufferSize;

				byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, bufferSize);
				while (byteBuffer.hasRemaining()) {
					offset++;

					if (byteBuffer.get() == Constants.NEW_LINE_CHAR) {
						lineInd++;

						if (lineInd > 1 && (lineInd % Constants.CHUNK_SIZE) == 0) {
							map.put(lineInd, offset);
						}
					}
				}
				byteBuffer.clear();
				fileChannel.force(true);
			}
			LOGGER.log(Level.INFO, "Preprocessing Completed.");
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			raFile.close();
			fileChannel.close();
			db.close();
			LOGGER.log(Level.INFO,
					"Total time taken in Pre-processed File [" + (System.nanoTime() - startTime) / 1000000 + "] ms");
		}
	}
}
