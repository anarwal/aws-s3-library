package edu.common.service.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class FileUtility {

    private FileUtility() {}

    /**
     * Access and read the contents of a file and return the contents as a string value.
     * @param fileName String
     * @return String
     * @throws Exception when {@link IOException} is encountered
     */
    public static String getFileContent(String fileName) throws Exception {
        return getFileContent(fileName, false);
    }

    /**
     * Access and read the contents of a file and return the contents as a string value.
     * @param fileName String
     * @param trimAllWhitespace boolean. If true, all white space is trimmed.
     * @return String
     * @throws Exception when {@link IOException} is encountered
     */
    public static String getFileContent(String fileName, boolean trimAllWhitespace) throws Exception {
        String content;
        try {
            ClassLoader classLoader = FileUtility.class.getClassLoader();
            content = IOUtils.toString(classLoader.getResourceAsStream(fileName), Charset.defaultCharset());
        } catch (IOException e) {
            throw new Exception("Unable to load the provided file (" + fileName + ")");
        }

        if(trimAllWhitespace){
            content = org.springframework.util.StringUtils.trimAllWhitespace(content);
        }

        return content;
    }

    /**
     * Read the contents of a file and return the contents as a string value.
     * @param file {@link File}
     * @return String
     * @throws Exception when unable to obtain content from specified file
     */
    public static String getContentFromFile(File file) throws Exception {
        String content;
        try {
            content = org.apache.commons.io.FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (Exception e) {
            throw new Exception("Unable to load the provided file (" + file.getCanonicalPath() + ")");
        }
        if (StringUtils.isBlank(content)) {
            throw new Exception("The provided file (" + file.getCanonicalPath() + ") is empty.");
        } else {
            return content;
        }
    }

    /**
     * Access the specified file and attempt to return the contents as a {@link Properties} file.
     * @param fileName String
     * @return {@link Properties}
     * @throws Exception when unable to obtain content from specified file
     */
    public static Properties getProperties(String fileName) throws Exception {
        Properties properties;
        try {
            properties = new Properties();
            ClassLoader classLoader = FileUtility.class.getClassLoader();
            InputStream resourceAsStream = classLoader.getResourceAsStream(fileName);
            properties.load(resourceAsStream);
        } catch (Exception e) {
            throw new Exception("Unable to load the provided properties file (" + fileName + ")");
        }
        return properties;
    }

    /**
     * Attempt to access the specified {@link Properties} file and retrieve the value of the provided key.
     * @param fileName String
     * @param key String
     * @return String
     * @throws Exception when unable to obtain content from specified file
     */
    public static String getPropertyValue(String fileName, String key) throws Exception {
        return getProperties(fileName).getProperty(key);
    }

}
