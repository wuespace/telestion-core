package org.telestion.safer.mavlink;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * TODO: add documentation here
 */

public class FileHandler {

    private final String fileName;
    private final File myFile;
    private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);
    private String existingContent = "";
    private final DateTimeFormatter dateAndTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public FileHandler(String fileName){
        this.fileName = fileName;
        this.myFile = new File(fileName + ".json");

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

    public void checkContent(){
        try {
            StringBuilder content = new StringBuilder();
            Scanner myScanner = new Scanner(myFile);

            while(myScanner.hasNextLine()){
                String lineFromFile = myScanner.nextLine();
                content.append(lineFromFile);
            }
            existingContent = content.toString();
            myScanner.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void save(String message){
        if (!existingContent.isEmpty()){
            try {
                JSONObject dataFromFile = new JSONObject(existingContent);
                JSONObject newObjFromMessage = new JSONObject(message);
                dataFromFile.append(LocalDateTime.now().format(dateAndTime), newObjFromMessage);
                FileWriter myWriter = new FileWriter(fileName + ".json");

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
                JSONObject jsonContainer = new JSONObject();
                JSONObject firstObject = new JSONObject(message);
                FileWriter myWriter = new FileWriter(fileName + ".json");

                jsonContainer.append(LocalDateTime.now().format(dateAndTime),firstObject);
                existingContent = jsonContainer.toString();
                myWriter.write(jsonContainer.toString());
                myWriter.close();
                logger.info("file was empty -- data added successfully");
            } catch (IOException e){
                e.printStackTrace();
                logger.info("file was empty -- failed to write to the file");
            }
        }
    }
}