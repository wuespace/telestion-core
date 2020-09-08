# Mavlink XML2Record Tool

## Description
This tool allows you to convert the Mavlink-Messages defined like
[this](https://github.com/mavlink/mavlink/tree/master/message_definitions/v1.0)
into a more practical format for vert.x - the **Java-Preview-Record**.

## Usage
The program can be started like a normal python script but with several
arguments.
- **-f:** &rightarrow; the next argument specifies the path<br>
_Note that if you use a windows terminal spaces in a path might not work_
_because they are interpreted as new arguments!_
- **-p:** &rightarrow; specifies the output-package _(default: org.telestion.adapter.mavlink.message)_
- **-o:** &rightarrow; defines the output-dir _(default: [package])_
- **-h:** &rightarrow; prints a help message

##### Example:
```shell
python mavlink_xml2record.py -f path/to/messages.xml
```

## TODO
Setup a GitHub action to automatically pull the messages from
[here](https://github.com/mavlink/mavlink/tree/master/message_definitions/v1.0)
if there is a new version and put them into mav_msgs (this dir).
After deleting all old messages (all messages in
_org.telestion.adapter.mavlink.message_ apart from _MavlinkMessage.java_ and _MessageIndex.java_) execute the script 
to create the messages from scratch.
