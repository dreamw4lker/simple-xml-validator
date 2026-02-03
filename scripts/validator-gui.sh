#!/bin/bash

PROPERTIES_FILENAME=application-$(date +%Y%m%d_%H%M%S_%N).properties
CURRENT_YEAR=$(date +%Y)
LICENSE="MIT License

Copyright (c) 2024-${CURRENT_YEAR} Alexander Shkirkov

Please refer to https://github.com/dreamw4lker/simple-xml-validator/blob/main/LICENSE to see the full license text."

# Accept license
if ! (whiptail --title "Simple XML validator" --yes-button="Принять" --no-button "Отмена" --yesno "$LICENSE" 15 80) then
  exit 1
fi

# Input file name
FILENAME=$(whiptail --title  "Путь до XML-файла" --inputbox "Путь до проверяемого XML-файла" 10 60 document.xml 3>&1 1>&2 2>&3)
EXIT_STATUS=$?
if [ $EXIT_STATUS != 0 ]; then
  exit 1
fi

# Validation protocol
PROTOCOL_TYPE=$(whiptail --title  "Протокол валидации" --radiolist \
"Выберите протокол валидации" 15 80 9 \
"LAB_V4" "Протокол лабораторного исследования (v4)" OFF \
"LAB_V5" "Протокол лабораторного исследования (v5)" ON \
"CITOL_V1" "Протокол цитологического исследования (v1)" OFF \
"CITOL_V2" "Протокол цитологического исследования (v2)" OFF \
"CITOL_V3" "Протокол цитологического исследования (v3)" OFF \
"MBIO_V1" "Заключение по результатам МБИО-исследования (v1)" OFF \
"CONSULT_V5" "Протокол консультации (v5)" OFF \
"CTA_V2" "Справка о результатах ХТИ (v2)" OFF \
"PATAN_V3" "Протокол прижизн. патологоанатом. иссл. (v3)" OFF \
"OTHER" "Другой" OFF 3>&1 1>&2 2>&3)
EXIT_STATUS=$?
if [ $EXIT_STATUS != 0 ]; then
  exit 1
fi

# Validation type
if [ "$PROTOCOL_TYPE" == "LAB_V4" ] || [ "$PROTOCOL_TYPE" == "CITOL_V1" ]; then
  VALIDATION_TYPE="XSD"
else
  VALIDATION_TYPE=$(whiptail --title  "Тип валидации" --radiolist \
  "Выберите тип валидации" 15 60 4 \
  "XSD" "XSD" OFF \
  "SCHEMATRON" "Schematron" OFF \
  "ALL" "XSD и Schematron" ON 3>&1 1>&2 2>&3)
  EXIT_STATUS=$?
  if [ $EXIT_STATUS != 0 ]; then
    exit 1
  fi
fi

# Extra parameters for OTHER protocol type
if [ "$PROTOCOL_TYPE" = "OTHER" ]; then
  if [ "$VALIDATION_TYPE" == "XSD" ] || [ "$VALIDATION_TYPE" == "ALL" ]; then
    XSD_FILENAME_1=$(whiptail --title  "Путь до XSD-файла" --inputbox "Путь до главного файла XSD-схемы" 10 60 CDA.xsd 3>&1 1>&2 2>&3)
    EXIT_STATUS=$?
    if [ $EXIT_STATUS != 0 ]; then
      exit 1
    fi
  fi

  if [ "$VALIDATION_TYPE" == "SCHEMATRON" ] || [ "$VALIDATION_TYPE" == "ALL" ]; then
    SCHEMATRON_FILENAME=$(whiptail --title  "Путь до Schematron-файла" --inputbox "Путь до Schematron-файла" 10 60 schematron.sch 3>&1 1>&2 2>&3)
    EXIT_STATUS=$?
    if [ $EXIT_STATUS != 0 ]; then
      exit 1
    fi
  fi
fi

# Save application.properties file?
whiptail --title "Сохранение введённых параметров" --yes-button="Да, сохранить" --no-button "Нет, не сохранять" --yesno "Сохранить введённые параметры в файл?" --defaultno 10 60 3>&1 1>&2 2>&3
SAVE_APPLICATION_PROPERTIES=$?

# Prepare application.properties file:
XSD_FILENAME_1=$PWD/$PROTOCOL_TYPE/xsd/XSD_CDA/CDA.xsd
if [ "$PROTOCOL_TYPE" != "LAB_V4" ] && [ "$PROTOCOL_TYPE" != "CITOL_V1" ] && [ "$PROTOCOL_TYPE" != "OTHER" ]; then
  XSD_FILENAME_2=$PWD/$PROTOCOL_TYPE/xsd/XSD_CDA_$PROTOCOL_TYPE/CDA.xsd
fi
if [ "$PROTOCOL_TYPE" == "LAB_V4" ] || [ "$PROTOCOL_TYPE" == "CITOL_V1" ]; then
  SCHEMATRON_FILENAME=
elif [ "$PROTOCOL_TYPE" != "OTHER" ]; then
  SCHEMATRON_FILENAME=$PWD/$PROTOCOL_TYPE/schematron/schematron.sch
fi

echo "# Files to validate
validator.input.xml.1=$FILENAME
# XSD schema main files
validator.input.xsd.1=$XSD_FILENAME_1" > "$PROPERTIES_FILENAME"
if [ "$PROTOCOL_TYPE" != "LAB_V4" ] && [ "$PROTOCOL_TYPE" != "CITOL_V1" ] && [ "$PROTOCOL_TYPE" != "OTHER" ]; then
  echo "validator.input.xsd.2=$XSD_FILENAME_2" >> "$PROPERTIES_FILENAME"
fi
echo "# Schematron files
validator.input.schematron.1=$SCHEMATRON_FILENAME
# Available validation modes: XSD, SCHEMATRON, ALL
validator.mode=$VALIDATION_TYPE
# Clear XML namespace on Schematron validation (if needed)
validator.input.xml.clear-xml-namespace-on-schematron-validation=true
" >> "$PROPERTIES_FILENAME"

# Run validator with parameters
java -jar -Dapplication.properties="$PROPERTIES_FILENAME" *.jar

# Remove application.properties file if necessary
if [ $SAVE_APPLICATION_PROPERTIES = 1 ]; then
  rm "$PROPERTIES_FILENAME"
else
  echo "Введённые параметры сохранены в файл $PROPERTIES_FILENAME
Для повторной проверки используйте команду:
java -jar -Dapplication.properties=$PROPERTIES_FILENAME *.jar"
fi
