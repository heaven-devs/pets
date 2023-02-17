#!/bin/bash
#configFile="$(dirname "$0")/$(basename "$0" .sh).conf"
configFile="$(dirname "$0")/src/main/resources/application.properties"
version=0.0.1
author=alrepin
declare -A params_key_value

function main() {
  readConf
  # If no arguments are provided
  [ $# -eq 0 ] && usage && exit

  while [ "$1" ]; do
    case "$1" in
    --create | -c) createdb && exit ;;
    --rm | -r) rmdb && exit ;;
    --settings | -s) settings && exit ;;
    --version | -v) echo "$version" && exit ;;
    --ls | -l) checkdb && exit ;;
    *[0-9]*)
      # If the user provides number as an argument,
      exit
      ;;
    -*) die "option '$1' does not exist" ;;
    esac
    shift
  done
  exit 0
}
function settings() {
  echo "${configFile}"
  echo PGHOST = "${PGHOST}"
  echo PGPORT = "${PGPORT}"
  echo DBNAME = "${DBNAME}"
  echo PGUSER = "${PGUSER}"
}

function rmdb() {
  settings
  result=$(psql "user=${PGUSER} password=${PGPASSWORD} host=${PGHOST} port=${PGPORT} dbname=postgres" -c "UPDATE pg_database SET datallowconn = 'false' WHERE datname = '${DBNAME}';")
  #  echo "$result"

  result=$(psql "user=${PGUSER} password=${PGPASSWORD} host=${PGHOST} port=${PGPORT} dbname=postgres" -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '${DBNAME}' AND pid <> pg_backend_pid();")
  #  echo "$result"

  result=$(psql "user=${PGUSER} password=${PGPASSWORD} host=${PGHOST} port=${PGPORT} dbname=postgres" -c "DROP DATABASE IF EXISTS ${DBNAME};")
  echo "$result"
  checkdb
}

function createdb() {
  settings
  result=$(psql "user=${PGUSER} password=${PGPASSWORD} host=${PGHOST} port=${PGPORT} dbname=postgres" -c "CREATE DATABASE ${DBNAME} WITH OWNER = ${PGUSER} ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;")
  echo "$result"
  checkdb
}

function usage() {
  # Using 'cat << EOF' we can easily output a multiline text. This is much
  # better than using 'echo' for each line or using '\n' to create a new line.
  echo Db create/force drop util by "${author}©"
  cat <<EOF
called with no option and no argument..
usage:
EOF

  echo "$(basename "$0") [-c] [-r] [-v] [-l]"
  cat <<EOF
-v | --version
        Show current script version;
-c | --create
        To create database, if not exist;
-r | --rm
        Force drop database
-s | --settings
        Show db settings from config file
-l | --ls
        Show info from postgres
EOF

}

function checkdb() {
  result=$(psql "user=${PGUSER} password=${PGPASSWORD} host=${PGHOST} port=${PGPORT} dbname=postgres" -l | head -n3)
  echo "$result"
  result=$(psql "user=${PGUSER} password=${PGPASSWORD} host=${PGHOST} port=${PGPORT} dbname=postgres" -l | grep "${PGUSER}")
  echo "$result"
}



function die() {
  printf "%b\n" "Error: $1" >&2
  exit 1
}

function readConf() {
  shopt -s extglob
  regex='^[a-z].*'
  #    regex='(^[a-z].*)=(.*)'
  while IFS='=' read -r lhs rhs; do
    if [[ $lhs =~ $regex ]]; then
      #          echo ${lhs//./_} and $rhs
      #            params_key_value[${lhs//./_}]=$rhs
      params_key_value[${lhs}]=$rhs
    fi
  done <"$configFile"
  regex='://(.*):(.*)/(.*)'
  if [[ "${params_key_value[spring.datasource.url]}" =~ $regex ]]; then
    export PGHOST="${BASH_REMATCH[1]}"
    export PGPORT="${BASH_REMATCH[2]}"
    export DBNAME="${BASH_REMATCH[3]}"
  fi
  export PGUSER="${params_key_value[spring.datasource.username]}"
  export PGPASSWORD="${params_key_value[spring.datasource.password]}"

  #for i in $(cat "$configFile");
  #do
  #  echo $i
  #    if [[ $i =~ $regex ]];
  #    then
  #        echo ok
  #        #${BASH_REMATCH[0]}
  #    fi
  #done

  #    for key in "${!params_key_value[@]}"; do
  #      #new2="${var//./_}"
  #        export "${key//./_}"="${params_key_value[${key//./_}]}"
  #
  #    done
}

main "$@"
exit 0
