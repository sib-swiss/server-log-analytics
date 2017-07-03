#!/bin/bash

module add Development/java_jdk/1.8.0_112;
JAVA_HOME=/software/Development/java_jdk/1.8.0_112/
SPARK_HOME=$HOME/spark #On home directory

EXEC_MEM=50g
DRIVER_MEM=50g
EXTRA_JARS=lib/moultingyaml_2.11-0.4.0.jar,lib/snakeyaml-1.18.jar

CMD="$SPARK_HOME/bin/spark-shell --executor-memory $EXEC_MEM --driver-memory $DRIVER_MEM --jars $EXTRA_JARS,target/scala-2.11/server-log-analytics_2.11-1.0.jar"
CONFIG_FILE=${1:-default-config.yaml}
CONFIG="--conf spark.driver.extraJavaOptions=\"-Dconfig.file=$CONFIG_FILE\""

echo "---"
echo "Using config file ${CONFIG_FILE}"
echo "---"

PS3='Please choose your action: '
options=("Convert Parquet" "Insights Report" "Option 3" "Quit")
select opt in "${options[@]}"
do
    case $opt in
        "Convert Parquet")
            echo "Executing ${CMD}"
            $CMD -i scripts/convert-logs-to-parquet-format.scala $CONFIG
            ;;
        "Insights Report")
            echo "Executing ${CMD}"
            $CMD -i scripts/generate-insights-report.scala $CONFIG
            ;;
        "Option 3")
            echo "you chose choice 3"
            ;;
        "Quit")
            break
            ;;
        *) echo invalid option;;
    esac
done
