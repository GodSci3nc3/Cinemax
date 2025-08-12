# Policine - Sistema de Reservas de Cine 

<img src = "https://github.com/user-attachments/assets/310148fd-adce-43a9-8165-26a8064e3cc2" style = "height: 700px;">

## Descripción del Proyecto

**Policine** es un sistema integral de reservas para cines desarrollado como proyecto final de la materia de Programación Orientada a Objetos (POO) en la Universidad Politécnica de Victoria. Este sistema permite gestionar múltiples sucursales, salas, funciones, reservas de asientos y venta de productos de dulcería de manera eficiente e intuitiva.

El proyecto fue desarrollado por un equipo comprometido con crear una solución moderna, funcional e inclusiva, considerando principios de UX/UI y accesibilidad para brindar la mejor experiencia de usuario posible.

## 🚀 Tecnologías Utilizadas

### Lenguajes y Frameworks
- **Java 17+** - Lenguaje principal de desarrollo
- **JavaFX** - Framework para interfaz de usuario moderna y responsiva
- **FXML** - Definición declarativa de interfaces
- **CSS** - Personalización y estilos avanzados

### Herramientas de Desarrollo
- **IntelliJ IDEA** - Entorno de desarrollo integrado
- **Scene Builder** - Diseño visual de interfaces FXML
- **Maven/Gradle** - Gestión de dependencias
- **Git** - Control de versiones

### Base de Datos
- **MySQL/PostgreSQL** - Sistema de gestión de base de datos relacional
- **JDBC** - Conectividad con base de datos

### Arquitectura
- **Patrón MVC** (Model-View-Controller) - Separación clara de responsabilidades
- **DAO Pattern** - Acceso eficiente a datos
- **Service Layer** - Lógica de negocio encapsulada

## ✨ Características Principales

### 🏢 Gestión de Cines
- Administración de múltiples sucursales
- Configuración de información de contacto y ubicación
- Control de estado activo/inactivo

### 🎭 Gestión de Salas y Asientos
- Configuración flexible de salas
- Diseño visual intuitivo de distribución de asientos
- Soporte para diferentes tipos de asientos (Normal, VIP, Discapacitado)
- Visualización en tiempo real de disponibilidad

### 🎪 Programación de Funciones
- Calendario visual para programación
- Gestión de horarios y precios
- Asignación automática de salas
- Control de capacidad y disponibilidad

### 🎫 Sistema de Reservas
- Selección interactiva de asientos
- Proceso de reserva paso a paso
- Validación en tiempo real
- Generación automática de tickets

### 🍿 Módulo de Dulcería
- Catálogo visual de productos
- Categorización (Bebidas, Dulces, Combos, Helados)
- Carrito de compras integrado
- Control de inventario en tiempo real

## 🎨 Enfoque en UX/UI

El desarrollo de Policine puso especial énfasis en la experiencia del usuario:

- **Diseño Intuitivo**: Interfaces que siguen patrones familiares y lógicos
- **Accesibilidad**: Consideraciones para usuarios con diferentes capacidades
- **Responsive Design**: Adaptabilidad a diferentes tamaños de pantalla
- **Feedback Visual**: Indicadores claros de estado y acciones
- **Navegación Fluida**: Transiciones suaves entre módulos

## 📊 Arquitectura del Sistema

```
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│    VIEW     │ │ CONTROLLER  │ │    MODEL    │
│  (JavaFX)   │◄──►│  (Service)  │◄──►│ (Entities)  │
│─────────────│ │─────────────│ │─────────────│
│ - FXML      │ │ - Logic     │ │ - DAO       │
│ - CSS       │ │ - Validation│ │ - Database  │
│ - Controllers│ │ - Business  │ │ - Entities  │
└─────────────┘ └─────────────┘ └─────────────┘
```

## 🗂️ Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── cinemax/
│   │           ├── model/
│   │           │   ├── entities/
│   │           │   └── dao/
│   │           ├── view/
│   │           │   ├── controllers/
│   │           │   └── components/
│   │           ├── controller/
│   │           ├── service/
│   │           ├── utils/
│   │           └── Main.java
│   └── resources/
│       ├── fxml/
│       ├── css/
│       └── images/
```

## 📋 Entidades Principales

- **Cine**: Gestión de sucursales
- **Sala**: Configuración de espacios
- **Asiento**: Control de disponibilidad
- **Función**: Programación de eventos
- **Reserva**: Gestión de ventas
- **Snack**: Productos de dulcería

## 🚀 Instalación y Configuración

### Prerrequisitos
- Java 17 o superior
- IntelliJ IDEA (recomendado)
- MySQL o PostgreSQL
- Maven o Gradle

### Pasos de Instalación
1. Clonar el repositorio
2. Configurar la base de datos
3. Actualizar las credenciales de conexión
4. Ejecutar las migraciones necesarias
5. Compilar y ejecutar el proyecto

## 👥 Equipo de Desarrollo

Este proyecto fue desarrollado como trabajo en equipo para la materia de Programación Orientada a Objetos en la Universidad Politécnica de Victoria, demostrando las mejores prácticas de desarrollo colaborativo y aplicación de conceptos de POO.

## 📚 Aprendizajes Clave

- Implementación práctica de patrones de diseño
- Desarrollo de interfaces gráficas modernas con JavaFX
- Gestión de bases de datos relacionales
- Trabajo colaborativo en proyectos de software
- Consideraciones de UX/UI en aplicaciones de escritorio

## 📝 Versión

**Versión 1.0** - Implementación inicial completa del sistema con todas las funcionalidades core.

## 🔮 Trabajo Futuro

- Integración con sistemas de pago
- Módulo web complementario
- Aplicación móvil
- Reportes avanzados y analytics
- Integración con APIs de películas

---

*Desarrollado con ❤️ como proyecto final de POO en la Universidad Politécnica de Victoria*
