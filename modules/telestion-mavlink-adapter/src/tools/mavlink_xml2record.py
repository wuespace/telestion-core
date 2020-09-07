import os
import sys
import traceback
import xml.etree.ElementTree as ET
from typing import List, Tuple


class Type:
    def __init__(self, name, size, java_repr):
        self._name = name
        self._size = size
        self._java_repr = java_repr

    def get_name(self):
        return self._name

    def get_size(self):
        return self._size

    def get_java_repr(self):
        return self._java_repr


class Types:
    UINT8_T = Type("uint8_t", 1, "UINT_8")
    UINT16_T = Type("uint16_t", 2, "UINT_16")
    UINT32_T = Type("uint32_t", 4, "UINT_32")
    UINT64_T = Type("uint64_t", 8, "UINT_64")
    INT8_T = Type("int8_t", 1, "INT_8")
    INT16_T = Type("int16_t", 2, "INT_16")
    INT32_T = Type("int32_t", 4, "INT_32")
    INT64_T = Type("int64_t", 8, "INT_64")
    FLOAT = Type("float", 4, "FLOAT")
    DOUBLE = Type("double", 8, "DOUBLE")
    CHAR = Type("char", 1, "CHAR")

    @classmethod
    def get_from_string(cls, name):
        if "uint8_t" in name:
            return cls.UINT8_T
        elif "uint16_t" in name:
            return cls.UINT16_T
        elif "uint32_t" in name:
            return cls.UINT32_T
        elif "uint64_t" in name:
            return cls.UINT64_T
        elif "int8_t" in name:
            return cls.INT8_T
        elif "int16_t" in name:
            return cls.INT16_T
        elif "int32_t" in name:
            return cls.INT32_T
        elif "int64_t" in name:
            return cls.INT64_T
        elif "float" in name:
            return cls.FLOAT
        elif "double" in name:
            return cls.DOUBLE
        elif "char" in name:
            return cls.CHAR
        else:
            print(name)


class Field:
    def __init__(self, mav_type: str, name: str, description: str):
        self._mav_type = Types.get_from_string(mav_type).get_name()
        if '[' in mav_type:
            self._mav_type += mav_type[mav_type.index('['):mav_type.index(']') + 1]
        self._name = name
        self._description = description

    def get_name(self) -> str:
        return self._name

    def get_mav_type(self) -> str:
        return self._mav_type

    def get_description(self) -> str:
        return self._description


class Message:
    def __init__(self, msg_id: int, name: str, description: str, wip: bool, *fields: Field):
        self._msg_id = msg_id
        self._name = name
        self._description = description
        self._wip = wip
        self._fields = sorted(fields, key=lambda k: -Types.get_from_string(k.get_mav_type()).get_size())

    @staticmethod
    def _calc_crc(data: int, current_crc: int) -> int:
        data ^= (current_crc & 0xff)
        data ^= (data << 4)
        data &= 0xff
        return (current_crc >> 8) ^ (data << 8) ^ (data << 3) ^ (data >> 4) & 0xffff

    def calc_crc_extra(self) -> int:
        array = lambda x: str(chr(int(x[x.index('[') + 1:x.index(']')]))) if '[' in x else ''
        s = f"{self._name} " \
            f"{''.join(x for x in [f'{Types.get_from_string(f.get_mav_type()).get_name()} {f.get_name()} {array(f.get_mav_type())}' for f in self._fields])}"

        current_crc = 0xffff

        for c in s:
            current_crc = Message._calc_crc(ord(c), current_crc)

        return (current_crc & 0xFF) ^ (current_crc >> 8)

    def get_msg_id(self) -> int:
        return self._msg_id

    def get_name(self) -> str:
        return self._name

    def get_description(self) -> str:
        return self._description

    def get_fields(self) -> List[Field]:
        return self._fields

    def get_wip(self) -> bool:
        return self._wip


def handle_args() -> Tuple[str, str]:
    args = sys.argv[1:]
    if len(args) == 0:
        return "", ""
    if args[0] in ['help', '-help', '-h']:
        print("Usage: 'python mavlink_xml2record.py -f <INPUT_FILE> -o <OUTPUT_PACKAGE [Not required]>'")
        print("This help is available with 'python mavlink_xml2record.py -h'")
        return "", ""

    pos = {s: (i + 1) for i, s in enumerate(args) if s in ['-f', '-o']}

    return args[pos.get("-f")] if "-f" in pos else "", args[pos.get("-o")] if "-o" in pos else ""


