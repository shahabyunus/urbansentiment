package org.urbanstmt.twitter.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.urbanstmt.stream.handlers.UsStatusStreamHandler;

import twitter4j.Logger;
import twitter4j.StatusListener;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.Location.Coordinate;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;

public class TweetCaptureEngine {
	private final StatusListener listener = new UsStatusStreamHandler();
	private static final Logger LOG = Logger
			.getLogger(TweetCaptureEngine.class);
	private static final String CONFIG_FILE = "twitter.properties";
	private static final Properties p = new Properties();

	public TweetCaptureEngine() {
		super();
		InputStream stream = TweetCaptureEngine.class.getClassLoader()
				.getResourceAsStream(CONFIG_FILE);
		try {
			p.load(stream);
			if (stream == null) {
				throw new IllegalStateException("Config file " + CONFIG_FILE
						+ " is missing.");
			}
		} catch (IOException e) {
			throw new IllegalStateException("Error reading config file "
					+ CONFIG_FILE);
		}

	}

	public static void main(String[] arg) throws InterruptedException {
		new TweetCaptureEngine().setupAndStart();
	}

	private void setupAndStart() throws InterruptedException {

		String terms[] = null;
		String termsStr = (String) p.get("terms");
		if (!Strings.isNullOrEmpty(termsStr)) {
			terms = termsStr.split(",");
		} else {
			terms = new String[] {};
		}

		float[][] rawLocations = null;
		String coordinatesStr = (String) p.get("coords");
		if (!Strings.isNullOrEmpty(coordinatesStr)) {

			String[] coordinates = coordinatesStr.split(",");
			try {
				rawLocations = new float[1][4];
				rawLocations[0][0] = Float.parseFloat(coordinates[0]);
				rawLocations[0][1] = Float.parseFloat(coordinates[1]);
				rawLocations[0][2] = Float.parseFloat(coordinates[2]);
				rawLocations[0][3] = Float.parseFloat(coordinates[3]);
			} catch (NumberFormatException ex) {
				rawLocations = null;
			}
		}

		if (rawLocations == null) {
			rawLocations = new float[1][4];
			rawLocations[0][0] = 71.07f;
			rawLocations[0][1] = 42.37f;
			rawLocations[0][2] = 71.13f;
			rawLocations[0][3] = 42.42f;
		}

		setupAndStart(terms, rawLocations);
	}

	private void setupAndStart(String[] terms, float[][] rawLocations)
			throws InterruptedException {
		/**
		 * Set up your blocking queues: Be sure to size these properly based on
		 * expected TPS of your stream
		 */

		Integer mQSz = Integer.parseInt((String) p.get("message.queue.size"));
		if (mQSz == null || mQSz < 1) {
			mQSz = 1000000;
		}

		Integer eQSz = Integer.parseInt((String) p.get("event.queue.size"));
		if (eQSz == null || eQSz < 1) {
			eQSz = 1000;
		}
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(mQSz);
		BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(eQSz);

		/**
		 * Declare the host you want to connect to, the endpoint, and
		 * authentication (basic auth or oauth)
		 */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

		if (terms == null || terms.length < 1) {
			/*
			 * TODO Right now no need ot make this compulsory LOG.error(
			 * "No terms specify. A comma separate list of terms must be provided."
			 * ); throw new IllegalStateException(
			 * "No terms specify. A comma separate list of terms must be provided."
			 * );
			 */
			
			LOG.info("No terms specified.");
		} else {

			List<String> termsList = Lists.newArrayList(terms);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Terms specified=" + termsList);
			}
			hosebirdEndpoint.trackTerms(termsList);
		}

		if (rawLocations != null && rawLocations.length > 0) {

			List<Location> locationLists = new ArrayList<Location>();
			for (float[] loc : rawLocations) {
				if (loc == null || loc.length < 4) {
					LOG.warn("Skipping invalid location specification. Not sufficient coordinates="
							+ loc);
				} else {
					Location location = new Location(new Coordinate(loc[0],
							loc[1]), new Coordinate(loc[2], loc[3]));
					locationLists.add(location);
				}
			}
			
			//TODO
			LOG.info("Locations specified=" + locationLists);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Locations specified=" + locationLists);
			}

			hosebirdEndpoint.locations(locationLists);
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No location specified");
			}
		}

		// These secrets should be read from a config file
		String consumerKey = (String) p.get("consumer.key");
		String consumerSecret = (String) p.get("consumer.secret");
		String accessToken = (String) p.get("access.token");
		String accessTokenSecret = (String) p.get("access.token.secret");

		Authentication hosebirdAuth = new OAuth1(consumerKey.trim(),
				consumerSecret.trim(), accessToken.trim(),
				accessTokenSecret.trim());

		ClientBuilder builder = new ClientBuilder()
				.name("user:shahab yunus")
				// optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth)
				.endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue))
				.eventMessageQueue(eventQueue); // optional: use this if you
												// want to process client events

		Client hosebirdClient = builder.build();

		// Create an executor service which will spawn threads to do the actual
		// work of parsing the incoming messages and
		// calling the listeners on each message
		Integer th = Integer.parseInt((String) p.get("number.capture.threads"));
		if (th == null || th < 1) {
			th = 10;
		}
		int numProcessingThreads = th;
		ExecutorService executorService = Executors
				.newFixedThreadPool(numProcessingThreads);

		// client is our Client object
		// msgQueue is our BlockingQueue<String> of messages that the handlers
		// will receive from
		// listeners is a List<StatusListener> of the t4j StatusListeners
		// executorService
		Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(
				hosebirdClient, msgQueue, Lists.newArrayList(listener),
				executorService);
		t4jClient.connect();
		System.out.println("connected to the client.");
		LOG.info("connected to the client.");
		// Call this once for every thread you want to spin off for processing
		// the raw messages.
		// This should be called at least once.

		for (int t = 0; t < numProcessingThreads; t++) {
			t4jClient.process();
		}

		// hosebirdClient.stop();
		LOG.info("now I am done.");

	}

}
