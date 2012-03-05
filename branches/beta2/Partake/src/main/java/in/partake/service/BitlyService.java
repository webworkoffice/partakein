package in.partake.service;

import in.partake.resource.PartakeProperties;

import org.apache.log4j.Logger;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.BitlyException;
import com.rosaloves.bitlyj.ShortenedUrl;

public class BitlyService {
    private static final Logger logger = Logger.getLogger(BitlyService.class);
    
    /**
     * URL を bitly で短縮する。
     *
     * @param sourceURL
     * @return
     */
    public static String callBitlyShortenURL(String sourceURL) throws BitlyException {
        if (sourceURL.startsWith("http://localhost") || sourceURL.startsWith("http://127.0.0.1")) {  
            // bitly API may throw Exception if its argument means localhost
            logger.debug(String.format("avoid shortening URL(%s)", sourceURL));
            return sourceURL;
        }
        final String bitlyUserName = PartakeProperties.get().getBitlyUserName();
        final String bitlyAPIKey = PartakeProperties.get().getBitlyAPIKey();
        final Provider bitly = Bitly.as(bitlyUserName, bitlyAPIKey);

        ShortenedUrl bUrl = bitly.call(Bitly.shorten(sourceURL));
        return bUrl.getShortUrl().toString();
    }

}
