#!/usr/bin/env bash

BASEDIR=$(cd `dirname $0`; pwd)

CRONNER_HOME=$BASEDIR/..

LIB_HOME=$CRONNER_HOME/lib

CONF_FILE=$CRONNER_HOME/conf/cronner.conf
. $CONF_FILE

JAR_FILE=$LIB_HOME/cronner-manager-1.0.1.jar

PID_FILE=$CRONNER_HOME/cronner.pid

# JAVA_OPTS
JAVA_OPTS="-server -Duser.dir=$BASEDIR -Dcronner.logPath=$LOG_PATH"
JAVA_OPTS="${JAVA_OPTS} $JAVA_HEAP_OPTS"
JAVA_OPTS="${JAVA_OPTS} -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:HeapDumpPath=$LOG_PATH -Xloggc:$LOG_PATH/gc.log"

# CONFIG_OPTS
CONFIG_OPTS="--server.address=$BIND_ADDR --server.port=$LISTEN_PORT"
CONFIG_OPTS="$CONFIG_OPTS --cronner.user=$CRONNER_USER --cronner.pass=$CRONNER_PASS"
CONFIG_OPTS="$CONFIG_OPTS --datasource.driver=$DATASOURCE_DRIVER --datasource.url=$DATASOURCE_URL --datasource.user=$DATASOURCE_USER --datasource.password=$DATASOURCE_PASSWORD --datasource.maximumPoolSize=$DATASOURCE_MAXIMUMPOOLSIZE --datasource.connectionTimeout=$DATASOURCE_CONNECTIONTIMEOUT --datasource.autoCommit=$DATASOURCE_AUTOCOMMIT"
CONFIG_OPTS="$CONFIG_OPTS --zookeeper.serverLists=$ZOOKEEPER_SERVERLISTS --zookeeper.namespace=$ZOOKEEPER_NAMESPACE --zookeeper.baseSleepTimeMilliseconds=$ZOOKEEPER_BASESLEEPTIMEMILLISECONDS --zookeeper.maxSleepTimeMilliseconds=$ZOOKEEPER_MAXSLEEPTIMEMILLISECONDS --zookeeper.maxRetries=$ZOOKEEPER_MAXRETRIES"


function start()
{
    java $JAVA_OPTS -jar $JAR_FILE $CONFIG_OPTS $1 > /dev/null 2>&1 &
    echo $! > $PID_FILE
}

function stop()
{
    pid=`cat $PID_FILE`
    echo "kill $pid ..."
    kill $pid
    rm -f $PID_FILE
}

args=($@)

case "$1" in

    'start')
        start
        ;;

    'stop')
        stop
        ;;

    'restart')
        stop
        start
        ;;

    'help')
        help $2
        ;;
    *)
        echo "Usage: $0 { start | stop | restart | help }"
        exit 1
        ;;
esac