package com.server;

import java.io.File;
import java.io.IOException;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap.Builder;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * LineServiceController handles all the processing for fetching a line from a line number.
 * Server calls "getLineFromLineNumber" method in this class for each request. If data is in cache, contents of the line are returned from cache. 
 * If data is not in cache, then it gets a RandomAccessFile Read Handle on file. Exact byte offset for a particular line can be calculated in following way:
 * 			offset_inside_chunk = (lineNum < Constants.CHUNK_SIZE) ? (lineNum - 1L) : (lineNum % Constants.CHUNK_SIZE)
 * 			offset = lineNum - offset_inside_chunk;
 * Here, offset_inside_chunk is the internal offset of the line within a particular chunk and offset is the offset of the chunk.
 * Using RandomAcessFile Read Handle, we read the file starting from offset to the first '\n' character encountered. 
 * This data is converted in to string and then returned in JSON String form as response. LRU Cache is also updated with this record.
 */

public class LineServiceController {
	LineServiceDAO lsDAO = null;
	ConcurrentMap<Long, Long> linesMap = null;
	ConcurrentLinkedHashMap<Long, String> cache = null;
	private static final Logger LOGGER = Logger.getLogger(LineServiceController.class.getName());

	public LineServiceController() {
		lsDAO = new LineServiceDAO();
		linesMap = lsDAO.initDB(Constants.STR_DB_PATH);
		cache = new Builder<Long, String>().maximumWeightedCapacity(500).build();
	}

	public LineServiceController(LineServiceDAO lsDAO) {
		this.lsDAO = lsDAO;
		cache = new Builder<Long, String>().maximumWeightedCapacity(500).build();
	}

	public String getLineFromLineNumber(long lineNum, String filePath) {
		// Logic to Random Access file and line
		String fileLineData = null;

		RandomAccessFile randomFile = null;
		FileChannel fileChannel = null;
		ByteBuffer byteBuffer = null;

		byte lineDataByte;
		StringBuilder reqLineData = new StringBuilder();

		LOGGER.log(Level.FINEST, "Line fetch started for line number " + lineNum);

		if (cache != null && cache.containsKey(lineNum)) {
			LOGGER.log(Level.FINEST, "Line found in cache for line number " + lineNum);
			return cache.get(lineNum);
		}

		try {
			randomFile = new RandomAccessFile(new File(filePath), "r");
			long counter = (lineNum < Constants.CHUNK_SIZE) ? (lineNum - 1L) : (lineNum % Constants.CHUNK_SIZE);
			long idx = lineNum - counter;

			if (linesMap.get(idx) != null) {
				randomFile.seek(linesMap.get(idx));
				fileChannel = randomFile.getChannel();
				byteBuffer = ByteBuffer.allocateDirect(8192);

				while ((counter >= 0) && (fileChannel.read(byteBuffer) > 0)) {
					byteBuffer.flip();

					while (byteBuffer.hasRemaining()) {
						lineDataByte = byteBuffer.get();

						if (lineDataByte == Constants.NEW_LINE_CHAR) {
							counter--;
						} else if (counter == 0) {
							reqLineData.append((char) lineDataByte);
						}
					}
					byteBuffer.clear();
				}

				fileChannel.close();
			}

			randomFile.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		fileLineData = reqLineData.toString();
		LOGGER.log(Level.FINEST, "Success: Line fetch Completed for line number " + lineNum);

		// Put Data in to Cache
		putDataInCache(lineNum, fileLineData);
		return fileLineData;
	}

	public void putDataInCache(Long key, String data) {
		this.cache.put(key, data);
	}
}
