# Aluraflix-Java-MongoDB
Repositório sobre o proteto do Challenge Alura Back End em Java com MongoDB

## API no ar
[Aluraflix](https://lr-aluraflix.herokuapp.com/start)

## Ferramentas
- Java 11
- Spring Boot
- MongoDB
- Autenticação JWT com Spring Security

## Executar Localmente - Configurar IDE Intellij

- Set gradle version (6.9)
    - File >> Build, Execution, Deployment >> Build Tools >> Gradle
        - **Gradle JVM**: JDK 11
        - **Use Gradle from**: 'Specified location' passando o diretório de instalação do gradle
- JDK 11
    - Project Settings >> Project
        - **Project SDK**: JDK 11
- Edit configurations..
    - '+' Add new configuration >> **Application**
        - **VM options**
            ```
            -Dspring.profiles.active=dev
            -Dmongodb_uri=mongodb://SUA_URI_DO_MONGO
            ```

