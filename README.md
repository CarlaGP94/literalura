[Literalura]: Biblioteca Virtual (Gutendex & PostgreSQL)

## :bowtie: Desarrolladora:

- Carla Pasandi

# 📚 Descripción del Proyecto

Este proyecto es una aplicación de consola Java que simula una Biblioteca Virtual, permitiendo a los usuarios interactuar con datos de libros y autores. La aplicación inicializa su base de datos local con información obtenida de la API pública Gutendex, específicamente los primeros 320 libros (10 páginas), y luego opera exclusivamente sobre estos datos persistidos en una base de datos PostgreSQL.

El objetivo principal es proporcionar una interfaz sencilla para buscar, listar y filtrar libros y autores, demostrando la persistencia de datos y el manejo de excepciones en una aplicación Spring Boot.

# ✨ Características Principales

- **Población de Base de Datos:** Al iniciar por primera vez, la aplicación consume las primeras 10 páginas de la API de Gutendex (aproximadamente 320 libros) y guarda los datos de libros y autores en una base de datos PostgreSQL local.
- **Búsqueda de Libros por Título:** Permite al usuario buscar libros registrados por una cadena de texto en su título (insensible a mayúsculas/minúsculas).
- **Listado de Libros Registrados:** Muestra todos los libros guardados en la base de datos local.
- **Listado de Autores Registrados:** Muestra todos los autores guardados en la base de datos local.
- **Búsqueda de Autores por Período de Vida:** Filtra autores que estuvieron vivos en un rango de años específico.
- **Búsqueda de Libros por Idioma:** Permite buscar libros por un idioma específico, utilizando códigos ISO 639-1 (ej. "en", "es", "fr") o incluso un mapeo a partir de nombres completos o truncados del idioma.
- **Persistencia de Datos:** Utiliza Spring Data JPA para la gestión de entidades y la persistencia de datos en PostgreSQL.
- **Manejo de Errores Robustos:** Implementa try-catch para gestionar entradas de usuario inválidas y asegurar la estabilidad de la aplicación.

# 🛠️ Tecnologías Utilizadas

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL (como base de datos relacional)
- Maven (para gestión de dependencias y construcción del proyecto)
- Gutendex API (para la obtención inicial de datos)

# 🚀 Prerequisitos

Antes de ejecutar la aplicación, asegúrate de tener instalado lo siguiente:

- Java Development Kit (JDK) 17 o superior
- Maven
- PostgreSQL: Necesitarás tener un servidor PostgreSQL en funcionamiento y una base de datos creada.

# ⚙️ Configuración y Ejecución

Sigue estos pasos para poner en marcha el proyecto en tu máquina local:

## 1. Clonar el Repositorio

- Bash:

git clone [[Repositorio](https://github.com/CarlaGP94/literalura.git)]

cd [literalura]

## 2. Configuración de la Base de Datos PostgreSQL
   
- Crea una base de datos: 

Abre tu cliente de PostgreSQL (pgAdmin, psql, DBeaver, etc.) y crea una nueva base de datos. Por ejemplo:

SQL:
CREATE DATABASE biblioteca_db;

- Configura src/main/resources/application.properties :

(Abre el archivo y asegúrate de que la configuración de la base de datos coincida con la tuya).

Properties:

- Configuración de PostgreSQL
  
spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca_db

spring.datasource.username=tu_usuario_postgres

spring.datasource.password=tu_contraseña_postgres

spring.datasource.driver-class-name=org.postgresql.Driver

- Configuración de JPA/Hibernate
  
spring.jpa.hibernate.ddl-auto=update # 'update' para que Hibernate cree/actualice las tablas

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

- Importante:
  
La primera vez que ejecutes la aplicación, spring.jpa.hibernate.ddl-auto=update creará las tablas book y author automáticamente. Una vez que las tablas existan y la base de datos esté poblada, podrías considerar cambiarlo a none o validate para evitar cambios accidentales en el esquema.

## 3. Construir el Proyecto

Navega a la raíz del proyecto en tu terminal y ejecuta Maven para construir el proyecto:

Bash

mvn clean install

## 4. Ejecutar la Aplicación

Puedes ejecutar la aplicación de varias maneras:

- Desde la terminal (usando Maven):

Bash:

mvn spring-boot:run

- Desde tu IDE (IntelliJ IDEA, Eclipse, VS Code):

Abre el proyecto en tu IDE favorito y ejecuta la clase principal de Spring Boot (la que contiene el método main).

# 🖥️ Uso de la Aplicación

Una vez que la aplicación se inicie, verás un menú en la consola. Sigue las instrucciones interactivas:

¡Bienvenido a tu biblioteca virtual!

Ingresa una opción:

[1] Buscar libro por título.

[2] Listar libros registrados.

[3] Listar autores registrados.

[4] Explora autores que estaban activos en...

[5] Listar libros por idioma.


[0] Salir

- Población Inicial: La primera vez que la aplicación se ejecute, consumirá la API de Gutendex y poblará la base de datos. Esto puede tomar unos segundos.
- Interacción: Simplemente ingresa el número correspondiente a la opción deseada y presiona Enter. La aplicación te guiará con las entradas necesarias (título, años, idioma).
- Manejo de Errores: Si ingresas una opción no numérica o inválida, la aplicación te lo notificará y te pedirá que intentes de nuevo.

# 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si encuentras un bug, tienes una idea para una mejora o quieres añadir una nueva característica, por favor:

- Haz un "fork" del repositorio.
- Crea una nueva rama (git checkout -b feature/nueva-caracteristica).
- Realiza tus cambios y haz "commit" (git commit -m 'feat: añade nueva característica X').
- Haz "push" a tu rama (git push origin feature/nueva-caracteristica).
- Abre un "Pull Request" explicando tus cambios.

# 📄 Licencia

*Este proyecto está bajo la licencia MIT License.*
