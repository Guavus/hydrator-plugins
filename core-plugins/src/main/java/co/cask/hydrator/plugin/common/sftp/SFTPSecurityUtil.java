package co.cask.hydrator.plugin.common.sftp;

import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class SFTPSecurityUtil {
    private static Logger logger = LoggerFactory.getLogger(SFTPSecurityUtil.class);

    /**
     * Overriding this method so that we can write our custom code to extract host and port information from URI.
     * Parent class method fails to do the same if the password contains special characters. e.g. "guavus@123".
     *
     * @param host
     * @param port
     * @return
     */
    public static String buildDTServiceName(String host, int port) {
        logger.info("Using custom implementation of `buildDTServiceName` instead of using same method from `SecurityUtil`. Using host: {} and port: {}.", host, port);
        InetSocketAddress addr = NetUtils.createSocketAddrForHost(host, port);
        return SecurityUtil.buildTokenService(addr).toString();
    }
}
