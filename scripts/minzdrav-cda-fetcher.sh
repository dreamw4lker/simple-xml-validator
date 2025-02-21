#!/bin/bash

# Internal protocol types
PROTOCOL_TYPES=(
"LAB"
"CITOL"
"MBIO"
"CONSULT"
"HTI"
"PATAN"
)

# External protocol OIDs
PROTOCOL_OIDS=(
"1.2.643.5.1.13.13.15.18"
"1.2.643.5.1.13.13.15.20"
"1.2.643.5.1.13.13.15.120"
"1.2.643.5.1.13.13.15.13"
"1.2.643.5.1.13.13.15.19"
"1.2.643.5.1.13.13.15.21"
)

# Protocol versions to download
LAB_VERSIONS=(4 5)
CITOL_VERSIONS=(1 2)
MBIO_VERSIONS=(1)
CONSULT_VERSIONS=(5)
HTI_VERSIONS=(2)
PATAN_VERSIONS=(3)

# Read parameters
if [ $# -eq 0 ] || [ $# -eq 1 ]; then
  echo "Укажите 2 параметра: логин и пароль от git.minzdrav.gov.ru"
  exit 1
fi

# Start downloading process
for index in ${!PROTOCOL_TYPES[*]}
do
  echo "[ Downloading ] Protocol: ${PROTOCOL_TYPES[$index]}, OID: ${PROTOCOL_OIDS[$index]}"

  # Clone one protocol repo
  git clone "https://$1:$2@git.minzdrav.gov.ru/semd/${PROTOCOL_OIDS[$index]}.git"
  cd ${PROTOCOL_OIDS[$index]}

  # Read an array of protocol versions
  PROTOCOL_VERSIONS_ARR="${PROTOCOL_TYPES[$index]}_VERSIONS"[*]

  # For each version:
  for version in ${!PROTOCOL_VERSIONS_ARR}
  do
    # Checkout branch (one branch - one protocol version)
    PROTOCOL_VERSION="${PROTOCOL_TYPES[$index]}_V$version"
    echo "[ Checkout ] to ${PROTOCOL_VERSION}"
    git checkout "${PROTOCOL_OIDS[$index]}.$version"

    # Proccessing XSD files folders
    for folder in ./xsd/*;
    do
      XSD_FOLDER="$(basename "$folder")"
      mkdir -p "../$PROTOCOL_VERSION/xsd"
      # Folder "XSD CDA" is always available, another one is available for each protocol except LAB_V4 and CITOL_V1
      if [ "$XSD_FOLDER" == "XSD CDA" ]; then
        cp -R "$folder" "../$PROTOCOL_VERSION/xsd/XSD_CDA"
      else
        if [ "${PROTOCOL_VERSION}" == "LAB_V4" ] || [ "${PROTOCOL_VERSION}" == "CITOL_V1" ]; then
          continue
        fi
        cp -R "$folder" "../$PROTOCOL_VERSION/xsd/XSD_CDA_$PROTOCOL_VERSION"
      fi
    done

    # Schematron files are not available for outdated protocols LAB_V4 and CITOL_V1
    if [ "${PROTOCOL_VERSION}" == "LAB_V4" ] || [ "${PROTOCOL_VERSION}" == "CITOL_V1" ]; then
      continue
    fi

    # Processing Schematron files:
    # Original schematron file will be renamed to "schematron.sch".
    # Additional .txt-file will be placed nearby with the original schematron filename
    SCH_FILE=$(find "./schematron/" -type f -name "*.sch" | head -n 1)
    if [ -z "$SCH_FILE" ]; then
      echo "Schematron-файл для $PROTOCOL_VERSION не найден"
    else
      mkdir -p "../$PROTOCOL_VERSION/schematron"
      BASENAME=$(basename "$SCH_FILE")
      if ! cp "$SCH_FILE" "../$PROTOCOL_VERSION/schematron/schematron.sch"; then
        echo "Ошибка при копировании Schematron-файла $PROTOCOL_VERSION"
      fi
      if ! touch "../$PROTOCOL_VERSION/schematron/$BASENAME.txt"; then
        echo "Ошибка при копировании файла с версией Schematron для $PROTOCOL_VERSION"
      fi
    fi
  done

  # Turn back to start folder to process another repo
  cd ".."
done
