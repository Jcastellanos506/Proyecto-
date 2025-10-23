# Score APP (Swing)
Java 17 + Maven. GUI Swing + CLI, validaciones, polimorfismo, persistencia CSV y gráfico.

## Ejecutar
```bash
mvn clean package
java -jar target/score-app-1.0.0.jar
```
CLI:
```bash
mvn -q -DskipTests exec:java -Dexec.mainClass=com.scoreapp.cli.MainCLI
```