def get_messages(file: str) -> List[Message]:
    root = ET.parse(file).getroot()
    extension = False
    return [Message(msg.attrib['id'], msg.attrib['name'], msg.find('description').text,
                    msg.find('wip') is not None, *[Field(f.attrib['type'], f.attrib['name'], f.text) for f in msg
                                                   if f.tag == 'field' and not (
                extension := (f.tag == 'extension' or extension))]) for msg in
            root.find('messages')]


def to_record(msg: Message, out_package: str = "org.telestion.adapter.mavlink.message"):
    name = msg.get_name().lower().replace("_", " ").title().replace(" ", "")
    new_line = '\n'
    template = f"""package {out_package};

import org.telestion.adapter.mavlink.annotation.MavField;
import org.telestion.adapter.mavlink.annotation.MavInfo;
import org.telestion.adapter.mavlink.annotation.NativeType;\
{f'{new_line}import org.telestion.adapter.mavlink.annotation.MavArray;'}

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {msg.get_description()}</br>
 * </br>
 * <i>Autogenerated by XML2Record-Tool v{VERSION}</i>
 * 
 * @author Autogenerated by XML2Record-Tool (by Cedric Boes)
 * @version 1.0 (autogenerated)\
{f'{new_line} * @deprecated This is still WIP and might change!' if msg.get_wip() else ''}
 */{f'{new_line}@Deprecated' if msg.get_wip() else ''}
@MavInfo(id = {msg.get_msg_id()}, crc = {msg.calc_crc_extra()})
@SuppressWarnings("preview")
public record {name}(/*TEMPLATE_RECORD_TYPES*/) implements MavlinkMessage {{
    /**
     * There shall be no default-constructor for normal developers.
     */
    @SuppressWarnings("unused")
    private {name}() {{
        this(/*TEMPLATE_CONSTRUCTOR_ARGS*/);
    }}
}}
    """
    no_array = True
    for field in msg.get_fields():
        data_type = "int"
        array = None
        if "[" in field.get_mav_type():
            array = field.get_mav_type()[field.get_mav_type().index("["):]
            no_array = False
        if field.get_mav_type() in [Types.INT64_T.get_name(), Types.UINT32_T.get_name(), Types.UINT64_T.get_name()]:
            data_type = "long"
        elif field.get_mav_type() in [Types.FLOAT.get_name(), Types.DOUBLE.get_name()]:
            data_type = "double"
        elif field.get_mav_type() == Types.CHAR.get_name():
            data_type = "char"
        array_length = -1
        if array is not None:
            data_type += "[]"
            array_length = int(
                field.get_mav_type()[field.get_mav_type().index("[") + 1:field.get_mav_type().index("]")])
        template = template.replace("/*TEMPLATE_RECORD_TYPES*/", f"""
        /**
         * {field.get_description()}</br>
         * </br>
         * <i>Autogenerated by XML2Record-Tool v{VERSION}</i>
         */{f'{os.linesep}        @MavArray(length = {array_length})' if array_length > 0 else ""}
        @MavField(nativeType = NativeType.{Types.get_from_string(field.get_mav_type()).get_java_repr()})
        @JsonProperty {data_type} {field.get_name()[:1].lower()}\
{field.get_name().lower().replace('_', ' ').title()[1:].replace(' ', '')}, /*TEMPLATE_RECORD_TYPES*/""") \
            .replace("/*TEMPLATE_CONSTRUCTOR_ARGS*/", f"{'null' if array_length > 0 else '0'}\
, /*TEMPLATE_CONSTRUCTOR_ARGS*/")
    template = template.replace(", /*TEMPLATE_RECORD_TYPES*/", "").replace(", /*TEMPLATE_CONSTRUCTOR_ARGS*/", "")
    if no_array:
        template = template.replace(f'{new_line}import org.telestion.adapter.mavlink.annotation.MavArray;', '')

    path = f"{out_package.replace('.', '/')}/"

    if not os.path.exists(path):
        os.makedirs(path)

    with open(file=path + f"{name}.java", mode='w') as f:
        f.write(template)


def main():
    print("Starting MAVLink XML2Record-Tool")

    file, output = handle_args()
    if not (file == "" and output == ""):
        print(f"Reading and interpreting MAVLink-File {file}...")
        messages = get_messages(file)
        print(f"Reading and interpreting MAVLink-File finished [{len(messages)} valid messages found]")

        print("Outputting to Java-Records (preview jdk-14)...")
        for message in messages:
            print(f"Creating record for {message.get_name()} (id={message.get_msg_id()})... ", end='')
            try:
                if output != "":
                    to_record(message, output)
                else:
                    to_record(message)
                print("Success!")
            except Exception as e:
                print("Failed!")
                traceback.print_exc()
        print("All done!")
    else:
        print("No input file specified")

    print("Exiting MAVLink XML2Record-Tool")


VERSION = "1.0.0"

if __name__ == '__main__':
    main()
