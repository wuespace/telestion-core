package org.telestion.safer.mavlink;

import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * This class is meant to save the received {@link org.telestion.core.message.Position Position} json-encoded objects to the backup file. The format of the backup is also a json-object, containing a json-array where the received messages from the event-bus are saved.
 *
 * @version 1.0
 * @author Matei Oana
 */

public class FileHandler {

    private static String fileName = "backup";
    private static final String folder = "backup\\";
    private File myFile;
    private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);
    private String existingContent = "";
    private final DateTimeFormatter dateAndTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * The constructor checks if the backup folder in the project root directory already exists and if not, it creates one. Further to this, it creates the backup.json file where the data will be stored.
     */
    public FileHandler(){
        this.myFile = new File(folder + fileName + ".json");

        try {
            Path currentRelativePath = Paths.get("");
            Path path = Paths.get(currentRelativePath.toAbsolutePath().toString() + "\\backup");

            Files.createDirectories(path);
            System.out.println("Directory is created!");
        } catch (IOException e) {
            System.err.println("Failed to create directory!" + e.getMessage());
        }
        if (!myFile.isFile()){
            try {
                if (myFile.createNewFile()){
                    logger.info("Output file " + myFile.getName() + " was created successfully");
                }
                else {
                    logger.info("Failed to create file " + fileName);
                }
            } catch (IOException e){
                logger.info("An error occurred.");
            }
        }
        else {
            logger.info("File " + fileName + " already exists");
            checkContent();
        }
    }

    /**
     * This method checks the content inside a existing backup file to ensure that already stored data doesn't get lost. If there is any content, the isJSONValid() method will be called to ensure that the existing content is not corrupted.
     * If corrupted content is being discovered, a new file with the name format: {backup_file_name}_{version_number}.json will be created for further backups.
     */
    public void checkContent(){
        try {
            StringBuilder content = new StringBuilder();
            Scanner myScanner = new Scanner(myFile);

            while(myScanner.hasNextLine()){
                String lineFromFile = myScanner.nextLine();
                content.append(lineFromFile);
            }
            if (isJSONValid(content.toString()) || content.length() == 0) {
                existingContent = content.toString();
            }
            else {
                logger.error("File " + fileName + ".json has corrupted content! A new backup file will be created!");
                String version = fileName.substring(fileName.length() - 1);
                if (StringUtils.isNumeric(version)){
                    int versionNumber = Integer.parseInt(version) + 1;
                    fileName = fileName + "_" + versionNumber;
                }
                else{
                    fileName = fileName + "_1";
                }
                myFile = new File(fileName + ".json");

            }
            myScanner.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This method returns a boolean, whether the parsed string is a valid Json Object or not.
     * @param test the string that should be checked.
     * @return boolean with the validity of the string
     */
    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    /**
     * The save method is the core of this class, creating the output json-object to be stored in the backup file.
     * The existing saved messages are handled using the methods provided by JSONsimple library and the received message to-be-saved is integrated in the {@link JSONArray JSONArray}.
     * To keep track of the exact time when each message was received, the Messages are stored in another {@link JSONObject JSONObject} inside the {@link JSONArray JSONArray} and the key is represented by the current date and time in the following format: "dd-MM-yyyy HH:mm:ss".
     * @param message is the message from the event-bus that has to be added to the backup file.
     *                TODO: the method has to be modified to add any type of messages to the backup, creating a different array for every new type.
     */
    public void save(String message){
        if (!existingContent.isEmpty()){
            try {
                JSONObject dataFromFile = new JSONObject(existingContent);
                JSONObject newPositionFromMessage = new JSONObject(message);
                JSONArray savedPositions = dataFromFile.getJSONArray("positions");
                JSONObject wrapper = new JSONObject();
                wrapper.put(LocalDateTime.now().format(dateAndTime), newPositionFromMessage);
                savedPositions.put(wrapper);
                dataFromFile.put("positions", savedPositions);
                FileWriter myWriter = new FileWriter(folder + fileName + ".json");

                myWriter.write(dataFromFile.toString());
                existingContent = dataFromFile.toString();
                myWriter.close();
                logger.info("File not empty -- data added successfully");
            } catch (IOException e){
                e.printStackTrace();
                logger.info("File not empty -- failed to write to the file");
            }
        }
        else{
            try {
                JSONObject firstObject = new JSONObject(message);
                FileWriter myWriter = new FileWriter(folder + fileName + ".json");
                JSONObject messageContainer = new JSONObject();
                JSONArray positions = new JSONArray();
                JSONObject newPosition = new JSONObject();

                newPosition.put(LocalDateTime.now().format(dateAndTime), firstObject); // creem obiectul {timestamp:{posObj}}
                positions.put(newPosition);
                messageContainer.put("positions", positions);
                existingContent = messageContainer.toString();
                myWriter.write(messageContainer.toString());
                myWriter.close();
                logger.info("file was empty -- data added successfully");
            } catch (IOException e){
                e.printStackTrace();
                logger.info("file was empty -- failed to write to the file");
            }
        }
    }
}