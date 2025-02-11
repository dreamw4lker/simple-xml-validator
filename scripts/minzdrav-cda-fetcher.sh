#!/bin/bash

# Внутренние коды типов протоколов
PROTOCOL_TYPES=(
"LAB"
"CITOL"
"MBIO"
"CONSULT"
"HTI"
)

# Внешние OIDs типов протоколов
PROTOCOL_OIDS=(
"1.2.643.5.1.13.13.15.18"
"1.2.643.5.1.13.13.15.20"
"1.2.643.5.1.13.13.15.120"
"1.2.643.5.1.13.13.15.13"
"1.2.643.5.1.13.13.15.19"
)

# Версии типов протоколов, которые требуется выгрузить
LAB_VERSIONS=(4 5)
CITOL_VERSIONS=(1 2)
MBIO_VERSIONS=(1)
CONSULT_VERSIONS=(5)
HTI_VERSIONS=(2)

# Начало выгрузки
for index in ${!PROTOCOL_TYPES[*]}
do
  # Выгрузка репозитория одного протокола
  git clone "https://$1:$2@git.minzdrav.gov.ru/semd/${PROTOCOL_OIDS[$index]}.git"
  cd ${PROTOCOL_OIDS[$index]}

  # Чтение массива версий одного протокола
  PROTOCOL_VERSIONS_ARR="${PROTOCOL_TYPES[$index]}_VERSIONS"[*]

  # Для каждой версии:
  for version in ${!PROTOCOL_VERSIONS_ARR}
  do
    # Делаем checkout ветки (одна ветка - одна версия протокола)
    PROTOCOL_VERSION="${PROTOCOL_TYPES[$index]}_V$version"
    git checkout "${PROTOCOL_OIDS[$index]}.$version"

    # Обработка папок с XSD-файлами
    for folder in ./xsd/*;
    do
      XSD_FOLDER="$(basename "$folder")"
      mkdir -p "../$PROTOCOL_VERSION/xsd"
      # Папка "XSD CDA" есть всегда, другие папки - везде, кроме устаревших протоколов LAB_V4 и CITOL_V1
      if [ "$XSD_FOLDER" == "XSD CDA" ]; then
        cp -R "$folder" "../$PROTOCOL_VERSION/xsd/XSD_CDA"
      else
        if [ "${PROTOCOL_VERSION}" == "LAB_V4" ] || [ "${PROTOCOL_VERSION}" == "CITOL_V1" ]; then
          continue
        fi
        cp -R "$folder" "../$PROTOCOL_VERSION/xsd/XSD_CDA_$PROTOCOL_VERSION"
      fi
    done

    # Schematron-файлы отсутствуют у устаревших протоколов LAB_V4 и CITOL_V1
    if [ "${PROTOCOL_VERSION}" == "LAB_V4" ] || [ "${PROTOCOL_VERSION}" == "CITOL_V1" ]; then
      continue
    fi

    # Обработка Schematron-файлов:
    # при копировании сам файл переименовывается в schematron.sch, а рядом создаётся .txt-файл с оригинальным именем
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

  # Возвращаемся в стартовую папку для выгрузки нового репозитория
  cd ".."
done
