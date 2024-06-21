import os
import shutil
import time
import sys
#copy build zip to machine agent folder

print(sys.argv)
if sys.argv[1] == '-w':
    #use workbench
    if os.path.exists('./machineagent-workbench-24.5.0/monitors/MqttMonitor'):
        shutil.rmtree('./machineagent-workbench-24.5.0/monitors/MqttMonitor')
    if os.path.exists('./machineagent-workbench-24.5.0/monitors/MqttMonitor-1.1.2.zip'):
        os.remove('./machineagent-workbench-24.5.0/monitors/MqttMonitor-1.1.2.zip')
    shutil.copy2('../target/MqttMonitor-1.1.2.zip','./machineagent-workbench-24.5.0/monitors')
    os.system('unzip ./machineagent-workbench-24.5.0/monitors/MqttMonitor-1.1.2.zip -d ./machineagent-workbench-24.5.0/monitors')
    os.remove('./machineagent-workbench-24.5.0/monitors/MqttMonitor/config.yml')
    shutil.copy2('./config.yml', './machineagent-workbench-24.5.0/monitors/MqttMonitor')
    os.system('java -jar machineagent-workbench-24.5.0/monitors/MqttMonitor/mqtt-monitoring-extension.jar')
if sys.argv[1] == '-c':
    shutil.copy('../target/MqttMonitor-1.1.2.zip','./machine_with_extension')
    with os.scandir('ma_logs') as entries:
        for entry in entries:
            if entry.is_file():
                os.unlink(entry.path)
            else:
                shutil.rmtree(entry.path)
    time.sleep(10)
    #probably should check if network exists and if not create
    os.system('docker network create jenkins || true')
    #maybe force build
    os.system('docker compose up --build')