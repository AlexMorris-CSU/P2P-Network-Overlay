DIR="$( cd "$( dirname "$0" )" && pwd )"
JAR_PATH="$DIR/conf/:$DIR/build/libs/Alex_Morris_HW1-1.0-SNAPSHOT.jar"
MACHINE_LIST="$DIR/src/main/resources/machine-list"
SCRIPT="java -cp $JAR_PATH cs455.overlay.node.MessagingNode <registry-host> <registry-port>"
COMMAND='gnome-terminal --geometry=200x40'
for machine in `cat $MACHINE_LIST`
do
 OPTION='--tab -t "'$machine'" -e "ssh -t '$machine' cd '$DIR'; echo '$SCRIPT'; '$SCRIPT'"'
 COMMAND+=" $OPTION"
done
eval $COMMAND &