package net.urosk.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jernej on 5/25/15.
 */
public class SBRSClientProperties {

    private Properties props;

    public SBRSClientProperties() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("client.properties");
        this.props = new Properties();
        this.props.load(inputStream);
        inputStream.close();
    }

    public String getValue(String key) {
        return props.getProperty(key);
    }

}
