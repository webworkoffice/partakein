package in.partake.servlet.listener;

import in.partake.daemon.TwitterDaemon;
import in.partake.service.PartakeService;
import in.partake.view.util.Helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


public class InitializationListener implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(InitializationListener.class);
	private static final String CSS_ENCODE = "UTF-8";
	private static final String CSS_DIR_NAME = "css";
	/** 結合すべきCSSファイルと結合順序を記録したリスト。 */
	private static final List<String> COMPOSITE_TARGETS = Arrays.asList(new String[]{
	        // "layout.css", "color.css", "font.css", "openid.css", "print.css",
	        "bootstrap.min.css", "bootstrap.fix.css"
	});
	/** IEにのみ適用すべきCSSファイル。結合すべきでない。 */
	private static final String FIXIE_FILE_NAME = "fixie.css";
	private static final String OUTPUT_FILE_NAME = "style.css";

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		TwitterDaemon.getInstance().schedule();
		checkUnknownCssFiles(arg0);
		initializeCssVersion(arg0);
		PartakeService.initialize();
		try {
			compositeCssFiles(arg0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void checkUnknownCssFiles(ServletContextEvent event) {
		String cssPath = event.getServletContext().getRealPath(CSS_DIR_NAME);
		File cssDir = new File(cssPath);
		if (!cssDir.exists()) {
			logger.warn("css directory doesn't exist.");
			return;
		}

		for (String cssFileName : cssDir.list(new CssFileFilter())) {
			if (!COMPOSITE_TARGETS.contains(cssFileName) && !cssFileName.equals(FIXIE_FILE_NAME)) {
				// TODO translate log message
				logger.warn("新しくCSSファイル(" + cssFileName + ")を追加した？それならこのクラスのCOMPOSITE_TARGETSも書き換えないと反映されないよ！");
			}
		}
	}

	private void initializeCssVersion(ServletContextEvent event) {
		String cssPath = event.getServletContext().getRealPath(CSS_DIR_NAME);
		File cssDir = new File(cssPath);
		if (!cssDir.exists()) {
			logger.warn("css directory doesn't exist.");
			return;
		}

		long version = 0L;
		for (File cssFile : cssDir.listFiles(new CssFileFilter())) {
			version ^= cssFile.lastModified();
		}
		Helper.setCssVersion(Long.toString(version));
	};

	/**
	 * composite css files to avoid issue 45.
	 * 
	 * @throws IOException
	 * @see http://code.google.com/p/partakein/issues/detail?id=45
	 * @author skypencil (@eller86)
	 */
	private void compositeCssFiles(ServletContextEvent event) throws IOException {
		String cssPath = event.getServletContext().getRealPath(CSS_DIR_NAME);
		File cssDir = new File(cssPath);
		if (!cssDir.exists()) {
			logger.warn("css directory doesn't exist.");
			return;
		}

		File outFile = new File(cssDir, OUTPUT_FILE_NAME);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), CSS_ENCODE));
		try {
			writer.write("@charset \"" + CSS_ENCODE + "\";");
			writer.newLine();

			for (String inFileName : COMPOSITE_TARGETS){
				File inFile = new File(cssDir, inFileName);
				if (!inFile.exists() || !inFile.canRead()) {
					logger.warn(inFileName + " is (not found | cannot read).");
					continue;
				}
				writeFromFile(writer, inFile);
			}

			writer.newLine();
			writer.write("/* IE reads non-doublequoted files */");
			writer.newLine();
			writer.write("@import " + FIXIE_FILE_NAME + " ;");
			writer.newLine();
		} catch (IOException e) {
			logger.error("IOException occured.", e);
			throw e;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				logger.error("IOException occured.", e);
				throw e;
			}
		}
	}

	private void writeFromFile(BufferedWriter writer, File inFile) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), CSS_ENCODE));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("@charset") || line.isEmpty()) continue;
				writer.write(line);
				writer.newLine();
			}
		} finally {
			try {
				reader.close();
			} catch (IOException ignore) {
				logger.warn("Reader#close throw IOException, but it's ignored.", ignore);
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			TwitterDaemon.getInstance().cancel();
		} catch (Throwable ignore) {
			// catch and ignore for shutdown other daemons.
			logger.warn("Unintentional exception is thrown.", ignore);
		}
	}

	private static final class CssFileFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".css") && !name.equals(OUTPUT_FILE_NAME);
		}
	}
}
