package com.server;

import spark.Response;
import static spark.Spark.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Main Server Class for the Line Server API - Server is based on Spark Framework.
 */
public class Server {
	static LineServiceController lsObj = null;
	static Preprocessor preProcessObj = new Preprocessor();

	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

	public static void main(String[] args) {
		String filePath = args[0];

		// Pre-processing Input File
		try {
			preProcessObj.preProcessInputFileMapping(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOGGER.info("Starting Server..");
		ObjectMapper mapper = new ObjectMapper();

		// Set New Port Number if provided in arguments. This is Optional. Default Port
		// is: 4567
		if (args.length > 1) {
			port(Integer.valueOf(args[1]));
		}

		lsObj = new LineServiceController();
		LOGGER.info("Open localhost:4567/lines/5 to access the API");

		/*
		 * HTTP GET Request: get("/lines/<Line Number>") Get request to fetch the
		 * specific line with line number from Line-Server Sample GET Request:
		 * "http://localhost:4567/lines/2". It return a JSON in response.
		 */
		try {
			get("/lines/:line", (req, res) -> {
				Long lineNum = Long.valueOf(req.params("line"));
				String lineText = getLineFromFile(lineNum, res, filePath);
				Line line = new Line(lineNum, lineText.trim());
				String out = mapper.writeValueAsString(line);
				return out;
			});
		} 
		catch (Exception e) {
			lsObj.lsDAO.db.close();
		}
	}

	/*
	 * Checks if lineNumber is out of file bounds- returns with Status:413. If line
	 * number is within bounds then call LineServiceController to service the line
	 * Number.
	 */
	public static String getLineFromFile(long line, Response res, String filePath) {
		// If lineNum is out of bounds return with Error code.
		if (line <= 0 || line >= Constants.maxLineNumber * Constants.CHUNK_SIZE) {
			res = setFailureStatus(res, line);
			return Constants.STR_EMPTY_STRING;
		}

		return lsObj.getLineFromLineNumber(line, filePath);
	}

	/*
	 * Method is used to log Failures and Set Failure error Code and Message.
	 */
	public static Response setFailureStatus(Response res, long line) {
		long totalLines = Constants.maxLineNumber * Constants.CHUNK_SIZE;
		LOGGER.log(Level.FINE, "Failure: Line number received is greater than total lines. " + "Line number received: "
				+ line + " , Total Lines: " + totalLines);
		res.status(Constants.FAILURE_CODE);
		res.body(Constants.STR_LINE_OUT_OF_FILE_MESSAGE);
		return res;
	}
}