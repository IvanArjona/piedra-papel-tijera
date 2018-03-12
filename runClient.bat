set /p nombre=Introduce tu nombre:
mvn exec:java -Dexec.mainClass="es.ubu.lsi.client.GameClientImpl" -Dexec.args="localhost %nombre%"