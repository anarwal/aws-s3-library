package edu.common.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public interface IFileStorageService {

    /**
     * Create or update the Provided {@link File} within file based storage
     * @param key String unique key value
     * @param file {@link File} content to be stored
     */
    void put(String key, File file);

    /**
     * Create or update the provided content within file based storage
     * @param key String unique key value
     * @param content byte[] content to be stored
     * @param contentType content type of the content to be stored
     */
    void put(String key, byte[] content, String contentType);

    /**
     * Gets the content from file based storage by key.
     * @param key String unique key value
     * @return byte[] content being returned
     */
    byte[] get(String key) throws IOException, FileNotFoundException;

    /**
     * Gets the last modified date of file based storage by key.
     * @param key String unique key value
     * @return Date will be returned
     */
    Date getDate(String key) throws IOException, FileNotFoundException, ParseException;

    /**
     * Deletes the content by key
     * @param key String unique key value
     */
    void delete(String key);

}
