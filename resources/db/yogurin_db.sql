-- ============================================================
-- Base de datos: yogurin_db
-- Sistema de Gestion - Yogurin Bustamante
-- ============================================================

CREATE DATABASE IF NOT EXISTS yogurin_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE yogurin_db;

-- ------------------------------------------------------------
-- Tabla: usuarios
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    usuario  VARCHAR(50)  NOT NULL UNIQUE,
    clave    CHAR(32)     NOT NULL COMMENT 'Hash MD5',
    rol      ENUM('ADMIN','VENDEDOR') NOT NULL DEFAULT 'VENDEDOR',
    activo   TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT chk_rol CHECK (rol IN ('ADMIN','VENDEDOR'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla: clientes
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clientes (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    dni      CHAR(8)      NOT NULL UNIQUE,
    telefono VARCHAR(15)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla: productos
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS productos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100)   NOT NULL,
    precio      DECIMAL(10,2)  NOT NULL,
    stock       INT            NOT NULL DEFAULT 0,
    descripcion TEXT,
    activo      TINYINT(1)     NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla: insumos
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS insumos (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100)  NOT NULL,
    unidad       VARCHAR(20)   NOT NULL COMMENT 'kg, litros, unidades...',
    stock        DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock_minimo DECIMAL(10,2) NOT NULL DEFAULT 0,
    activo       TINYINT(1)    NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla: ventas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ventas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    fecha       DATETIME      NOT NULL DEFAULT NOW(),
    total       DECIMAL(10,2) NOT NULL,
    id_cliente  INT           NOT NULL,
    id_usuario  INT           NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla: detalle_ventas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS detalle_ventas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    id_venta    INT           NOT NULL,
    id_producto INT           NOT NULL,
    cantidad    INT           NOT NULL,
    subtotal    DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_venta)    REFERENCES ventas(id),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla: lotes_produccion
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS lotes_produccion (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    fecha       DATETIME NOT NULL DEFAULT NOW(),
    id_producto INT      NOT NULL,
    unidades    INT      NOT NULL,
    FOREIGN KEY (id_producto) REFERENCES productos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabla: lote_insumos (detalle de insumos por lote)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS lote_insumos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    id_lote     INT           NOT NULL,
    id_insumo   INT           NOT NULL,
    cantidad    DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_lote)   REFERENCES lotes_produccion(id),
    FOREIGN KEY (id_insumo) REFERENCES insumos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Datos iniciales
-- ============================================================

-- Usuario admin por defecto: admin / admin123 (MD5)
INSERT INTO usuarios (nombre, usuario, clave, rol) VALUES
('Administrador', 'admin', '0192023a7bbd73250516f069df18b500', 'ADMIN'),
('Vendedor Demo',  'vendedor', '0192023a7bbd73250516f069df18b500', 'VENDEDOR');

-- Productos de ejemplo
INSERT INTO productos (nombre, precio, stock, descripcion) VALUES
('Yogur Natural 1L',      5.50, 100, 'Yogur natural sin saborizantes'),
('Yogur de Fresa 500ml',  3.20,  80, 'Yogur con sabor a fresa'),
('Yogur de Durazno 1L',   5.80,  60, 'Yogur con sabor a durazno'),
('Yogur Griego 250g',     4.50,  40, 'Yogur griego espeso');

-- Insumos de ejemplo
INSERT INTO insumos (nombre, unidad, stock, stock_minimo) VALUES
('Leche entera',   'litros', 500.0, 100.0),
('Azucar',         'kg',      80.0,  20.0),
('Fermento lactico','gramos', 200.0,  50.0),
('Frutas (fresa)', 'kg',      30.0,  10.0),
('Envases 1L',     'unidades',200.0,  50.0);

-- Cliente de ejemplo
INSERT INTO clientes (nombre, dni, telefono) VALUES
('Consumidor Final', '00000000', '000000000');
