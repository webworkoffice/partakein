package in.partake.view.impl;

import in.partake.base.Util;
import in.partake.service.IViewInitializer;
import in.partake.view.util.Helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class ViewInitializerImpl implements IViewInitializer {
    private static final Logger logger = Logger.getLogger(ViewInitializerImpl.class);

    private static final String CSS_ENCODE = "UTF-8";
    /** 結合すべきCSSファイルと結合順序を記録したリスト。 */
    private static final List<String> COMPOSITE_TARGETS = Arrays.asList(new String[]{
            "bootstrap.css", "bootstrap.fix.css", "partake.css"
    });
    /** IEにのみ適用すべきCSSファイル。結合すべきでない。 */
    private static final String FIXIE_FILE_NAME = "fixie.css";
    private static final String OUTPUT_FILE_NAME = "style.css";

    @Override
    public void initialize(String viewPath) throws Exception {
        checkUnknownCssFiles(viewPath);
        initializeCssVersion(viewPath);
        compositeCssFiles(viewPath);
    }

    private void checkUnknownCssFiles(String cssPath) {
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

    private void initializeCssVersion(String cssPath) {
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
    private void compositeCssFiles(String cssPath) throws IOException {
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
                Util.writeFromFile(writer, inFile, CSS_ENCODE);
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

    private static final class CssFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".css") && !name.equals(OUTPUT_FILE_NAME);
        }
    }

}
